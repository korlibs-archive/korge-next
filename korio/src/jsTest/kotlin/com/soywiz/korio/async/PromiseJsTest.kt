package com.soywiz.korio.async

import com.soywiz.klock.DateTime
import com.soywiz.klock.milliseconds
import com.soywiz.korio.jsGlobal
import com.soywiz.korio.util.OS
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PromiseJsTest {
    @Test
    fun test() = suspendTest({ !OS.isJsNodeJs }) {
        val startTime = DateTime.now()
        val value = delay(100)
        assertTrue(value is JsPromise<*>)
        assertTrue(value.asDynamic().then != null)
        value.await()
        val endTime = DateTime.now()
        assertEquals(true, endTime - startTime >= 100.milliseconds)
    }

    fun delay(ms: Int): Promise<Unit> = Promise { resolve, reject -> jsGlobal.setTimeout({ resolve(Unit) }, ms) }
}

@JsName("Promise")
private external class JsPromise<T>
