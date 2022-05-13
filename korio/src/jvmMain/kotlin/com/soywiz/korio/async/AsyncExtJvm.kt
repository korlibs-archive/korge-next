package com.soywiz.korio.async

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.newFixedThreadPoolContext
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutorService

fun <T> Deferred<T>.jvmSyncAwait(): T = runBlocking { await() }

operator fun ExecutorService.invoke(callback: () -> Unit) {
	this.execute(callback)
}

private val mainDispatcher by lazy { newSingleThreadContext("mainDispatcher") }
internal val workerContext by lazy { newFixedThreadPoolContext(4, "worker") }

actual fun asyncEntryPoint(callback: suspend () -> Unit) =
    //runBlocking { callback() }
	runBlocking(mainDispatcher) { callback() }

suspend fun <T> executeInWorkerJVM(callback: suspend () -> T): T = withContext(workerContext) { callback() }
