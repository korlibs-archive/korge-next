import com.soywiz.korlibs.modules.*

val kspVersion: String by project
val kirpterVersion: String by project

description = "KSP Plugin for Kmem NativeL Libraries written in Kotlin"

plugins {
    kotlin("jvm")
    //id("com.soywiz.kirpter")
}

group = "com.soywiz.korlibs.korge.plugins.ksp"

dependencies {
    api(kotlin("stdlib"))
    api("com.soywiz.kirpter:kirpter-api:$kirpterVersion")
}

sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

