package com.soywiz.korge

import com.soywiz.kmem.Platform
import kotlin.system.exitProcess

private fun getJavaVersion(): Int {
    val version = System.getProperty("java.version")
    val parts = version.split('.')
    if (parts.first() == "1") return parts.getOrElse(1) { "0" }.toInt()
    return parts.first().toInt()
}

/** Call this as soon as possible to create a new process with the JVM --add-opens */
fun jvmEnsureAddOpens() {
    val javaVersion = getJavaVersion()
    if (javaVersion <= 8) return
    val processInfo = ProcessHandle.current().info()
    val cli = processInfo.commandLine().get()
    val command = processInfo.command().get()
    val arguments = processInfo.arguments().get()
    if (!cli.contains("--add-opens")) {
        println("Java Version $javaVersion, not included --add-opens. Creating a new process...")
        println("CLI: $cli")
        val addOpens = buildList {
            add("--add-opens=java.desktop/sun.java2d.opengl=ALL-UNNAMED")
            add("--add-opens=java.desktop/java.awt=ALL-UNNAMED")
            add("--add-opens=java.desktop/sun.awt=ALL-UNNAMED")
            if (Platform.os.isMac) {
                add("--add-opens=java.desktop/sun.lwawt=ALL-UNNAMED")
                add("--add-opens=java.desktop/sun.lwawt.macosx=ALL-UNNAMED")
            }
        }

        val pb = ProcessBuilder(command, *addOpens.toTypedArray(), *arguments)
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT)
        pb.redirectError(ProcessBuilder.Redirect.INHERIT)
        val p = pb.start()
        exitProcess(p.waitFor())
    }
}
