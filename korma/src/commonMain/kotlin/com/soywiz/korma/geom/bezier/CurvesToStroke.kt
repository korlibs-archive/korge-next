package com.soywiz.korma.geom.bezier

import com.soywiz.kds.getCyclic
import com.soywiz.kds.iterators.fastForEach
import com.soywiz.korma.geom.Angle
import com.soywiz.korma.geom.IPoint
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.VectorArrayList
import com.soywiz.korma.geom.angle
import com.soywiz.korma.geom.angleTo
import com.soywiz.korma.geom.plus
import com.soywiz.korma.geom.times
import com.soywiz.korma.geom.vector.LineCap
import com.soywiz.korma.geom.vector.LineJoin

// @TODO
private fun Curves.toStrokeCurves(join: LineJoin, startCap: LineCap, endCap: LineCap): Curves {
    TODO()
}

/** Useful for drawing */
fun Curves.toStrokePoints(width: Double, join: LineJoin = LineJoin.MITER, startCap: LineCap = LineCap.BUTT, endCap: LineCap = LineCap.BUTT): VectorArrayList {
    val out = VectorArrayList(dimensions = 5) // x, y, dx, dy, length

    fun addTwoPoints(pos: IPoint, normal: IPoint, width: Double) {
        val flipped = Point(normal).setToNormal().setToNormal()
        //val flipped = Point(normal).neg()
        out.add(pos.x, pos.y, normal.x, normal.y, width)
        out.add(pos.x, pos.y, flipped.x, flipped.y, -width)
    }

    for (n in curves.indices) {
        val prev = curves.getCyclic(n - 1)
        val curr = curves.getCyclic(n + 0)
        val next = curves.getCyclic(n + 1)

        // No caps
        if (closed) {
            // Generate joins
            TODO()
        } else {
            // Generate start cap
            if (n == 0) {
                // BUTT
                addTwoPoints(curr.calc(0.0), curr.normal(0.0), width)
            }

            // Generate intermediate points for curves (no for lines)
            // Generate join

            // Generate end cap
            if (n == curves.size - 1) {
                // BUTT
                addTwoPoints(curr.calc(1.0), curr.normal(1.0), width)
            }
        }
    }
    return out
}
