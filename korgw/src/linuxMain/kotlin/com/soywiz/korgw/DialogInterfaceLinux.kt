package com.soywiz.korgw

import com.soywiz.korio.lang.*
import com.soywiz.korio.stream.*
import kotlinx.cinterop.*
import platform.posix.*

actual fun createDialogInterfaceForComponent(nativeComponent: Any?): DialogInterface = NativeZenityDialogs()

// @TODO: Move this to Korio exec/execToString & make it asynchronous
class NativeZenityDialogs : ZenityDialogs() {
    private fun escapeshellarg(str: String) = "'" + str.replace("'", "\\'") + "'"

    override suspend fun exec(vararg args: String): String = memScoped {
        val command = "/bin/sh -c '" + args.joinToString(" ") { escapeshellarg(it) }.replace("'", "\"'\"") + "' 2>&1"
        println("COMMAND: $command")
        val fp = popen(command, "r")
            ?: error("Couldn't exec ${args.toList()}")

        val out = MemorySyncStream()
        val TMPSIZE = 1024
        val temp = allocArray<ByteVar>(TMPSIZE)

        do {
            val res = fread(temp, 1.convert(), TMPSIZE.convert(), fp)
            for (n in 0 until res.toInt()) {
                out.write(temp[n].toInt() and 0xFF)
            }
        } while (res.toInt() > 0)
        pclose(fp)
        return out.toByteArray().toString(Charsets.UTF8)
    }
}
