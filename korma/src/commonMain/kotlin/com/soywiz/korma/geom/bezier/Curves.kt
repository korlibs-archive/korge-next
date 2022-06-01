package com.soywiz.korma.geom.bezier

import com.soywiz.kds.iterators.fastForEach
import com.soywiz.korma.geom.BoundsBuilder
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.Rectangle
import kotlin.jvm.JvmName

@JvmName("ListCurves_toCurves")
fun List<Curves>.toCurves(closed: Boolean = false) = Curves(this.flatMap { it.curves }, closed)
@JvmName("ListCurve_toCurves")
fun List<Curve>.toCurves(closed: Boolean = false) = Curves(this, closed)

data class Curves(val curves: List<Curve>, val closed: Boolean) : Curve {
    data class CurveInfo(
        val curve: Curve,
        val start: Double,
        val end: Double,
        val bounds: Rectangle,
    ) {
        fun contains(pos: Double): Boolean = pos in start..end

        val length: Double get() = end - start
    }

    val infos: List<CurveInfo> by lazy {
        var pos = 0.0
        curves.map {
            val start = pos
            pos += it.length()
            CurveInfo(it, start, pos, it.getBounds())
        }
    }
    val length: Double by lazy { infos.sumOf { it.length } }
    private val bb = BoundsBuilder()

    override fun getBounds(target: Rectangle): Rectangle {
        bb.reset()
        infos.fastForEach { bb.addEvenEmpty(it.bounds) }
        return bb.getBounds(target)
    }

    @PublishedApi
    internal fun findInfo(t: Double): CurveInfo {
        val pos = t * length
        val index = infos.binarySearch {
            when {
                it.contains(pos) -> 0
                it.end < pos -> -1
                else -> +1
            }
        }
        return infos.getOrNull(index) ?: error("OUTSIDE")
    }

    @PublishedApi
    internal inline fun <T> findTInCurve(t: Double, block: (curve: Curve, ratioInCurve: Double) -> T): T {
        val pos = t * length
        val info = findInfo(t)
        val posInCurve = pos - info.start
        val ratioInCurve = posInCurve / info.length
        return block(info.curve, ratioInCurve)
    }

    override fun calc(t: Double, target: Point): Point {
        return findTInCurve(t) { curve, ratioInCurve -> curve.calc(ratioInCurve, target) }
    }

    override fun normal(t: Double, target: Point): Point {
        return findTInCurve(t) { curve, ratioInCurve -> curve.normal(ratioInCurve, target) }
    }

    override fun length(steps: Int): Double = length
}
