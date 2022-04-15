package com.soywiz.korge.gradle.targets.jvm

import com.soywiz.korge.gradle.*
import com.soywiz.korge.gradle.targets.*
import com.soywiz.korge.gradle.util.*
import org.gradle.api.*
import org.gradle.api.file.*
import org.gradle.api.tasks.*
import org.gradle.api.tasks.bundling.*
import org.gradle.api.tasks.testing.*
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.gradle.plugin.mpp.*
import proguard.*
import proguard.gradle.*

fun Project.configureJvm() {
    if (gkotlin.targets.findByName("jvm") != null) return

    val jvmTarget = gkotlin.jvm()
	gkotlin.targets.add(jvmTarget)

    project.korge.addDependency("jvmMainImplementation", "net.java.dev.jna:jna:$jnaVersion")
    project.korge.addDependency("jvmMainImplementation", "net.java.dev.jna:jna-platform:$jnaVersion")
	project.korge.addDependency("jvmMainImplementation", "org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    gkotlin.jvm {
        testRuns["test"].executionTask.configure {
            val it = this
            it.useJUnit()
            //it.useJUnitPlatform()
        }
    }
	project.tasks.withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).all {
        val it = this
        it.kotlinOptions {
            this.jvmTarget = korge.jvmTarget
        }
    }

	project.addTask<KorgeJavaExec>("runJvm", group = GROUP_KORGE) { task ->
		group = GROUP_KORGE_RUN
		dependsOn("jvmMainClasses")
		project.afterEvaluate {
			val beforeJava9 = System.getProperty("java.version").startsWith("1.")
		    if (!beforeJava9) task.jvmArgs(project.korge.javaAddOpens)
			task.mainClass.set(korge.realJvmMainClassName)
		}
	}

	project.afterEvaluate {
		for (entry in korge.extraEntryPoints) {
			project.addTask<KorgeJavaExec>("runJvm${entry.name.capitalize()}", group = GROUP_KORGE) { task ->
				group = GROUP_KORGE_RUN
				dependsOn("jvmMainClasses")
				task.mainClass.set(entry.jvmMainClassName)
			}
		}
	}

	for (jvmJar in project.getTasksByName("jvmJar", true)) {
		val jvmJar = (jvmJar as Jar)
		jvmJar.apply {
			entryCompression = ZipEntryCompression.STORED
		}
	}

	addProguard()
	configureJvmTest()

    val jvmProcessResources = tasks.findByName("jvmProcessResources") as? Copy?
    jvmProcessResources?.duplicatesStrategy = org.gradle.api.file.DuplicatesStrategy.INCLUDE
}

private val Project.jvmCompilation: NamedDomainObjectSet<*> get() = kotlin.targets.getByName("jvm").compilations as NamedDomainObjectSet<*>
private val Project.mainJvmCompilation: KotlinJvmCompilation get() = jvmCompilation.getByName("main") as org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmCompilation

open class KorgeJavaExec : JavaExec() {
    @get:InputFiles
    val korgeClassPath: FileCollection = run {
        val mainJvmCompilation = project.mainJvmCompilation
        listOf(
            mainJvmCompilation.runtimeDependencyFiles,
            mainJvmCompilation.compileDependencyFiles,
            mainJvmCompilation.output.allOutputs,
            mainJvmCompilation.output.classesDirs,
            project.files().from(project.getCompilationKorgeProcessedResourcesFolder(mainJvmCompilation))
        ).reduceRight { l, r -> l + r }
    }

    override fun exec() {
        val firstThread = firstThread
            ?: (
                System.getenv("KORGE_START_ON_FIRST_THREAD") == "true"
                    || System.getenv("KORGW_JVM_ENGINE") == "sdl"
                    //|| project.findProperty("korgw.jvm.engine") == "sdl"
                )

        if (firstThread && isMacos) {
            jvmArgs("-XstartOnFirstThread")
            //println("Executed jvmArgs(\"-XstartOnFirstThread\")")
        } else {
            //println("firstThread=$firstThread, isMacos=$isMacos")
        }
        classpath = korgeClassPath
        for (classPath in korgeClassPath.toList()) {
            logger.info("- $classPath")
        }
        super.exec()
    }

    @get:Input
    @Optional
    var firstThread: Boolean? = null

