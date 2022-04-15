description = "Portable UI with accelerated graphics support for Kotlin"

com.soywiz.korlibs.NativeTools.configureCInteropLinux(project, "X11Embed")

dependencies {
    add("commonMainApi", project(":korim"))
    add("jvmMainApi", libs.bundles.jna)
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
