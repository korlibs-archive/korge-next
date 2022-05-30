package com.soywiz.korio.async

import kotlinx.coroutines.*
import kotlin.coroutines.*

@OptIn(ExperimentalStdlibApi::class)
actual fun <T> runBlockingNoJs(context: CoroutineContext, block: suspend CoroutineScope.() -> T): T =
    runBlocking(context.minusKey(CoroutineDispatcher.Key)) { block() }
