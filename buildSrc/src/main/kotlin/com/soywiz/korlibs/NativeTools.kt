package com.soywiz.korlibs

import com.soywiz.korlibs.modules.allNativeTargets
import com.soywiz.korlibs.modules.configurePublishing
import com.soywiz.korlibs.modules.configureSigning
import com.soywiz.korlibs.modules.doEnableKotlinNative
import com.soywiz.korlibs.modules.isLinux
import com.soywiz.korlibs.modules.isWin
import com.soywiz.korlibs.modules.nativeTargets
import org.gradle.api.Project

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
    fun groovyConfigurePublishing(project: Project, multiplatform: Boolean) {
        project.configurePublishing(multiplatform = multiplatform)
    }

    @JvmStatic
    fun groovyConfigureSigning(project: Project) {
        project.configureSigning()
    }
}
