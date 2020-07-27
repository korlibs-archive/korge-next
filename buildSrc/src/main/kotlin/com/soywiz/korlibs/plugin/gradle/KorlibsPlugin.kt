package com.soywiz.korlibs.plugin.gradle

import org.gradle.api.*

class KorlibsPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit {
        //TODO()

        target.group = "com.soywiz.korlibs.${target.name}"

        val doEnableKotlinNative = true

        println("Applying KorlibsPlugin...")

        target.kotlin.apply {
            jvm {
                compilations.all {
                    it.kotlinOptions {
                        jvmTarget = "1.8"
                    }
                }
            }
            js(org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType.IR) {
                //js(org.jetbrains.kotlin.gradle.plugin.KotlinJsCompilerType.LEGACY) {
                browser {
                    //binaries.executable()
                    /*
                    webpackTask {
                        cssSupport.enabled = true
                    }
                    runTask {
                        cssSupport.enabled = true
                    }
                    */
                    testTask {
                        useKarma {
                            useChromeHeadless()
                            //webpackConfig.cssSupport.enabled = true
                        }
                    }
                }
            }
            if (doEnableKotlinNative) {
                linuxX64()
                mingwX64()
            }
            // common
            //    js
            //    concurrent // non-js
            //      jvmAndroid
            //         android
            //         jvm
            //      native
            //         kotlin-native
            //    nonNative: [js, jvmAndroid]
            sourceSets.apply {
                val commonMain by getting {
                    dependencies {
                        implementation(kotlin("stdlib-common"))
                    }
                }
                val commonTest by getting {
                    dependencies {
                        implementation(kotlin("test-common"))
                        implementation(kotlin("test-annotations-common"))
                    }
                }

                val concurrentMain by creating {
                    dependsOn(commonMain)
                }
                val concurrentTest by creating {
                    dependsOn(commonTest)
                }

                val nonNativeCommonMain by creating {
                    dependsOn(commonMain)
                }
                val nonNativeCommonTest by creating {
                    dependsOn(commonTest)
                }

                val nonJsMain by creating {
                    dependsOn(commonMain)
                }
                val nonJsTest by creating {
                    dependsOn(commonTest)
                }

                val nonJvmMain by creating {
                    dependsOn(commonMain)
                }
                val nonJvmTest by creating {
                    dependsOn(commonTest)
                }

                val jvmAndroidMain by creating {
                    dependsOn(commonMain)
                }
                val jvmAndroidTest by creating {
                    dependsOn(commonTest)
                }

                // Default source set for JVM-specific sources and dependencies:
                val jvmMain by getting {
                    dependsOn(concurrentMain)
                    dependsOn(nonNativeCommonMain)
                    dependsOn(nonJsMain)
                    dependsOn(jvmAndroidMain)
                    dependencies {
                        implementation(kotlin("stdlib-jdk8"))
                    }
                }
                // JVM-specific tests and their dependencies:
                val jvmTest by getting {
                    dependsOn(concurrentTest)
                    dependsOn(nonNativeCommonTest)
                    dependsOn(nonJsTest)
                    dependsOn(jvmAndroidTest)
                    dependencies {
                        implementation(kotlin("test-junit"))
                    }
                }

                val jsMain by getting {
                    dependsOn(commonMain)
                    dependsOn(nonNativeCommonMain)
                    dependsOn(nonJvmMain)
                    dependencies {
                        implementation(kotlin("stdlib-js"))
                    }
                }
                val jsTest by getting {
                    dependsOn(commonTest)
                    dependsOn(nonNativeCommonTest)
                    dependsOn(nonJvmTest)
                    dependencies {
                        implementation(kotlin("test-js"))
                    }
                }

                if (doEnableKotlinNative) {
                    val nativeCommonMain by creating { dependsOn(concurrentMain) }
                    val nativeCommonTest by creating { dependsOn(concurrentTest) }

                    val nativePosixMain by creating { dependsOn(nativeCommonMain) }
                    val nativePosixTest by creating { dependsOn(nativeCommonTest) }

                    val nativePosixNonAppleMain by creating { dependsOn(nativePosixMain) }
                    val nativePosixNonAppleTest by creating { dependsOn(nativePosixTest) }

                    val linuxX64Main by getting {
                        dependsOn(commonMain)
                        dependsOn(nativeCommonMain)
                        dependsOn(nativePosixMain)
                        dependsOn(nativePosixNonAppleMain)
                        dependsOn(nonJvmMain)
                        dependsOn(nonJsMain)
                        dependencies {

                        }
                    }
                    val linuxX64Test by getting {
                        dependsOn(commonTest)
                        dependsOn(nativeCommonTest)
                        dependsOn(nativePosixTest)
                        dependsOn(nativePosixNonAppleTest)
                        dependsOn(nonJvmTest)
                        dependsOn(nonJsTest)
                        dependencies {
                        }
                    }

                    val mingwX64Main by getting {
                        dependsOn(commonMain)
                        dependsOn(nativeCommonMain)
                        dependsOn(nonJvmMain)
                        dependsOn(nonJsMain)
                        dependencies {

                        }
                    }
                    val mingwX64Test by getting {
                        dependsOn(commonTest)
                        dependsOn(nativeCommonTest)
                        dependsOn(nonJvmTest)
                        dependsOn(nonJsTest)
                        dependencies {
                        }
                    }
                }
            }

            Unit
        }
    }
}
