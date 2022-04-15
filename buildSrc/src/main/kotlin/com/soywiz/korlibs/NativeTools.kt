package com.soywiz.korlibs

import com.soywiz.korlibs.modules.*
import org.gradle.api.*

object NativeTools {
    @JvmStatic
    fun configureCInteropWin32(project: Project, name: String) {
        if (project.doEnableKotlinNative) {
            project.kotlin {
                for (target in nativeTargets(project)) {
                    if (target.isWin) {
                        target.compilations["main"].cinterops {
                            maybeCreate(name)
                        }
                    }
                }
            }
        }
    }


    @JvmStatic
    fun configureCInteropLinux(project: Project, name: String) {
        if (project.doEnableKotlinNative) {
            project.kotlin {
                for (target in nativeTargets(project)) {
                    if (target.isLinux) {
                        target.compilations["main"].cinterops {
                            maybeCreate(name)
                        }
                    }
                }
            }
        }
    }

    @JvmStatic
    fun configureAllCInterop(project: Project, name: String) {
        if (project.doEnableKotlinNative) {
            project.kotlin {
                for (target in allNativeTargets(project)) {
                    target.compilations["main"].cinterops {
                        maybeCreate(name)
                    }
                }
            }
        }
    }

    @JvmStatic
    fun configureAndroidDependency(project: Project, dep: Any) {
        project.dependencies {
            project.afterEvaluate {
                if (project.configurations.findByName("androidMainApi") != null) {
                    add("androidMainApi", dep)
                }
            }
        }
    }

    @JvmStatic
    fun configureExtraSourceSets(project: Project) {
        project.afterEvaluate {
            kotlin {
                for (targetName in listOf("linuxX64", "linuxArm32Hfp", "macosX64", "macosArm64")) {
                    //println("targetName=$targetName")
                    val target = targets.findByName(targetName) ?: continue
                    //println("target=$target")
                    val folder = project.file("src/${targetName}Main/kotlin")
                    //println(" - $folder")
                    target.compilations["main"].defaultSourceSet.kotlin.srcDir(folder)
                }
            }
        }
    }

    @JvmStatic
    fun groovyConfigurePublishing(project: Project, multiplatform: Boolean) {
        project.configurePublishing(multiplatform = multiplatform)
    }

    @JvmStatic
    fun groovyConfigureSigning(project: Project) {
        project.configureSigning()
    }
}
