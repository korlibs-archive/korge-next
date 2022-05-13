package com.soywiz.korio.async

import com.soywiz.korio.lang.unexpected
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

actual fun <T> runBlockingNoJs(context: CoroutineContext, block: suspend CoroutineScope.() -> T): T {
    unexpected("Calling runBlockingNoJs on JavaScript")
}
