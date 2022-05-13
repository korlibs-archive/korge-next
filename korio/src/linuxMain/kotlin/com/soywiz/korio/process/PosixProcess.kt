package com.soywiz.korio.process

import com.soywiz.klock.milliseconds
import com.soywiz.kmem.startAddressOf
import com.soywiz.korio.async.delay
import com.soywiz.korio.file.VfsProcessHandler
import com.soywiz.korio.file.std.ShellArgs
import kotlinx.cinterop.*
import platform.posix.AF_UNIX
import platform.posix.FILE
import platform.posix.SOCK_STREAM
import platform.posix.STDIN_FILENO
import platform.posix.STDOUT_FILENO
import platform.posix._exit
import platform.posix.chdir
import platform.posix.close
import platform.posix.dup2
import platform.posix.execv
import platform.posix.fd_set
import platform.posix.fdopen
import platform.posix.fileno
import platform.posix.fork
import platform.posix.posix_FD_ISSET
import platform.posix.posix_FD_SET
import platform.posix.putenv
import platform.posix.read
import platform.posix.select
import platform.posix.socketpair
import platform.posix.timeval
import platform.posix.waitpid
import kotlin.collections.*

actual suspend fun posixExec(
    path: String, cmdAndArgs: List<String>, env: Map<String, String>, handler: VfsProcessHandler
): Int = memScoped {
    val fd = alloc<fd_set>()
    val timeval = alloc<timeval>()
    timeval.tv_sec = 0
    timeval.tv_usec = 1
    val (f, pid) = sopen(*cmdAndArgs.toTypedArray(), cwd = path, envs = env)
    val fn = fileno(f)
    val bufSize = 10240
    ByteArray(bufSize).usePinned { tmp ->
        val bufArray = tmp.get()
        val buf = tmp.startAddressOf
        loop@while (true) {
            while (true) {
                posix_FD_SET(fn, fd.ptr)
                select(fileno(f) + 1, fd.ptr, null, null, timeval.ptr)
                if (posix_FD_ISSET(fn, fd.ptr) != 0) {
                    val res = read(fn, buf, bufSize.convert()).toInt()
                    if (res <= 0) break@loop
                    handler.onOut(bufArray.copyOf(res))
                } else {
                    break // No more data available
                }
            }
            delay(1.milliseconds)
        }
    }
    val status = alloc<IntVar>()
    waitpid(pid.convert(), status.ptr, 0.convert())
    status.value
}

fun sopen(vararg cmds: String, cwd: String, envs: Map<String, String> = mapOf()): Pair<CPointer<FILE>?, Long> = memScoped {
    val fds = allocArray<IntVar>(2)
    if (socketpair(AF_UNIX, SOCK_STREAM, 0, fds) < 0) {
        return null to 0L
    }
    val rcmd = ShellArgs.buildShellExecCommandLineArrayForExecl(cmds.toList())
    //val rcmd = listOf("/bin/sh", "-c", "\"'echo' 'hello world'\"")
    //val rcmd = listOf("/bin/sh", "-c", "'echo' 'hello world'")

    //println("rcmd=$rcmd")
    val command = rcmd.first()
    //val args = rcmd.drop(1)
    val args = rcmd
    //println("rcmd=$rcmd")
    val pid = fork()
    when (pid) {
        -1 -> {
            close(fds[0])
            close(fds[1])
            return null to 0L
        }
        0 -> { // child
            //printf("CHILD!\n");
            close(fds[0])
            dup2(fds[0], STDIN_FILENO)
            dup2(fds[1], STDOUT_FILENO)
            close(fds[1])
            chdir(cwd)
            for ((k ,v) in envs) putenv("$k=$v".cstr)
            memScoped {
                val vargs = allocArray<CPointerVar<ByteVar>>(args.size + 1)
                for (n in args.indices) vargs[n] = args[n].cstr.getPointer(this)
                vargs[args.size] = null
                execv(command, vargs)
                _exit(127);
            }
        }
    }
    //printf("GO!\n");
    /* parent */
    close(fds[1]);
    return fdopen(fds[0], "r+") to pid.toLong()
}
