description = "Memory utilities for Kotlin"

val jnaVersion: String by project

dependencies {
    //add("androidMainApi", "com.implimentz:unsafe:0.0.6")
    add("jvmMainApi", "net.java.dev.jna:jna:$jnaVersion")
    //add("jvmMainApi", "net.java.dev.jna:jna-platform:$jnaVersion")
}
