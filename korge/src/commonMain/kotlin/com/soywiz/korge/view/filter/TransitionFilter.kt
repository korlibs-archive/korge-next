package com.soywiz.korge.view.filter

import com.soywiz.korag.AG
import com.soywiz.korag.DefaultShaders.t_Temp1
import com.soywiz.korag.shader.FragmentShader
import com.soywiz.korag.shader.Uniform
import com.soywiz.korag.shader.VarType
import com.soywiz.korag.shader.appending
import com.soywiz.korag.shader.storageFor
import com.soywiz.korag.shader.storageForTextureUnit
import com.soywiz.korge.debug.uiEditableValue
import com.soywiz.korge.render.RenderContext
import com.soywiz.korge.view.Views
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.bitmap.Bitmap32Context2d
import com.soywiz.korim.color.Colors
import com.soywiz.korim.paint.GradientPaint
import com.soywiz.korim.paint.LinearGradientPaint
import com.soywiz.korim.paint.RadialGradientPaint
import com.soywiz.korim.paint.SweepGradientPaint
import com.soywiz.korma.geom.vector.rect
import com.soywiz.korui.UiContainer

class TransitionFilter(
    var transition: Transition = Transition.CIRCULAR,
    reversed: Boolean = false,
    smooth: Boolean = true,
    ratio: Double = 1.0,
    filtering: Boolean = false,
) : ShaderFilter() {
    class Transition(val bmp: Bitmap) {
        fun inverted() = bmp.toBMP32().also { it.invert() }

        companion object {
            private val BMP_SIZE = 64

            private fun createTransitionBox(paint: GradientPaint): Transition {
                return Transition(Bitmap32Context2d(BMP_SIZE, BMP_SIZE) {
                    fill(paint.add(0.0, Colors.WHITE).add(1.0, Colors.BLACK)) {
                        rect(0, 0, BMP_SIZE, BMP_SIZE)
                    }
                })
            }
            private fun createLinearTransitionBox(x0: Int, y0: Int, x1: Int, y1: Int): Transition =
                createTransitionBox(LinearGradientPaint(x0, y0, x1, y1))

            val VERTICAL by lazy { createLinearTransitionBox(0, 0, 0, BMP_SIZE) }
            val HORIZONTAL by lazy { createLinearTransitionBox(0, 0, BMP_SIZE, 0) }
            val DIAGONAL1 by lazy { createLinearTransitionBox(0, 0, BMP_SIZE, BMP_SIZE) }
            val DIAGONAL2 by lazy { createLinearTransitionBox(BMP_SIZE, 0, 0, BMP_SIZE) }
            val CIRCULAR by lazy { createTransitionBox(RadialGradientPaint(BMP_SIZE / 2, BMP_SIZE / 2, 0, BMP_SIZE / 2, BMP_SIZE / 2, BMP_SIZE / 2)) }
            val SWEEP by lazy { createTransitionBox(SweepGradientPaint(BMP_SIZE / 2, BMP_SIZE / 2)) }
        }
    }

    companion object : BaseProgramProvider() {
        private val u_Reversed = Uniform("u_Reversed", VarType.Float1)
        private val u_Smooth = Uniform("u_Smooth", VarType.Float1)
        private val u_Ratio = Uniform("u_Ratio", VarType.Float1)
        private val u_Mask = Uniform("u_Mask", VarType.Sampler2D)

        override val fragment = Filter.DEFAULT_FRAGMENT.appending {
            val alpha = t_Temp1.x
            SET(alpha, texture2D(u_Mask, v_Tex01).r)
            IF(u_Reversed eq 1f.lit) {
                SET(t_Temp1.x, 1f.lit - t_Temp1.x)
            }
            SET(alpha, clamp(alpha + ((u_Ratio * 2f.lit) - 1f.lit), 0f.lit, 1f.lit))
            IF(u_Smooth ne 1f.lit) {
                IF(t_Temp1.x ge 1f.lit) {
                    SET(t_Temp1.x, 1f.lit)
                } ELSE {
                    SET(t_Temp1.x, 0f.lit)
                }
            }
            SET(out, (out * alpha))
            //SET(out, texture2D(u_Mask, v_Tex01))
            //SET(out, vec4(1.lit, 0.lit, 1.lit, 1.lit))
        }
    }

    init {
        this.filtering = filtering
    }

    override val programProvider: ProgramProvider get() = TransitionFilter
    private val textureUnit = AG.TextureUnit()
    private val s_ratio = uniforms.storageFor(u_Ratio)
    private val s_tex = uniforms.storageForTextureUnit(u_Mask, textureUnit)
    var reversed by uniforms.storageFor(u_Reversed).boolDelegateX(reversed)
    var smooth by uniforms.storageFor(u_Smooth).boolDelegateX(smooth)
    var ratio by s_ratio.doubleDelegateX(ratio)

    override fun updateUniforms(ctx: RenderContext, filterScale: Double) {
        textureUnit.texture = ctx.getTex(transition.bmp).base
    }

    override fun buildDebugComponent(views: Views, container: UiContainer) {
        container.uiEditableValue(::ratio)
        container.uiEditableValue(::smooth)
        container.uiEditableValue(::reversed)
        //container.uiEditableValue(::bitmap)
    }
}
