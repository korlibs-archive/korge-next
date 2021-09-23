package com.soywiz.korio.kds

import com.soywiz.kds.concurrent.*
import kotlinx.coroutines.*

suspend fun <T : Any> ConcurrentDeque<T>.consumeAllSuspend(block: suspend (T) -> Unit) {
    while (true) {
        var count = 0
        consumeAvailable { block(it); count++ }
        // @TODO: Observe notifyAll and queue to CoroutineDispatcher
        //waitSuspend()
        delay(if (count >= 1) 0L else 1L)
    }
}

//suspend fun ThreadLockSignal.waitNotifySuspend() = suspendCoroutine<Unit> { c -> this.onNotifyOnce { c.resume(Unit) } }
//suspend fun <T : Any> ConcurrentDeque<T>.waitSuspend() { this.signal.waitNotifySuspend() }
