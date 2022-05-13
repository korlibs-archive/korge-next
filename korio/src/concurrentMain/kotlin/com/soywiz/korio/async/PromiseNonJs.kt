package com.soywiz.korio.async

import kotlinx.coroutines.CompletableDeferred
import kotlin.coroutines.CoroutineContext

actual fun <T> Promise(coroutineContext: CoroutineContext, executor: (resolve: (T) -> Unit, reject: (Throwable) -> Unit) -> Unit): Promise<T> {
    val deferred = CompletableDeferred<T>()
    executor({ deferred.complete(it) }, { deferred.completeExceptionally(it) })
    return deferred.toPromise(coroutineContext)
}
