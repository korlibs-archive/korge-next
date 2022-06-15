package com.soywiz.korma.geom.bezier

import com.soywiz.korim.vector.format.pathSvg
import com.soywiz.korma.geom.VectorArrayList
import com.soywiz.korma.geom.shape.buildVectorPath
import com.soywiz.korma.geom.vector.LineJoin
import com.soywiz.korma.geom.vector.StrokeInfo
import com.soywiz.korma.geom.vector.VectorBuilder
import com.soywiz.korma.geom.vector.toCurvesList
import kotlin.test.Test
import kotlin.test.assertEquals

class CurvesToStrokeExTest {
    @Test
    fun testShape() {
        val curvesList = buildVectorPath {
            pathSvg("m262.15-119.2s2.05-8-2.35-3.6c0,0-6.4,5.2-13.2,5.2,0,0-13.2,2-17.2,14,0,0-3.6,24.4,3.6,29.6,0,0,4.4,6.8,10.8,0.8s20.35-33.6,18.35-46z")
        }.toCurvesList()
        val curves = curvesList.first()
        assertEquals(1, curvesList.size)
        assertEquals(6, curves.beziers.size)
        assertEquals(true, curves.closed)
    }

    fun pathPoints(join: LineJoin, block: VectorBuilder.() -> Unit): VectorArrayList =
        buildVectorPath { block() }.toStrokePointsList(StrokeInfo(thickness = 10.0, join = join), mode = StrokePointsMode.SCALABLE_POS_NORMAL_WIDTH).first().vector.clone().roundDecimalPlaces(2)

}
