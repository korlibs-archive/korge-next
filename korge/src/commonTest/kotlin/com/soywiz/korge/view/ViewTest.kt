package com.soywiz.korge.view

import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.*
import com.soywiz.korma.geom.*
import kotlin.test.*

class ViewTest {
    @Test
    fun testAncestorCount() {
        val v0: View? = null
        assertEquals(0, v0.ancestorCount)
        val c2 = Container()
        val c = Container()
        val v = DummyView()
        assertEquals(0, v.ancestorCount)
        c.addChild(v)
        assertEquals(1, v.ancestorCount)
        c2.addChild(c)
        assertEquals(2, v.ancestorCount)
    }

    @Test
    fun testPositionRelativeTo() {
        lateinit var rect: SolidRect
        lateinit var rectParent: Container
        val container = Container().apply {
            scale = 2.0
            position(10, 10)
            rectParent = container {
                scale = 3.0
                rect = solidRect(100, 100).position(30, 30)
            }
        }
        assertEquals("(30, 30), (90, 90)", "${rect.pos}, ${rect.getPositionRelativeTo(container)}")
        rect.setPositionRelativeTo(container, Point(240, 240))
        assertEquals("(80, 80), (240, 240)", "${rect.pos}, ${rect.getPositionRelativeTo(container)}")
    }

    @Test
    fun testConcatMatrix() {
        lateinit var root: Container
        lateinit var middle: Container
        lateinit var leaf: Image
        root = Container().apply {
            scale(4, 2).position(20, 10)
            middle = container {
                scale(7, 3).position(50, 30)
                leaf = image(Bitmap32(32, 32)) {
                    //anchor(Anchor.MIDDLE_CENTER)
                    scale(2, 5).position(70, 90)
                }
            }
        }
        assertEquals(
            """
                [1]:Rectangle(x=0, y=0, w=32, h=32)
                [3]:Rectangle(x=0, y=0, w=32, h=32)
                [2]:Rectangle(x=0, y=0, w=32, h=32)
                [4]:Rectangle(x=70, y=90, w=64, h=160)
                [5]:Rectangle(x=540, y=300, w=448, h=480)
                [6]:Rectangle(x=540, y=300, w=448, h=480)
                [7]:Rectangle(x=2180, y=610, w=1792, h=960)
                [8]:null
                [9]:Rectangle(x=540, y=300, w=448, h=480)
            """.trimIndent(),
            """
                [1]:${leaf.getLocalBoundsOptimizedAnchored().toStringCompat()}
                [3]:${leaf.getBounds().toStringCompat()}
                [2]:${leaf.getBounds(leaf).toStringCompat()}
                [4]:${leaf.getBounds(middle).toStringCompat()}
                [5]:${leaf.getBounds(root).toStringCompat()}
                [6]:${leaf.getGlobalBounds().toStringCompat()}
                [7]:${leaf.getBounds(root, inclusive = true).toStringCompat()}
                [8]:${leaf.getWindowBoundsOrNull()?.toStringCompat()}
                [9]:${leaf.getWindowBounds().toStringCompat()}
            """.trimIndent()
        )
    }

    @Test
    fun scaleWhileMaintainingAspect_byWidthAndHeight_scalesToWidthToFit() {
        val rect = SolidRect(80.0, 60.0)

        rect.scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(
            160.0, 100000.0
        ))

        assertEquals(rect.scaledWidth, 160.0)
        assertEquals(rect.scaledHeight, 120.0)
    }

    @Test
    fun scaleWhileMaintainingAspect_byWidthAndHeight_scalesToHeightToFit() {
        val rect = SolidRect(80.0, 60.0)

        rect.scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(
            1000000.0, 120.0
        ))

        assertEquals(rect.scaledWidth, 160.0)
        assertEquals(rect.scaledHeight, 120.0)
    }

    @Test
    fun scaleWhileMaintainingAspect_byWidth_scalesUpCorrectly() {
        val rect = SolidRect(80.0, 60.0)

        rect.scaleWhileMaintainingAspect(ScalingOption.ByWidth(240.0))

        assertEquals(rect.scaledWidth, 240.0)
        assertEquals(rect.scaledHeight, 180.0)
    }

    @Test
    fun scaleWhileMaintainingAspect_byWidth_scalesDownCorrectly() {
        val rect = SolidRect(80.0, 60.0)

        rect.scaleWhileMaintainingAspect(ScalingOption.ByWidth(40.0))

        assertEquals(rect.scaledWidth, 40.0)
        assertEquals(rect.scaledHeight, 30.0)
    }

    @Test
    fun scaleWhileMaintainingAspect_byHeight_scalesUpCorrectly() {
        val rect = SolidRect(80.0, 60.0)

        rect.scaleWhileMaintainingAspect(ScalingOption.ByHeight(240.0))

        assertEquals(rect.scaledWidth, 320.0)
        assertEquals(rect.scaledHeight, 240.0)
    }

    @Test
    fun scaleWhileMaintainingAspect_byHeight_scalesDownCorrectly() {
        val rect = SolidRect(80.0, 60.0)

        rect.scaleWhileMaintainingAspect(ScalingOption.ByHeight(15.0))

        assertEquals(rect.scaledWidth, 20.0)
        assertEquals(rect.scaledHeight, 15.0)
    }
}
