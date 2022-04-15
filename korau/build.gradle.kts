import com.soywiz.korlibs.modules.*

description = "Portable Audio library for Kotlin"

if (doEnableKotlinNative) {
    kotlin {
        for (target in allNativeTargets(project)) {
            target.compilations["main"].cinterops {
                maybeCreate("minimp3")
                maybeCreate("stb_vorbis")
            }
        }

    }
}

dependencies {
    add("commonMainApi", project(":korio"))
    add("jvmMainApi", libs.bundles.jna)
}
repositories {
    mavenCentral()
}
