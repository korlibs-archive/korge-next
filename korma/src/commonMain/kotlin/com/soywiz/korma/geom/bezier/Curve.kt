package com.soywiz.korma.geom.bezier

import com.soywiz.kds.forEachRatio01
import com.soywiz.korma.geom.IPointArrayList
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.PointArrayList
import com.soywiz.korma.geom.Rectangle
import com.soywiz.korma.interpolation.interpolate
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

fun Curve.calcOffset(t: Double, offset: Double, out: Point = Point()): Point {
    calc(t, out)
    val px = out.x
    val py = out.y
    normal(t, out)
    val nx = out.x
    val ny = out.y
    return out.setTo(
        px + nx * offset,
        py + ny * offset,
    )
}

interface Curve {
    val order: Int
    fun getBounds(target: Rectangle = Rectangle()): Rectangle
    fun normal(t: Double, target: Point = Point()): Point
    fun tangent(t: Double, target: Point = Point()): Point
    fun calc(t: Double, target: Point = Point()): Point
    fun ratioFromLength(length: Double): Double = TODO()
    fun length(steps: Int = recommendedDivisions()): Double
    fun recommendedDivisions(): Int = DEFAULT_STEPS

    class Line(
        val x0: Double = 0.0, val y0: Double = 0.0,
        val x1: Double = 0.0, val y1: Double = 0.0,
    ) : Bezier {
        override val order: Int get() = 1
        val tangent = Point(x1 - x0, y1 - y0).also { it.normalize() }

        override fun getBounds(target: Rectangle): Rectangle =
            target.setBounds(min(x0, x1), min(y0, y1), max(x0, x1), max(y0, y1))

        override fun calc(t: Double, target: Point): Point =
            target.setTo(t.interpolate(x0, x1), t.interpolate(y0, y1))
        //.also { println("Line.calc[t=$t] -> $target") }

        override fun length(steps: Int): Double = hypot(x1 - x0, y1 - y0)

        override fun recommendedDivisions(): Int = lineRecommendedSteps(x0, y0, x1, y1)

        override fun normal(t: Double, target: Point): Point {
            target.copyFrom(tangent)
            target.setToNormal()
            return target
        }

        override fun tangent(t: Double, target: Point): Point {
            target.copyFrom(tangent)
            return target
        }

        override fun toString(): String = "Curve.Line(($x0, $y0), ($x1, $y1))"
    }
    companion object {
        const val DEFAULT_STEPS = 100

        fun lineRecommendedSteps(x0: Double, y0: Double, x1: Double, y1: Double): Int {
            return DEFAULT_STEPS
        }
    }
}

@PublishedApi
internal fun Curve._getPoints(count: Int = this.recommendedDivisions(), equidistant: Boolean = false, out: PointArrayList = PointArrayList()): IPointArrayList {
    val temp = Point()
    val curveLength = length()
    forEachRatio01(count) { ratio ->
        val t = if (equidistant) ratioFromLength(ratio * curveLength) else ratio
        val point = calc(t, temp)
        //println("${this::class.simpleName}: ratio: $ratio, point=$point")
        out.add(point)
    }
    return out
}

fun Curve.getPoints(count: Int = this.recommendedDivisions(), out: PointArrayList = PointArrayList()): IPointArrayList {
    return _getPoints(count, equidistant = false, out = out)
}

fun Curve.getEquidistantPoints(count: Int = this.recommendedDivisions(), out: PointArrayList = PointArrayList()): IPointArrayList {
    return _getPoints(count, equidistant = true, out = out)
}
