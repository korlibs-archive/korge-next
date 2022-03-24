package com.soywiz.korim.paint

import com.soywiz.kmem.clamp01
import com.soywiz.korim.bitmap.Bitmap32
import com.soywiz.korim.bitmap.Bitmaps
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.color.RGBAPremultiplied
import com.soywiz.korim.color.RgbaPremultipliedArray
import com.soywiz.korim.vector.*
import com.soywiz.korma.geom.*

abstract class BaseFiller {
    abstract fun fill(data: RgbaPremultipliedArray, offset: Int, x0: Int, x1: Int, y: Int)
}

object NoneFiller : BaseFiller() {
    override fun fill(data: RgbaPremultipliedArray, offset: Int, x0: Int, x1: Int, y: Int) = Unit
}

class ColorFiller : BaseFiller() {
    private var color: RGBAPremultiplied = Colors.RED.premultiplied

    fun set(fill: ColorPaint, state: Context2d.State) = this.apply {
        this.color = fill.color.premultiplied
        //println("ColorFiller: $color")
    }

    override fun fill(data: RgbaPremultipliedArray, offset: Int, x0: Int, x1: Int, y: Int) {
        data.fill(color, offset + x0, offset + x1 + 1)
    }
}

class BitmapFiller : BaseFiller() {
    private var cycleX: CycleMethod = CycleMethod.NO_CYCLE
    private var cycleY: CycleMethod = CycleMethod.NO_CYCLE
    private var texture: Bitmap32 = Bitmaps.transparent.bmp
    private var transform: Matrix = Matrix()
    private var linear: Boolean = true
    private val stateTrans = Matrix()
    private val fillTrans = Matrix()
    private val compTrans = Matrix()

    fun set(fill: BitmapPaint, state: Context2d.State) = this.apply {
        this.cycleX = fill.cycleX
        this.cycleY = fill.cycleY
        this.texture = fill.bmp32
        this.transform = fill.transform
        this.linear = fill.smooth
        state.transform.inverted(this.stateTrans)
        fill.transform.inverted(this.fillTrans)
        compTrans.apply {
            identity()
            multiply(this, stateTrans)
            multiply(this, fillTrans)
        }
    }

    fun lookupLinear(x: Double, y: Double): RGBA = texture.getRgbaSampled(x, y)
    fun lookupNearest(x: Double, y: Double): RGBA = texture[x.toInt(), y.toInt()]

    override fun fill(data: RgbaPremultipliedArray, offset: Int, x0: Int, x1: Int, y: Int) {
        /*
        val total = ((x1 - x0) + 1).toDouble()
        val tx0 = compTrans.transformX(x0, y)
        val ty0 = compTrans.transformY(x0, y)
        val tx1 = compTrans.transformX(x1, y)
        val ty1 = compTrans.transformY(x1, y)

        for (n in x0..x1) {
            val ratio = n / total
            val tx = ratio.interpolate(tx0, tx1)
            val ty = ratio.interpolate(ty0, ty1)
            val color = if (linear) lookupLinear(tx, ty) else lookupNearest(tx, ty)
            data[n] = color.premultiplied
        }
        */
        for (n in x0..x1) {
            val tx = cycleX.apply(compTrans.transformX(n.toDouble(), y.toDouble()), texture.width.toDouble())
            val ty = cycleY.apply(compTrans.transformY(n.toDouble(), y.toDouble()), texture.height.toDouble())
            val color = if (linear) lookupLinear(tx, ty) else lookupNearest(tx, ty)
            //println("($tx, $ty)")
            data[offset + n] = color.premultiplied
        }
    }
}

class GradientFiller : BaseFiller() {
    companion object {
        const val NCOLORS = 256
    }
    private val colors = RgbaPremultipliedArray(NCOLORS)
    private lateinit var fill: GradientPaint
    private val stateInv: Matrix = Matrix()

    fun set(fill: GradientPaint, state: Context2d.State) = this.apply {
        this.fill = fill
        state.transform.inverted(this.stateInv)

        fill.fillColors(colors)
    }

    private fun color(ratio: Double): RGBAPremultiplied = colors[(ratio.clamp01() * (NCOLORS - 1)).toInt()]

    // @TODO: Radial gradient
    // @TODO: This doesn't seems to work proprely
    override fun fill(data: RgbaPremultipliedArray, offset: Int, x0: Int, x1: Int, y: Int) {
        //for (n in x0..x1) data[n] = color(mat.transformX(n.toDouble(), y.toDouble()).clamp01())
        for (n in x0..x1) data[offset + n] = color(fill.getRatioAt(n.toDouble(), y.toDouble(), stateInv))
    }
}
