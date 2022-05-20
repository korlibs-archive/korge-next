package com.soywiz.korio.util

import com.soywiz.klock.milliseconds
import com.soywiz.korio.async.delay
import com.soywiz.korio.async.suspendTest
import kotlin.test.Test
import kotlin.test.assertEquals

class OnceTest {
	@Test
	fun test() {
		var log = ""
		val once = Once()
		once { log += "a" }
		once { log += "b" }
		assertEquals("a", log)
	}

	@Test
	fun test2() = suspendTest {
		var count = 0
		val once = AsyncOnce<String>()
		assertEquals("a", once { delay(10.milliseconds); count++; "a" })
		assertEquals("a", once { delay(100.milliseconds); count++; "b" })
		assertEquals(1, count)
	}
}