    init {
        systemProperties = (System.getProperties().toMutableMap() as MutableMap<String, Any>) - "java.awt.headless"
        defaultCharacterEncoding = Charsets.UTF_8.toString()
        // https://github.com/korlibs/korge-plugins/issues/25
    }
}

private fun Project.configureJvmTest() {
	val jvmTest = (tasks.findByName("jvmTest") as Test)
    jvmTest.classpath += project.files().from(project.getCompilationKorgeProcessedResourcesFolder(mainJvmCompilation))
	jvmTest.jvmArgs = (jvmTest.jvmArgs ?: listOf()) + listOf("-Djava.awt.headless=true")

    val jvmTestFix = tasks.create("jvmTestFix", Test::class.java) {
        val it = this
        it.group = "verification"
        it.environment("UPDATE_TEST_REF", "true")
        it.testClassesDirs = jvmTest.testClassesDirs
        it.classpath = jvmTest.classpath
        it.bootstrapClasspath = jvmTest.bootstrapClasspath
        it.systemProperty("java.awt.headless", "true")
    }
}

open class PatchedProGuardTask : ProGuardTask() {
    @Internal override fun getadaptresourcefilenames(): Any = super.getadaptresourcefilenames()
    @Internal override fun getadaptresourcefilecontents(): Any = super.getadaptresourcefilecontents()
    @Internal override fun getallowaccessmodification(): Any = super.getallowaccessmodification()
    @Internal override fun getaddconfigurationdebugging(): Any = super.getaddconfigurationdebugging()
    @Internal override fun getadaptclassstrings(): Any = super.getadaptclassstrings()
    @Internal override fun getdontoptimize(): Any = super.getdontoptimize()
    @Internal override fun getdontobfuscate(): Any = super.getdontobfuscate()
    @Internal override fun getdontwarn(): Any = super.getdontwarn()
    @Internal override fun getdontnote(): Any = super.getdontnote()
    @Internal override fun getConfigurationFiles(): MutableList<Any?> = super.getConfigurationFiles()
    @Internal override fun getandroid(): Any = super.getandroid()
    @Internal override fun getdontusemixedcaseclassnames(): Any = super.getdontusemixedcaseclassnames()
    @Internal override fun getdontskipnonpubliclibraryclassmembers(): Any = super.getdontskipnonpubliclibraryclassmembers()
    @Internal override fun getdontshrink(): Any = super.getdontshrink()
    @Internal override fun getdontpreverify(): Any = super.getdontpreverify()
    @Internal override fun getInJarCounts(): MutableList<Any?> = super.getInJarCounts()
    @Internal override fun getignorewarnings(): Any = super.getignorewarnings()
    @Internal override fun getflattenpackagehierarchy(): Any = super.getflattenpackagehierarchy()
    @Internal override fun getdump(): Any = super.getdump()
    @Internal override fun getkeepdirectories(): Any = super.getkeepdirectories()
    @Internal override fun getkeepattributes(): Any = super.getkeepattributes()
    @Internal override fun getInJarFiles(): MutableList<Any?> = super.getInJarFiles()
    @Internal override fun getInJarFilters(): MutableList<Any?> = super.getInJarFilters()
    @Internal override fun getforceprocessing(): Any = super.getforceprocessing()
    @Internal override fun getmergeinterfacesaggressively(): Any = super.getmergeinterfacesaggressively()
    @Internal override fun getLibraryJarFilters(): MutableList<Any?> = super.getLibraryJarFilters()
    @Internal override fun getLibraryJarFiles(): MutableList<Any?> = super.getLibraryJarFiles()
    @Internal override fun getskipnonpubliclibraryclasses(): Any = super.getskipnonpubliclibraryclasses()
    @Internal override fun getprintseeds(): Any = super.getprintseeds()
    @Internal override fun getprintusage(): Any = super.getprintusage()
    @Internal override fun getprintmapping(): Any = super.getprintmapping()
    @Internal override fun getoverloadaggressively(): Any = super.getoverloadaggressively()
    @Internal override fun getuseuniqueclassmembernames(): Any = super.getuseuniqueclassmembernames()
    @Internal override fun getkeeppackagenames(): Any = super.getkeeppackagenames()
    @Internal override fun getrepackageclasses(): Any = super.getrepackageclasses()
    @Internal override fun getkeepparameternames(): Any = super.getkeepparameternames()
    @Internal override fun getrenamesourcefileattribute(): Any = super.getrenamesourcefileattribute()
    @Internal override fun getmicroedition(): Any = super.getmicroedition()
    @Internal override fun getverbose(): Any = super.getverbose()
    @Internal override fun getprintconfiguration(): Any = super.getprintconfiguration()
    @Internal override fun getOutJarFiles(): MutableList<Any?> = super.getOutJarFiles()
    @Internal override fun getOutJarFilters(): MutableList<Any?> = super.getOutJarFilters()
}

