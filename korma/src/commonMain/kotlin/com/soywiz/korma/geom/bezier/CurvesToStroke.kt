package com.soywiz.korma.geom.bezier

import com.soywiz.kds.forEachRatio01
import com.soywiz.kds.getCyclic
import com.soywiz.korma.geom.IPoint
import com.soywiz.korma.geom.Line
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.VectorArrayList
import com.soywiz.korma.geom.fastForEachGeneric
import com.soywiz.korma.geom.vector.LineCap
import com.soywiz.korma.geom.vector.LineJoin

// @TODO
//private fun Curves.toStrokeCurves(join: LineJoin, startCap: LineCap, endCap: LineCap): Curves {
//    TODO()
//}

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

class StrokePointsBuilder(val width: Double, val mode: StrokePointsMode = StrokePointsMode.NON_SCALABLE_POS) {
    val NSTEPS = 20

    val out: VectorArrayList = VectorArrayList(dimensions = when (mode) {
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

    fun addJoin(curr: Curve, next: Curve, kind: LineJoin, miterLimit: Double) {
        when (kind) {
            LineJoin.MITER -> {
            }
            LineJoin.BEVEL -> {
            }
            LineJoin.ROUND -> {
            }
        }
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

    fun addCap(curr: Curve, ratio: Double, kind: LineCap) {
        when (kind) {
            LineCap.SQUARE, LineCap.ROUND -> {
                val derivate = curr.normal(ratio).setToNormal().also { if (ratio == 1.0) it.neg() }
                when (kind) {
                    LineCap.SQUARE -> {
                        //val w = if (ratio == 1.0) -width else width
                        addTwoPoints(curr.calc(ratio) + derivate * width, curr.normal(ratio), width) // Not right
                    }
                    LineCap.ROUND -> {
                        val p0 = curr.calc(ratio) + curr.normal(ratio) * width
                        val p1 = curr.calc(ratio) + derivate * (width * 1.5) + curr.normal(ratio) * width
                        val p2 = curr.calc(ratio) + derivate * (width * 1.5) + curr.normal(ratio) * -width
                        val p3 = curr.calc(ratio) + curr.normal(ratio) * -width
                        //println("---")
                        //println("p0=$p0")
                        //println("p1=$p1")
                        //println("p2=$p2")
                        //println("p3=$p3")
                        val nsteps = NSTEPS
                        val curve = if (ratio == 1.0) BezierCurve(p3, p2, p1, p0) else BezierCurve(p0, p1, p2, p3)
                        val pointZero = Point(0, 0)
                        forEachRatio01(nsteps, include0 = true, include1 = true) {
                            val pe = curve.calc(it)
                            val pd = (pe - p0)
                            val pdLen = pd.length
                            pd.normalize()
                            addPoint(p0, pointZero, 0.0)
                            //addPoint(curr.calc(it), curr.normal(it), width)
                            addPoint(p0, pd, pdLen)
                        }
                        //addPoint(p0, Point(0, 0), width)
                        //addPoint(p3, Point(0, 0), -width)
                    }
                    else -> error("Can't happen")
                }
            }
            LineCap.BUTT -> {
                addTwoPoints(curr.calc(ratio), curr.normal(ratio), width)
            }
        }
    }

    fun addCurvePoints(curr: Curve, nsteps: Int = NSTEPS) {
        // @TODO: Here we could generate curve information to render in the shader with a plain simple quadratic bezier to reduce the number of points and make the curve as accurate as possible
        forEachRatio01(nsteps, include0 = false, include1 = false) {
            addTwoPoints(curr.calc(it), curr.normal(it), width)
        }
    }

    fun addAllCurvesPoints(curves: Curves, join: LineJoin = LineJoin.MITER, startCap: LineCap = LineCap.BUTT, endCap: LineCap = LineCap.BUTT, miterLimit: Double = 10.0) {
        val closed = curves.closed
        val curves = curves.curves
        for (n in curves.indices) {
            val curr = curves.getCyclic(n + 0)
            val next = curves.getCyclic(n + 1)

            // Generate start cap
            if (n == 0) {
                if (closed) {
                    addJoin(curves.getCyclic(n - 1), curr, join, miterLimit)
                } else {
                    addCap(curr, 0.0, startCap)
                }
            }

            // Generate intermediate points for curves (no for plain lines)
            if (curr.order != 1) {
                addCurvePoints(curr)
            }

            // Generate join
            if (n < curves.size - 1) {
                addJoin(curr, next, join, miterLimit)
            }
            // Generate end cap
            else {
                //println("closed=$closed")
                if (closed) {
                    addJoin(curr, next, join, miterLimit)
                } else {
                    addCap(curr, 1.0, endCap)
                }
            }
        }
    }

    fun strokePoints(): StrokePoints = StrokePoints(out, mode)
}

/** Useful for drawing */
fun Curves.toStrokePoints(width: Double, join: LineJoin = LineJoin.MITER, startCap: LineCap = LineCap.BUTT, endCap: LineCap = LineCap.BUTT, miterLimit: Double = 10.0, mode: StrokePointsMode = StrokePointsMode.NON_SCALABLE_POS): StrokePoints {
    //println("closed: $closed")
    return StrokePointsBuilder(width, mode).also {
        it.addAllCurvesPoints(this, join, startCap, endCap, miterLimit)
    }.strokePoints()
}
