package com.soywiz.korma.geom.bezier

import com.soywiz.korma.geom.VectorArrayList
import com.soywiz.korma.geom.shape.buildVectorPath
import com.soywiz.korma.geom.vector.LineJoin
import com.soywiz.korma.geom.vector.StrokeInfo
import com.soywiz.korma.geom.vector.VectorBuilder
import com.soywiz.korma.geom.vector.getCurves
import com.soywiz.korma.geom.vector.line
import com.soywiz.korma.geom.vector.lineTo
import com.soywiz.korma.geom.vector.moveTo
import com.soywiz.korma.geom.vector.rect
import com.soywiz.korma.geom.vector.star
import kotlin.test.Test
import kotlin.test.assertEquals

class CurvesToStrokeTest {
    @Test
    fun testMiterClosedRect() {
        assertEquals(
            """
                VectorArrayList[10](
                   [0.0, 0.0, 0.71, 0.71, 7.07, 7.07], 
                   [0.0, 0.0, 0.71, 0.71, -7.07, 7.07], 
                   [100.0, 0.0, -0.71, 0.71, 7.07, 7.07], 
                   [100.0, 0.0, -0.71, 0.71, -7.07, 7.07], 
                   [100.0, 100.0, -0.71, -0.71, 7.07, 7.07], 
                   [100.0, 100.0, -0.71, -0.71, -7.07, 7.07], 
                   [0.0, 100.0, 0.71, -0.71, 7.07, 7.07], 
                   [0.0, 100.0, 0.71, -0.71, -7.07, 7.07], 
                   [0.0, 0.0, 0.71, 0.71, 7.07, 7.07], 
                   [0.0, 0.0, 0.71, 0.71, -7.07, 7.07]
                )
            """.trimIndent(),
            pathPoints(LineJoin.MITER) {
                rect(0, 0, 100, 100)
            }.toString()
        )
    }

    @Test
    fun testBevelAngleCW() {
        assertEquals(
            """
                VectorArrayList[8](
                   [0.0, 0.0, -0.0, 1.0, 5.0, 5.0], 
                   [0.0, 0.0, -0.0, 1.0, -5.0, 5.0], 
                   [100.0, 0.0, -0.71, 0.71, 7.07, 7.07], 
                   [100.0, 0.0, -0.0, 1.0, -5.0, 5.0], 
                   [100.0, 0.0, -0.71, 0.71, 7.07, 7.07], 
                   [100.0, 0.0, -1.0, -0.0, -5.0, 5.0], 
                   [100.0, 100.0, -1.0, 0.0, 5.0, 5.0], 
                   [100.0, 100.0, -1.0, 0.0, -5.0, 5.0]
                )
            """.trimIndent(),
            pathPoints(LineJoin.BEVEL) {
                moveTo(0, 0)
                lineTo(100, 0)
                lineTo(100, 100)
            }.toString()
        )
    }

    @Test
    fun testClosed() {
        val path = buildVectorPath {
            star(6, 10.0, 20.0)
        }
        assertEquals(true, path.getCurves().closed)
    }

    @Test
    fun testSplit() {
        val curves = buildVectorPath {
            moveTo(0, 0)
            lineTo(100, 0)
            lineTo(200, 0)
        }.getCurves()
        assertEquals(Curves(Bezier(100,0, 150,0)), curves.split(0.5, 0.75))
        assertEquals(Curves(Bezier(50,0, 100,0)), curves.split(0.25, 0.5))
        assertEquals(Curves(Bezier(50,0, 100,0), Bezier(100,0, 150,0)), curves.split(0.25, 0.75))
    }

    fun pathPoints(join: LineJoin, block: VectorBuilder.() -> Unit): VectorArrayList =
        buildVectorPath { block() }.toStrokePointsList(StrokeInfo(thickness = 10.0, join = join), mode = StrokePointsMode.SCALABLE_POS_NORMAL_WIDTH).first().vector.clone().roundDecimalPlaces(2)
}
