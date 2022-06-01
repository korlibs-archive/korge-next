package com.soywiz.korma.geom.bezier

import com.soywiz.korma.geom.shape.buildVectorPath
import com.soywiz.korma.geom.vector.getCurves
import com.soywiz.korma.geom.vector.line
import kotlin.test.Test

class CurvesToStrokeTest {
    @Test
    fun test() {
        val path = buildVectorPath {
            line(0, 0, 100, 100)
        }
        val strokePoints = path.getCurves().toStrokePoints(10.0)
        for (n in 0 until strokePoints.size) {
            println(strokePoints.vectorToString(n))
        }
        println(strokePoints)
    }
}
