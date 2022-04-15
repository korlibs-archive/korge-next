package com.soywiz.korio.process

import com.soywiz.kmem.*
import com.soywiz.korio.file.VfsProcessHandler
import com.soywiz.korio.lang.*
import com.soywiz.korio.stream.*
import kotlinx.cinterop.*
import platform.posix.*

private fun escapeshellarg(str: String) = "'" + str.replace("'", "\\'") + "'"

// @TODO: Use a separate thread
actual suspend fun posixExec(
    path: String, cmdAndArgs: List<String>, env: Map<String, String>, handler: VfsProcessHandler
): Int = memScoped {
    val command = "/bin/sh -c '" + cmdAndArgs.joinToString(" ") { escapeshellarg(it) }.replace("'", "\"'\"") + "' 2>&1"
    println("COMMAND: $command")
    val fp = popen(command, "r")
        ?: error("Couldn't exec ${cmdAndArgs.toList()}")

    val temp = ByteArray(1024)
    temp.usePinned { tempPin ->
        do {
            val res = fread(tempPin.startAddressOf, 1.convert(), temp.size.convert(), fp).toInt()
            if (res > 0) {
                handler.onOut(temp.copyOf(res))
            }
        } while (res > 0)

    }

    return pclose(fp)
}
