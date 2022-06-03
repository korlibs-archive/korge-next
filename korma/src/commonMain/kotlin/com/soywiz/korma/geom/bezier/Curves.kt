package com.soywiz.korma.geom.bezier

import com.soywiz.kds.iterators.fastForEach
import com.soywiz.korma.geom.BoundsBuilder
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.Rectangle
import com.soywiz.korma.math.convertRange
import kotlin.jvm.JvmName

@JvmName("ListCurves_toCurves")
fun List<Curves>.toCurves(closed: Boolean = false) = Curves(this.flatMap { it.curves }, closed)
@JvmName("ListCurve_toCurves")
fun List<Curve>.toCurves(closed: Boolean = false) = Curves(this, closed)

data class Curves(val curves: List<Curve>, val closed: Boolean) : Curve {
    override val order: Int get() = -1

    data class CurveInfo(
        val index: Int,
        val curve: Curve,
        val startLength: Double,
        val endLength: Double,
        val bounds: Rectangle,
    ) {
        fun contains(length: Double): Boolean = length in startLength..endLength

        val length: Double get() = endLength - startLength
    }

    val infos: List<CurveInfo> by lazy {
        var pos = 0.0
        curves.mapIndexed { index, curve ->
            val start = pos
            pos += curve.length()
            CurveInfo(index, curve, start, pos, curve.getBounds())
        }

    }
    val length: Double by lazy { infos.sumOf { it.length } }
    private val bb = BoundsBuilder()

    val CurveInfo.startRatio: Double get() = this.startLength / this@Curves.length
    val CurveInfo.endRatio: Double get() = this.endLength / this@Curves.length

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
                it.endLength < pos -> -1
                else -> +1
            }
        }
        return infos.getOrNull(index) ?: error("OUTSIDE")
    }

    @PublishedApi
    internal inline fun <T> findTInCurve(t: Double, block: (curve: Curve, ratioInCurve: Double) -> T): T {
        val pos = t * length
        val info = findInfo(t)
        val posInCurve = pos - info.startLength
        val ratioInCurve = posInCurve / info.length
        return block(info.curve, ratioInCurve)
    }

    override fun calc(t: Double, target: Point): Point {
        return findTInCurve(t) { curve, ratioInCurve -> curve.calc(ratioInCurve, target) }
    }

    override fun normal(t: Double, target: Point): Point {
        return findTInCurve(t) { curve, ratioInCurve -> curve.normal(ratioInCurve, target) }
    }

    override fun tangent(t: Double, target: Point): Point {
        return findTInCurve(t) { curve, ratioInCurve -> curve.tangent(ratioInCurve, target) }
    }

    override fun ratioFromLength(length: Double): Double {
        if (length <= 0.0) return 0.0
        if (length >= this.length) return 1.0

        val curveIndex = infos.binarySearch {
            when {
                length < it.startLength -> +1
                length > it.endLength -> -1
                else -> 0
            }
        }
        val index = if (curveIndex < 0) -curveIndex + 1 else curveIndex
        if (curveIndex < 0) {
            //infos.fastForEach { println("it=$it") }
            //println("length=${this.length}, requestedLength = $length, curveIndex=$curveIndex")
            return Double.NaN
        } // length not in curve!
        val info = infos[index]
        val lengthInCurve = length - info.startLength
        val ratioInCurve = info.curve.ratioFromLength(lengthInCurve)
        return ratioInCurve.convertRange(0.0, 1.0, info.startRatio, info.endRatio)
    }

    override fun length(steps: Int): Double = length
}
