package com.soywiz.korma.geom

import kotlin.test.Test
import kotlin.test.assertEquals

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
    fun testAngle() {
        assertEquals(0.degrees, Point().setToZero().angle)
        assertEquals(45.degrees, Point().setToOne().angle)
        assertEquals(0.degrees, Point().setToRight().angle)
        assertEquals(90.degrees, Point().setToDown().angle)
        assertEquals(180.degrees, Point().setToLeft().angle)
        assertEquals(270.degrees, Point().setToUp().angle)
    }

    @Test
    fun testChangeLength_WITH_different_types_MUST_keep_angle_degrees() {
        val point = Point(5, 5)
        assertEquals(45.degrees, point.angle)

        // Double
        point.changeLength(10.0)
        assertEquals(10.0, point.length)
        assertEquals(45.degrees, point.angle)

        // Float
        point.changeLength(20f)
        assertEquals(20.0, point.length)
        assertEquals(45.degrees, point.angle)

        // Int
        point.changeLength(40)
        assertEquals(40.0, point.length)
        assertEquals(45.degrees, point.angle)
    }

    @Test
    fun testChangeLength_ISNOT_accurate_in_all_scenarios() {
        val point = Point(5, 5)
        assertEquals(45.degrees, point.angle)

        point.changeLength(30.0)
        assertEquals(30.0, point.length, 0.01)
    }

    @Test
    fun testChangeLength_WITH_point_at_zero_CANT_change_length() {
        // Given
        val point = Point().setToZero()
        assertEquals(0.0, point.length)

        // When
        point.changeLength(20.0)

        // Then
        assertEquals(0.0, point.length)
    }
}
