package com.soywiz.korag

import com.soywiz.korag.software.*
import com.soywiz.korio.async.*
import kotlin.test.*

class AGTest {
	@Test
	fun testOnReady() = suspendTest {
		val ag = AGFactorySoftware().create(null, AGConfig())
		val buffer = ag.createIndexBuffer()
		buffer.upload(intArrayOf(1, 2, 3, 4))
	}

    @Test
    fun testCombineScissor() {
        assertEquals(null, AG.Scissor.combine(null, null))
        assertEquals(AG.Scissor(0.0, 0.0, 100.0, 100.0), AG.Scissor.combine(AG.Scissor(0.0, 0.0, 100.0, 100.0), null))
        assertEquals(AG.Scissor(50.0, 50.0, 50.0, 50.0), AG.Scissor.combine(AG.Scissor(0.0, 0.0, 100.0, 100.0), AG.Scissor(50.0, 50.0, 100.0, 100.0)))
    }
}
