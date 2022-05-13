package com.soywiz.korinject

import com.soywiz.korinject.util.expectException
import com.soywiz.korinject.util.suspendTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AsyncInjectorSuspendContextTest {
    @Test
    fun testWithInjectorStoresInjectorInTheContext() = suspendTest {
        val injector = AsyncInjector()
        val string = "hello"
        injector.mapInstance(string)
        val result = withInjector(injector) {
            otherFunction()
        }
        assertEquals(string, result)
    }

    @Test
    fun testWithoutInjector() = suspendTest {
        val injector = AsyncInjector()
        val string = "hello"
        injector.mapInstance(string)
        expectException<IllegalStateException> {
            otherFunction()
        }
    }

    suspend fun otherFunction(): String = injector().get()
}
