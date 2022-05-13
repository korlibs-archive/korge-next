package com.soywiz.korge.component.length

import com.soywiz.korge.test.assertEqualsFileReference
import com.soywiz.korge.tests.ViewsForTesting
import com.soywiz.korge.view.View
import com.soywiz.korge.view.fixedSizeContainer
import com.soywiz.korge.view.solidRect
import kotlin.test.Test
import kotlin.test.assertEquals

class BindLengthComponentJvmTest : ViewsForTesting(log = true) {
    @Test
    fun testPercent() = viewsTest {
        val container = fixedSizeContainer(300.0, 500.0)
        val rect = container.solidRect(100, 100)
        rect.bindLength(View::x) { 50.percent }
        rect.bindLength(View::y) { 50.percent }
        assertEquals(0.0, rect.x)
        assertEquals(0.0, rect.y)
        delayFrame()
        assertEqualsFileReference("korge/bindlength/BindLengthComponentJvmTest.log", logAg!!.getLogAsString())
        assertEquals(150.0, rect.x)
        assertEquals(250.0, rect.y)
    }
}
