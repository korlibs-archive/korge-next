package com.soywiz.korio.process

import com.soywiz.kds.concurrent.*
import com.soywiz.klock.*
import com.soywiz.kmem.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.VfsProcessHandler
import com.soywiz.korio.lang.*
import com.soywiz.korio.stream.*
import kotlinx.cinterop.*
import platform.posix.*
import kotlin.native.concurrent.*

// @TODO: Use a separate thread
actual suspend fun posixExec(
    path: String, cmdAndArgs: List<String>, env: Map<String, String>, handler: VfsProcessHandler
): Int {
    class Info(
        val commandLine: String,
        val stdout: ConcurrentDeque<ByteArray>,
        val stderr: ConcurrentDeque<ByteArray>,
        val result: ConcurrentDeque<Int>,
    )

    println("@WARNING: this exec implementation is not setting env variables, or the current path, and not reading stderr")

    // @TODO: place environment variables like ENV=value ENV2=value2 cd path; command
    // @TODO: does it work on windows? only posix?
    val commandLine = "/bin/sh -c '" + cmdAndArgs.joinToString(" ") { escapeshellargUnix(it) }.replace("'", "\"'\"") + "'"

    //println("[MAIN] BEFORE WORKER: commandLine=$commandLine")

    val stdoutQueue = ConcurrentDeque<ByteArray>()
    val stderrQueue = ConcurrentDeque<ByteArray>()
    val resultQueue = ConcurrentDeque<Int>()
    val worker = Worker.start()
    worker.execute(TransferMode.SAFE, { Info(commandLine, stdoutQueue, stderrQueue, resultQueue) }, { info ->
        memScoped {
            val f = popen(info.commandLine, "r")
            //println("[WORKER] OPENED ${info.commandLine}")
            val temp = ByteArray(1024)
            temp.usePinned { pin ->
                val tempAddress = pin.addressOf(0)
                while (true) {
                    val result = fread(tempAddress, 1, temp.size.convert(), f).toInt()
                    //println("[WORKER] fread result $result")
                    if (result <= 0) break
                    info.stdout.add(temp.copyOf(result))
                }
            }
            val exitCode = pclose(f)
            //println("[WORKER] pclose $exitCode")
            info.result.add(exitCode)
        }
    })

    var exitCode: Int? = null

    //println("[MAIN] START WAIT")

    while (exitCode == null) {
        stdoutQueue.consume()?.let {
            //println("[MAIN] ON OUT: ${it.size}")
            handler.onOut(it)
        }
        stderrQueue.consume()?.let {
            //println("[MAIN] ON ERR: ${it.size}")
            handler.onErr(it)
        }
        exitCode = resultQueue.consume()
        if (exitCode != null) {
            //println("[MAIN] ON EXIT: $exitCode")
        }
        delay(1.milliseconds)
    }

    //println("[MAIN] END WAIT")

    worker.requestTermination()

    //println("[MAIN] END WAIT 2")
    return exitCode
}
