package com.soywiz.korio.process

import com.soywiz.kds.concurrent.*
import com.soywiz.kds.lock.*
import com.soywiz.klock.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.VfsProcessHandler
import kotlinx.cinterop.*
import platform.posix.*
import kotlin.native.concurrent.*

actual suspend fun posixExec(
    path: String, cmdAndArgs: List<String>, env: Map<String, String>, handler: VfsProcessHandler
): Int {
    class Packet(val kind: Int, val data: ByteArray, val value: Int) {
        init {
            this.freeze()
        }
    }

    class Info(
        val commandLine: String,
        val deque: ConcurrentDeque<Packet>,
    )

    println("@WARNING: this exec implementation is not escaping, not setting env variables, or the current path")

    // @TODO: escape stuff
    // @TODO: place environment variables like ENV=value ENV2=value2 cd path; command
    // @TODO: does it work on windows? only posix?
    val commandLine = cmdAndArgs.joinToString(" ")

    //println("[MAIN] BEFORE WORKER: commandLine=$commandLine")

    val deque = ConcurrentDeque<Packet>()
    val worker = Worker.start()
    worker.execute(TransferMode.SAFE, { Info(commandLine, deque) }, { info ->
        memScoped {
            val f = _popen(info.commandLine, "r")
            //println("[WORKER] OPENED ${info.commandLine}")
            val temp = ByteArray(1024)
            temp.usePinned { pin ->
                val tempAddress = pin.addressOf(0)
                while (true) {
                    val result = fread(tempAddress, 1, temp.size.convert(), f).toLong()
                    //println("[WORKER] fread result $result")
                    if (result <= 0L) break
                    info.deque.add(Packet(0, temp.copyOf(result.toInt()), 0))
                }
            }
            val exitCode = _pclose(f)
            //println("[WORKER] pclose $exitCode")
            info.deque.add(Packet(-1, byteArrayOf(), exitCode))
            info.deque.close()
        }
    })

    var exitCode: Int? = null

    //println("[MAIN] START WAIT")

    deque.consumeAllSuspend {
        when (it.kind) {
            -1 -> exitCode = it.value
            0 -> handler.onOut(it.data)
            1 -> handler.onErr(it.data)
        }
    }

    //println("[MAIN] END WAIT")

    worker.requestTermination()

    //println("[MAIN] END WAIT 2")
    return exitCode!!
}
