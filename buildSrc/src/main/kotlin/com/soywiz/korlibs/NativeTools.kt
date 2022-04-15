package com.soywiz.korlibs

import com.soywiz.korlibs.modules.*
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*

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
    fun configureCInterop(project: Project, name: String) {
        if (project.doEnableKotlinNative) {
            project.kotlin {
                for (target in nativeTargets(project)) {
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
}
