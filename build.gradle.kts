import java.net.URLClassLoader

plugins {
	java
	//kotlin("multiplatform") version "1.4-M2"
	//kotlin("multiplatform") version "1.4-M3"
    //kotlin("multiplatform") version "1.4.0-rc"
    kotlin("multiplatform")
}

allprojects {
	repositories {
		mavenCentral()
		maven("https://dl.bintray.com/kotlin/kotlin-eap")
		maven("https://kotlin.bintray.com/kotlinx")
	}
}

val kotlinVersion: String by project
val isKotlinDev = kotlinVersion.contains("-release")
val isKotlinEap = kotlinVersion.contains("-eap") || kotlinVersion.contains("-M") || kotlinVersion.contains("-rc")

allprojects {
	repositories {
		mavenCentral()
		jcenter()
		maven { url = uri("https://plugins.gradle.org/m2/") }
		if (isKotlinDev || isKotlinEap) {
			maven { url = uri("https://dl.bintray.com/kotlin/kotlin-eap") }
			maven { url = uri("https://dl.bintray.com/kotlin/kotlin-dev") }
		}
	}
}

val enableKotlinNative: String by project
val doEnableKotlinNative get() = enableKotlinNative == "true"

// Required by RC
kotlin {
    jvm { }
}

subprojects {
	apply(plugin = "kotlin-multiplatform")
	apply(plugin = "maven-publish")
    apply(plugin = "korlibs")
}

subprojects {

    fun getKorgeProcessResourcesTaskName(target: org.jetbrains.kotlin.gradle.plugin.KotlinTarget, compilation: org.jetbrains.kotlin.gradle.plugin.KotlinCompilation<*>): String {
        return "korgeProcessedResources${target.name.capitalize()}${compilation.name.capitalize()}"
    }

    if (project.path.startsWith(":samples:")) {
        // @TODO: Move to KorGE plugin
        project.tasks {
            val jvmMainClasses by getting
            val runJvm by creating(com.soywiz.korlibs.plugin.gradle.tasks.KorgeJavaExec::class) {
                group = "run"
                main = "MainKt"
            }
            val runJs by creating {
                group = "run"
                dependsOn("jsBrowserDevelopmentRun")
            }

            //val jsRun by creating { dependsOn("jsBrowserDevelopmentRun") } // Already available
            val jvmRun by creating {
                group = "run"
                dependsOn(runJvm)
            }
            //val run by getting(JavaExec::class)

            //val processResources by getting {
            //	dependsOn(processResourcesKorge)
            //}
        }

        kotlin {
            jvm {
            }
            js {
                browser {
                    binaries.executable()
                }
            }
            if (doEnableKotlinNative) {
                linuxX64 {
                    binaries {
                        executable {
                            entryPoint("entrypoint.main")
                        }
                    }
                }
                mingwX64 {
                    binaries {
                        executable {
                            entryPoint("entrypoint.main")
                        }
                    }
                }

                for (target in listOf(linuxX64(), mingwX64())) {
                    for (binary in target.binaries) {
                        val compilation = binary.compilation
                        val copyResourcesTask = tasks.create("copyResources${target.name.capitalize()}${binary.name.capitalize()}", Copy::class) {
                            dependsOn(getKorgeProcessResourcesTaskName(target, compilation))
                            group = "resources"
                            val isDebug = binary.buildType == org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType.DEBUG
                            val isTest = binary.outputKind == org.jetbrains.kotlin.gradle.plugin.mpp.NativeOutputKind.TEST
                            val compilation = if (isTest) target.compilations["test"] else target.compilations["main"]
                            //target.compilations.first().allKotlinSourceSets
                            val sourceSet = compilation.defaultSourceSet
                            from(sourceSet.resources)
                            from(sourceSet.dependsOn.map { it.resources })
                            into(binary.outputDirectory)
                        }

                        //compilation.compileKotlinTask.dependsOn(copyResourcesTask)
                        binary.linkTask.dependsOn(copyResourcesTask)
                    }
                }
            }
        }

        project.tasks {
            val runJvm by getting(com.soywiz.korlibs.plugin.gradle.tasks.KorgeJavaExec::class)
            val jvmMainClasses by getting(Task::class)

            //val prepareResourceProcessingClasses = create("prepareResourceProcessingClasses", Copy::class) {
            //    dependsOn(jvmMainClasses)
            //    afterEvaluate {
            //        from(runJvm.korgeClassPath.toList().map { if (it.extension == "jar") zipTree(it) else it })
            //    }
            //    into(File(project.buildDir, "korgeProcessedResources/classes"))
            //}

            for (target in kotlin.targets) {
                for (compilation in target.compilations) {
                    val processedResourcesFolder = File(project.buildDir, "korgeProcessedResources/${target.name}/${compilation.name}")
                    compilation.defaultSourceSet.resources.srcDir(processedResourcesFolder)
                    val korgeProcessedResources = create(getKorgeProcessResourcesTaskName(target, compilation)) {
                        //dependsOn(prepareResourceProcessingClasses)
                        dependsOn(jvmMainClasses)

                        doLast {
                            processedResourcesFolder.mkdirs()
                            //URLClassLoader(prepareResourceProcessingClasses.outputs.files.toList().map { it.toURL() }.toTypedArray(), ClassLoader.getSystemClassLoader()).use { classLoader ->
                            URLClassLoader(runJvm.korgeClassPath.toList().map { it.toURL() }.toTypedArray(), ClassLoader.getSystemClassLoader()).use { classLoader ->
                                val clazz = classLoader.loadClass("com.soywiz.korge.resources.ResourceProcessorRunner")
                                val folders = compilation.allKotlinSourceSets.flatMap { it.resources.srcDirs }.filter { it != processedResourcesFolder }.map { it.toString() }
                                //println(folders)
                                try {
                                    clazz.methods.first { it.name == "run" }.invoke(null, classLoader, folders, processedResourcesFolder.toString(), compilation.name)
                                } catch (e: java.lang.reflect.InvocationTargetException) {
                                    val re = (e.targetException ?: e)
                                    re.printStackTrace()
                                    System.err.println(re.toString())
                                }
                            }
                            System.gc()
                        }
                    }
                    //println(compilation.compileKotlinTask.name)
                    //println(compilation.compileKotlinTask.name)
                    //compilation.compileKotlinTask.finalizedBy(processResourcesKorge)
                    //println(compilation.compileKotlinTask)
                    //compilation.compileKotlinTask.dependsOn(processResourcesKorge)
                    if (compilation.compileKotlinTask.name != "compileKotlinJvm") {
                        compilation.compileKotlinTask.dependsOn(korgeProcessedResources)
                    } else {
                        compilation.compileKotlinTask.finalizedBy(korgeProcessedResources)
                        getByName("runJvm").dependsOn(korgeProcessedResources)

                    }
                    //println(compilation.output.allOutputs.toList())
                    //println("$target - $compilation")

                }
            }
        }

    }
}
