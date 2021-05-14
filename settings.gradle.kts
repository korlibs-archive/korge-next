rootProject.name = "korlibs-next"

pluginManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
        maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
	}
}

include(":korge-gradle-plugin")
include(":korgeall")
include(":korgeall-sample")
