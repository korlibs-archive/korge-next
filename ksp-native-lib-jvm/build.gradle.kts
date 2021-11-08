import com.soywiz.korlibs.modules.*

description = "KSP Plugin for Kmem NativeL Libraries written in Kotlin"

plugins {
    kotlin("jvm")
}

group = "com.soywiz.korlibs.korge.plugins.ksp"

dependencies {
    api(project(":ksp-common"))
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}
