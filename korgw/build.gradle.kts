description = "Portable UI with accelerated graphics support for Kotlin"

//plugins { id("com.google.devtools.ksp") }

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

/*
// Code for use kdynlib dynamic libraries via interface
dependencies {
    tasks.withType<com.google.devtools.ksp.gradle.KspTask> {
        val task = this

        //println("$project : task=$task")

        val targetName = task.name.removePrefix("kspKotlin").removePrefix("kspDebugKotlin").removePrefix("kspReleaseKotlin").decapitalize()
        val targetKind = when (task) {
            is com.google.devtools.ksp.gradle.KspTaskJS -> "js"
            is com.google.devtools.ksp.gradle.KspTaskJvm -> "jvm"
            is com.google.devtools.ksp.gradle.KspTaskNative -> "native"
            is com.google.devtools.ksp.gradle.KspTaskMetadata -> "metadata"
            else -> "unknown"
        }
        task.options.add(org.jetbrains.kotlin.gradle.plugin.SubpluginOption("apoption", "kotlinTarget=$targetName"))
        task.options.add(org.jetbrains.kotlin.gradle.plugin.SubpluginOption("apoption", "kotlinTargetKind=$targetKind"))
    }

    // Old Kotlin 1.5
    if (configurations.findByName("ksp") != null) {
        add("ksp", project(":kdynlib-ksp"))
    }

    afterEvaluate {
        for (target in kotlin.targets) {
            val configName = "ksp${target.name.capitalize()}"
            if (configurations.findByName(configName) != null) {
                add(configName, project(":kdynlib-ksp"))
                //println("$project : $configName")
            } else {
                //println("$project : $configName - NOT FOUND!")
            }
            //val sourceSetName = "${target.name}Main"
            //kotlin.sourceSets.maybeCreate(sourceSetName).kotlin.srcDir(File(buildDir, "generated/ksp/$sourceSetName/kotlin"))
        }
    }
}
*/
