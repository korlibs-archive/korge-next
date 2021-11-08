import com.soywiz.korlibs.modules.*

plugins {
    java
    `java-gradle-plugin`
    kotlin("jvm")
    `maven-publish`
    id("com.gradle.plugin-publish")
    //id("com.github.gmazzo.buildconfig")
}

description = "Kotlin IR Plugin for KDynLib"
group = "com.soywiz.korlibs.kdynlib.irplugin"

dependencies {
    api(kotlin("stdlib"))
    implementation(kotlin("gradle-plugin-api"))
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    testImplementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    testImplementation(kotlin("test-junit"))
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.5")
}

//buildConfig {
//    val project = this
//    packageName(project.group.toString())
//    buildConfigField("String", "PROJECT_GROUP_ID", "\"${project.group}\"")
//    buildConfigField("String", "PROJECT_ARTIFACT_ID", "\"${project.name}\"")
//    buildConfigField("String", "PROJECT_VERSION", "\"${project.version}\"")
//}


sourceSets.main {
    java.srcDirs("src/main/kotlin")
}

gradlePlugin {
    plugins {
        create("kotlinReactFunction") {
            id = "com.soywiz.kdynlib"
            displayName = "Kdynlib"
            description = "Dynamic Libraries for Common Kotlin"
            implementationClass = "com.soywiz.kdynlib.KDynLibGradlePlugin"
        }
    }
}
