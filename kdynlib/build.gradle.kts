plugins {
    id("com.google.devtools.ksp")
}

description = "Dynamic libraries for Kotlin"

val jnaVersion: String by project

val enableKotlinNative: String by project
val doEnableKotlinNative get() = enableKotlinNative == "true"

val enableKotlinRaspberryPi: String by project
val doEnableKotlinRaspberryPi get() = enableKotlinRaspberryPi == "true"

val isWindows get() = org.apache.tools.ant.taskdefs.condition.Os.isFamily(org.apache.tools.ant.taskdefs.condition.Os.FAMILY_WINDOWS)
val isMacos get() = org.apache.tools.ant.taskdefs.condition.Os.isFamily(org.apache.tools.ant.taskdefs.condition.Os.FAMILY_MAC)

dependencies {
    //add("androidMainApi", "com.implimentz:unsafe:0.0.6")
}

fun org.jetbrains.kotlin.gradle.dsl.KotlinTargetContainerWithPresetFunctions.nativeTargets(): List<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
    return when {
        isWindows -> listOf(mingwX64())
        isMacos -> listOf(macosX64(), macosArm64(), iosArm64(), iosX64())
        else -> listOfNotNull(
            linuxX64(),
            if (doEnableKotlinRaspberryPi) linuxArm32Hfp() else null,
            mingwX64(), macosX64(), macosArm64(), iosArm64(), iosX64()
        )
    }
}

dependencies {
    add("commonMainApi", project(":kmem"))

    add("jvmMainApi", "net.java.dev.jna:jna:$jnaVersion")
    add("jvmMainApi", "net.java.dev.jna:jna-platform:$jnaVersion")
}

// Code for use kdynlib dynamic libraries via interface
dependencies {
    for (target in kotlin.targets) {
        val baseKind = when (target.name) {
            "metadata" -> "metadata"
            "jvm" -> "jvm"
            "js" -> "dummy"
            "android" -> "dummy"
            else -> "native"
        }
        val configName = "ksp${target.name.capitalize()}"
        if (configurations.findByName(configName) != null) {
            add(configName, project(":kdynlib-ksp-native-lib-$baseKind"))
        }
        val sourceSetName = "${target.name}Main"
        kotlin.sourceSets.maybeCreate(sourceSetName).kotlin.srcDir(File(buildDir, "generated/ksp/$sourceSetName/kotlin"))
    }
}
