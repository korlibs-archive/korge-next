import com.soywiz.korge.gradle.BuildVersions

dependencies {
    add("commonMainApi", project(":korge"))
    add("commonMainImplementation", "io.github.quillraven.fleks:Fleks:${BuildVersions.FLEKS}")
}
