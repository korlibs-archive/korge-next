package com.soywiz.korio.async

import kotlinx.coroutines.runBlocking
import platform.posix.LC_ALL
import platform.posix.setlocale

actual fun asyncEntryPoint(callback: suspend () -> Unit) = runBlocking {
    setlocale(LC_ALL, ".UTF-8")
    callback()
}
