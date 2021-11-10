import com.soywiz.korlibs.modules.*

val kspVersion: String by project

description = "KSP Plugin for Kmem NativeL Libraries written in Kotlin"

plugins {
    kotlin("jvm")
}

group = "com.soywiz.korlibs.korge.plugins.ksp"

dependencies {
    api(kotlin("stdlib"))
    api("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

