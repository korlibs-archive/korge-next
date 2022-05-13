package com.soywiz.korinject.util

import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

actual fun syncTestImpl(ignoreJs: Boolean, block: suspend () -> Unit) {
	var e: Throwable? = null
	var done = false

	block.startCoroutine(object : Continuation<Unit> {
		override val context: CoroutineContext = EmptyCoroutineContext
		override fun resumeWith(result: Result<Unit>) {
			done = true
			e = result.exceptionOrNull()
		}
	})

	while (!done) {
		//Thread.sleep(1L) // @TODO: kotlin-native: No std sleep available in native? so spinlock for now
	}
	e?.let { throw it }
}
