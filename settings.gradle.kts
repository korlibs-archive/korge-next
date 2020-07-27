rootProject.name = "korlibs-next"

pluginManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven ("https://dl.bintray.com/kotlin/kotlin-eap")
	}
}

enableFeaturePreview("GRADLE_METADATA")

include(":kmem")
/*
include(":kbox2d")
include(":kbignum")
include(":klock")
include(":klogger")
include(":korinject")
include(":kds")
include(":korma")
include(":korma-shape")
include(":luak")
include(":krypto")
include(":korte")
include(":korte-ktor")
include(":korte-korio")
include(":korte-vertx")
include(":korio")
include(":korim")
include(":korau")
include(":korgw")
include(":korvi")
include(":korge")
include(":korge-box2d")
include(":korge-admob")
include(":korge-dragonbones")
include(":korge-spine")
include(":korge-swf")

for (sample in (File(rootProject.projectDir, "samples").takeIf { it.isDirectory }?.listFiles() ?: arrayOf())) {
    include(":samples:${sample.name}")
}
*/
