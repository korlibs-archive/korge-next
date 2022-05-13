package com.soywiz.korio.async

import kotlinx.coroutines.runBlocking

actual fun asyncEntryPoint(callback: suspend () -> Unit) = runBlocking {
    callback()
}
