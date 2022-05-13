package com.soywiz.korio.async

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import java.lang.reflect.Method
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED

suspend fun Method.invokeSuspend(obj: Any?, args: List<Any?>): Any? {
	val method = this@invokeSuspend
	val cc = coroutineContext

	val lastParam = method.parameterTypes.lastOrNull()
	val margs = java.util.ArrayList(args)
	var deferred: CompletableDeferred<Any?>? = null

    if (lastParam != null && Continuation::class.java.isAssignableFrom(lastParam)) {
		deferred = CompletableDeferred<Any?>(Job())
		margs += deferred.toContinuation(cc)
	}
	val result = method.invoke(obj, *margs.toTypedArray())
	return if (result == COROUTINE_SUSPENDED) {
		deferred?.await()
	} else {
		result
	}
}

private fun <T> CompletableDeferred<T>.toContinuation(context: CoroutineContext, job: Job? = null): Continuation<T> {
	val deferred = CompletableDeferred<T>(job)
	return object : Continuation<T> {
		override val context: CoroutineContext = context

		override fun resumeWith(result: Result<T>) {
			val exception = result.exceptionOrNull()
			if (exception != null) {
				deferred.completeExceptionally(exception)
			} else {
				deferred.complete(result.getOrThrow())
			}
		}
	}
}
