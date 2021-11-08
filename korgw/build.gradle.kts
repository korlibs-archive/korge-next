description = "Portable UI with accelerated graphics support for Kotlin"

val enableKotlinNative: String by project
val doEnableKotlinNative get() = enableKotlinNative == "true"

val enableKotlinRaspberryPi: String by project
val doEnableKotlinRaspberryPi get() = enableKotlinRaspberryPi == "true"

val isWindows get() = org.apache.tools.ant.taskdefs.condition.Os.isFamily(org.apache.tools.ant.taskdefs.condition.Os.FAMILY_WINDOWS)
val isMacos get() = org.apache.tools.ant.taskdefs.condition.Os.isFamily(org.apache.tools.ant.taskdefs.condition.Os.FAMILY_MAC)
val hasAndroid = project.extensions.findByName("android") != null

fun org.jetbrains.kotlin.gradle.dsl.KotlinTargetContainerWithPresetFunctions.nativeTargets(): List<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
    return when {
        isWindows -> listOfNotNull(mingwX64())
        isMacos -> listOfNotNull(macosX64(), macosArm64(), iosArm64(), iosX64())
        else -> listOfNotNull(
            linuxX64(),
            if (doEnableKotlinRaspberryPi) linuxArm32Hfp() else null,
            mingwX64(), macosX64(), macosArm64()
        )
    }
}

if (doEnableKotlinNative) {
    kotlin {
        for (target in nativeTargets()) {
            target.compilations["main"].cinterops {
                if (target.name == "linuxX64" || target.name == "linuxArm32Hfp") {
                    maybeCreate("X11_embed")
                }
                //if (target.name == "linuxX64") maybeCreate("X11")
            }
        }
    }
}

dependencies {
    add("commonMainApi", project(":korim"))
    add("commonMainApi", project(":kdynlib"))
    if (hasAndroid) {
        add("androidMainApi", "com.android.support:appcompat-v7:28.0.0")
    }
}

afterEvaluate {
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