private fun Project.addProguard() {
	// packageJvmFatJar
	val packageJvmFatJar = project.addTask<org.gradle.jvm.tasks.Jar>("packageJvmFatJar", group = GROUP_KORGE) { task ->
        task.archiveBaseName.set("${project.name}-all")
		task.group = GROUP_KORGE_PACKAGE
		task.exclude(
			"com/sun/jna/aix-ppc/**",
			"com/sun/jna/aix-ppc64/**",
			"com/sun/jna/freebsd-x86/**",
			"com/sun/jna/freebsd-x86-64/**",
			"com/sun/jna/linux-ppc/**",
			"com/sun/jna/linux-ppc64le/**",
			"com/sun/jna/linux-s390x/**",
			"com/sun/jna/linux-mips64el/**",
			"com/sun/jna/openbsd-x86/**",
			"com/sun/jna/openbsd-x86-64/**",
			"com/sun/jna/sunos-sparc/**",
			"com/sun/jna/sunos-sparcv9/**",
			"com/sun/jna/sunos-x86/**",
			"com/sun/jna/sunos-x86-64/**",
			"natives/macosx64/**",
            "natives/macosarm64/**",
            "META-INF/*.kotlin_module",
		)
        task.duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        task.doFirst {
            task.from(project.files().from(project.getCompilationKorgeProcessedResourcesFolder(mainJvmCompilation)))
        }
		project.afterEvaluate {
			task.manifest {
                val manifest = this
				manifest.attributes(
					mapOf(
						"Implementation-Title" to korge.realJvmMainClassName,
						"Implementation-Version" to project.version.toString(),
						"Main-Class" to korge.realJvmMainClassName
					)
				)
			}
			//it.from()
			//fileTree()
			task.from(GroovyClosure(project) {
				(project.gkotlin.targets["jvm"]["compilations"]["main"]["runtimeDependencyFiles"] as FileCollection).map { if (it.isDirectory) it else project.zipTree(it) as Any }
				//listOf<File>()
			})
			task.with(project.getTasksByName("jvmJar", true).first() as CopySpec)
		}
	}

	val runJvm = tasks.getByName("runJvm") as JavaExec

	project.addTask<PatchedProGuardTask>("packageJvmFatJarProguard", group = GROUP_KORGE, dependsOn = listOf(
		packageJvmFatJar
	)
	) { task ->
		task.group = GROUP_KORGE_PACKAGE
		project.afterEvaluate {
			task.libraryjars("${System.getProperty("java.home")}/lib/rt.jar")
			// Support newer java versions that doesn't have rt.jar
			task.libraryjars(project.fileTree("${System.getProperty("java.home")}/jmods/") {
                val it = this
                it.include("**/java.*.jmod")
			})
			//println(packageJvmFatJar.outputs.files.toList())
			task.injars(packageJvmFatJar.outputs.files.toList())
			task.outjars(buildDir["/libs/${project.name}-all-proguard.jar"])
			task.dontwarn()
			task.ignorewarnings()
			if (!project.korge.proguardObfuscate) {
				task.dontobfuscate()
			}
			task.assumenosideeffects("""
                class kotlin.jvm.internal.Intrinsics {
                    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
                }
            """.trimIndent())

			task.keepnames("class com.sun.jna.** { *; }")
			task.keepnames("class * extends com.sun.jna.** { *; }")
			//task.keepnames("class org.jcodec.** { *; }")
			task.keepattributes()
			task.keep("class * implements com.sun.jna.** { *; }")
			task.keep("class com.sun.jna.** { *; }")
			task.keep("class ${project.korge.realJvmMainClassName} { *; }")
			task.keep("class org.jcodec.** { *; }")

			if (runJvm.mainClass.get().isNotBlank()) {
				task.keep("""public class ${runJvm.mainClass.get()} { public static void main(java.lang.String[]); }""")
			}
		}

	}
}
