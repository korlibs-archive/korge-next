import org.jetbrains.kotlin.konan.properties.*

plugins {
    `kotlin-dsl`
    publishing
    `maven-publish`
    signing
    //kotlin("multiplatform")
    //kotlin("gradle-plugin")
}

val props = loadProperties(File(rootDir, "../gradle.properties").absolutePath)

val kotlinVersion: String = props["kotlinVersion"].toString()
val androidToolsBuildGradleVersion = props["androidBuildGradleVersion"].toString()
val gsonVersion = props["gsonVersion"].toString()

dependencies {
    implementation("com.android.tools.build:gradle:$androidToolsBuildGradleVersion")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    implementation("com.google.code.gson:gson:$gsonVersion")
}


repositories {
    mavenLocal()
    mavenCentral()
    google()
    if (kotlinVersion.contains("-M") || kotlinVersion.contains("-dev") || kotlinVersion.contains("-RC") || kotlinVersion.contains("eap") || kotlinVersion.contains("-release")) {
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/temporary")
        maven("https://maven.pkg.jetbrains.space/public/p/kotlinx-coroutines/maven")
        maven("https://maven.pkg.jetbrains.space/kotlin/p/kotlin/bootstrap/")
    }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
}
