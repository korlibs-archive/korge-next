import com.soywiz.korlibs.modules.*

description = "I/O utilities for Kotlin"

dependencies {
	add("commonMainApi", libs.kotlinx.coroutines.core)

    add("commonMainApi", project(":klock"))
	add("commonMainApi", project(":kds"))
	add("commonMainApi", project(":kmem"))
    add("commonMainApi", project(":krypto"))
    add("commonMainApi", project(":klogger"))

    afterEvaluate {
        if (configurations.findByName("androidMainApi") != null) {
            add("androidMainApi", libs.kotlinx.coroutines.android)
        }
    }
}

if (doEnableKotlinNative) {
    kotlin {
        for (target in nativeTargets(project)) {
            if (target.isWin) {
                target.compilations["main"].cinterops {
                    maybeCreate("win32ssl")
                }
            }
        }
    }
}
