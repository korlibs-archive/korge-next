package com.soywiz.korma.geom

import kotlin.math.hypot
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class PointTest {
    @Test
    fun testPolar() {
        assertEquals("(10, 0)", Point.fromPolar(0.degrees, 10.0).toString())
        assertEquals("(0, 10)", Point.fromPolar(90.degrees, 10.0).toString())
        assertEquals("(-10, 0)", Point.fromPolar(180.degrees, 10.0).toString())
        assertEquals("(0, -10)", Point.fromPolar(270.degrees, 10.0).toString())
        assertEquals("(10, 0)", Point.fromPolar(360.degrees, 10.0).toString())

        assertEquals("(0, 5)", Point.fromPolar(0.degrees, 10.0).setToPolar(90.degrees, 5.0).toString())
    }

    @Test
    fun test_WHEN_set_new_length_MUST_recalculate_point_coordinates() {
        /* GIVEN */
        val posX = 11.1
        val posY = 22.2
        val point = Point(posX, posY)
        assertEquals(point.length, hypot(posX, posY))

        /* WHEN */
        val newLength = 33.3
        point.setLength(newLength)

        /* THEN */
        assertEquals(newLength, point.length)
        assertNotEquals(point.x, posX)
        assertNotEquals(point.y, posY)
    }

    @Test
    fun test_GIVEN_point_at_zero_WHEN_set_new_length_CANT_change_coordinates() {
        /* GIVEN */
        val point = Point()
        assertEquals(point.length, 0.0)

        /* WHEN */
        point.setLength(20.0)

        /* THEN */
        assertEquals(point.length, 0.0)
    }

    @Test
    fun test_WHEN_set_same_length_CANT_change_coordinates() {
        /* GIVEN */
        val posX = 11.1
        val posY = 22.2
        val point = Point(posX, posY)
        assertEquals(point.length, hypot(posX, posY))

        /* WHEN */
        val newLength = hypot(posX, posY)
        point.setLength(newLength)

        /* THEN */
        assertEquals(newLength, point.length)
        assertEquals(point.x, posX)
        assertEquals(point.y, posY)
    }
}
