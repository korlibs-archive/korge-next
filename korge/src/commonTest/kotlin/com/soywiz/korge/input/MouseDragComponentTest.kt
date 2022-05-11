package com.soywiz.korge.input

import com.soywiz.korev.MouseButton
import com.soywiz.korge.tests.ViewsForTesting
import com.soywiz.korge.view.ViewsTest
import com.soywiz.korge.view.solidRect
import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.Matrix
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.SizeInt
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class MouseDragComponentTest : ViewsForTesting(
    virtualSize = SizeInt(100, 100),
    windowSize = SizeInt(200, 200),
) {
    @Test
    fun testStageScale() = viewsTest {
        assertEquals(Matrix(), stage.localMatrix)
        assertEquals(1.0, views.ag.devicePixelRatio)
        assertEquals(0.5, views.windowToGlobalScaleX)
        assertEquals(2.0, views.globalToWindowScaleX)
    }

    @Test
    fun testMouseCoords() = viewsTest {
        val rect = solidRect(100, 100, Colors.RED)
        rect.draggable()
        val deltaX = 20
        val deltaY = 10
        mouseMoveTo(10, 10)
        mouseDown(MouseButton.LEFT)
        mouseMoveTo(10 + deltaX, 10 + deltaY)
        mouseUp(MouseButton.LEFT)
        assertEquals(Point(deltaX, deltaY), rect.pos)
    }
}
