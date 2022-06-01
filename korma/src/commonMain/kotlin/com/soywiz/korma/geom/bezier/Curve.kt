package com.soywiz.korma.geom.bezier

import com.soywiz.korma.geom.IPointArrayList
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.PointArrayList
import com.soywiz.korma.geom.Rectangle
import com.soywiz.korma.interpolation.interpolate
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min

interface Curve {
    fun getBounds(target: Rectangle = Rectangle()): Rectangle
    fun normal(t: Double, target: Point = Point()): Point
    fun calc(t: Double, target: Point = Point()): Point
    fun length(steps: Int = recommendedDivisions()): Double
    fun recommendedDivisions(): Int = DEFAULT_STEPS

    class Line(
        val x0: Double = 0.0, val y0: Double = 0.0,
        val x1: Double = 0.0, val y1: Double = 0.0,
    ) : Bezier {
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

        override fun toString(): String = "Curve.Line(($x0, $y0), ($x1, $y1))"
    }
    companion object {
        const val DEFAULT_STEPS = 100

        fun lineRecommendedSteps(x0: Double, y0: Double, x1: Double, y1: Double): Int {
            return DEFAULT_STEPS
        }
    }
}

fun Curve.getPoints(count: Int = this.recommendedDivisions(), out: PointArrayList = PointArrayList()): IPointArrayList {
    val c1 = count - 1
    val temp = Point()
    for (n in 0..c1) {
        val ratio = n.toDouble() / c1
        val point = calc(ratio, temp)
        //println("${this::class.simpleName}: ratio: $ratio, point=$point")
        out.add(point)
    }
    return out
}
