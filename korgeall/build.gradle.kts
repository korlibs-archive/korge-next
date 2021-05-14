// Kmem
val jcodecVersion: String by project
val jnaVersion: String by project
val coroutinesVersion: String by project

val enableKotlinNative: String by project
val doEnableKotlinNative get() = enableKotlinNative == "true"

val isWindows get() = org.apache.tools.ant.taskdefs.condition.Os.isFamily(org.apache.tools.ant.taskdefs.condition.Os.FAMILY_WINDOWS)
val isMacos get() = org.apache.tools.ant.taskdefs.condition.Os.isFamily(org.apache.tools.ant.taskdefs.condition.Os.FAMILY_MAC)
val hasAndroid = project.extensions.findByName("android") != null

fun org.jetbrains.kotlin.gradle.dsl.KotlinTargetContainerWithPresetFunctions.nativeTargets(): List<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
    return when {
        isWindows -> listOf(mingwX64())
        isMacos -> listOf(macosX64(), iosArm64(), iosX64())
        else -> listOf(linuxX64(), mingwX64(), macosX64(), iosArm64(), iosX64())
    }
}

if (doEnableKotlinNative) {
    kotlin {
        for (target in nativeTargets()) {
            target.compilations["main"].cinterops {
                maybeCreate("fastmem")
                maybeCreate("stb_image")
                maybeCreate("minimp3")
                maybeCreate("stb_vorbis")
                if (target.name == "mingwX64") maybeCreate("win32_winmm")
                if (target.name == "linuxX64") {
                    maybeCreate("linux_OpenAL")
                    maybeCreate("GL")
                }
            }
        }
    }
}

dependencies {
    add("jvmMainApi", "net.java.dev.jna:jna:$jnaVersion")
    add("jvmMainApi", "net.java.dev.jna:jna-platform:$jnaVersion")
    if (hasAndroid) {
        add("androidMainApi", "com.android.support:appcompat-v7:28.0.0")
    }

    add("commonTestApi", "it.krzeminski.vis-assert:vis-assert:0.4.0-beta")

    // Korvi
    add("jvmMainApi", "org.jcodec:jcodec:$jcodecVersion")

    // Korte
    add("jvmTestImplementation", "com.vladsch.flexmark:flexmark-all:0.61.6")

    add("commonMainApi", "org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
}
