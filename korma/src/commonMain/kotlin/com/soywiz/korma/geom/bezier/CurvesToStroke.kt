package com.soywiz.korma.geom.bezier

import com.soywiz.kds.getCyclic
import com.soywiz.korma.geom.IPoint
import com.soywiz.korma.geom.Line
import com.soywiz.korma.geom.VectorArrayList
import com.soywiz.korma.geom.fastForEachGeneric
import com.soywiz.korma.geom.vector.LineCap
import com.soywiz.korma.geom.vector.LineJoin

// @TODO
private fun Curves.toStrokeCurves(join: LineJoin, startCap: LineCap, endCap: LineCap): Curves {
    TODO()
}

enum class StrokePointsMode {
    SCALABLE_POS_NORMAL_WIDTH,
    NON_SCALABLE_POS
}

/**
 * A generic stroke points with either [x, y] or [x, y, dx, dy, scale] components when having separate components,
 * it is possible to later scale the stroke without regenerating it by adjusting the [scale] component
 */
data class StrokePoints(val vector: VectorArrayList, val mode: StrokePointsMode) {
    fun scale(scale: Double) {
        if (mode == StrokePointsMode.SCALABLE_POS_NORMAL_WIDTH) {
            vector.fastForEachGeneric {
                this.set(it, 4, this.get(it, 4) * scale)
            }
        }
    }
}

/** Useful for drawing */
fun Curves.toStrokePoints(width: Double, join: LineJoin = LineJoin.MITER, startCap: LineCap = LineCap.BUTT, endCap: LineCap = LineCap.BUTT, miterLimit: Double = 10.0, mode: StrokePointsMode = StrokePointsMode.NON_SCALABLE_POS): StrokePoints {
    val out = VectorArrayList(dimensions = when (mode) {
        StrokePointsMode.SCALABLE_POS_NORMAL_WIDTH -> 5 // x, y, dx, dy, length
        StrokePointsMode.NON_SCALABLE_POS -> 2 // x, y
    })

    fun addPoint(pos: IPoint, normal: IPoint, width: Double) = when (mode) {
        StrokePointsMode.SCALABLE_POS_NORMAL_WIDTH -> out.add(pos.x, pos.y, normal.x, normal.y, width)
        StrokePointsMode.NON_SCALABLE_POS -> out.add(pos.x + normal.x * width, pos.y + normal.y * width)
    }

    fun addTwoPoints(pos: IPoint, normal: IPoint, width: Double) {
        addPoint(pos, normal, width)
        addPoint(pos, normal, -width)
    }

    fun generateJoin(curr: Curve, next: Curve) {
        val currTangent = curr.tangent(1.0)
        val nextTangent = next.tangent(0.0)

        val commonPoint = curr.calc(1.0)

        val line0a = Line.fromPointAndDirection(commonPoint + curr.normal(1.0) * width, currTangent)
        val line0b = Line.fromPointAndDirection(commonPoint + next.normal(1.0) * width, nextTangent)

        val line1a = Line.fromPointAndDirection(commonPoint + curr.normal(1.0) * -width, currTangent)
        val line1b = Line.fromPointAndDirection(commonPoint + next.normal(1.0) * -width, nextTangent)

        val p0 = line0a.getLineIntersectionPoint(line0b)
        val p1 = line1a.getLineIntersectionPoint(line1b)
        if (p0 != null && p1 != null) {
            //if (false) {
            val d0 = p0 - commonPoint
            val d1 = commonPoint - p1

            addPoint(commonPoint, d0.normalized, d0.length)
            addPoint(commonPoint, d1.normalized, -d1.length)
        } else {
            addTwoPoints(curr.calc(1.0), curr.normal(1.0), width)
        }
    }

    //println("closed: $closed")

    for (n in curves.indices) {
        val curr = curves.getCyclic(n + 0)
        val next = curves.getCyclic(n + 1)

        // Generate start cap
        if (n == 0) {
            if (closed) {
                generateJoin(curves.getCyclic(n - 1), curr)
            } else {
                // BUTT
                addTwoPoints(curr.calc(0.0), curr.normal(0.0), width)
            }
        }

        // Generate intermediate points for curves (no for plain lines)
        if (curr.order != 1) {
            // @TODO: Here we could generate curve information to render in the shader with a plain simple quadratic bezier to reduce the number of points and make the curve as accurate as possible
            val nsteps = 20
            for (n in 1 until nsteps) {
                val ratio = n.toDouble() / nsteps
                addTwoPoints(curr.calc(ratio), curr.normal(ratio), width)
            }
        }

        // Generate join
        if (n < curves.size - 1) {
            generateJoin(curr, next)
        }
        // Generate end cap
        else {
            //println("closed=$closed")
            if (closed) {
                generateJoin(curr, next)
            } else {
                // BUTT
                addTwoPoints(curr.calc(1.0), curr.normal(1.0), width)
            }
        }
    }
    return StrokePoints(out, mode)
}
