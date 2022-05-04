package com.soywiz.korge.view.vector

import com.soywiz.korag.*
import com.soywiz.korag.shader.*
import com.soywiz.korge.internal.*
import com.soywiz.korge.render.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.*
import com.soywiz.korim.paint.*
import com.soywiz.korma.geom.*

@KorgeInternal
class GpuShapeViewPaintShader {
    private val colorUniforms = AG.UniformValues()
    private val bitmapUniforms = AG.UniformValues()
    private val gradientUniforms = AG.UniformValues()
    private val texUniforms = AG.UniformValues()
    private val gradientBitmap = Bitmap32(256, 1)

    private val colorF = FloatArray(4)
    internal val tempPaintShader = PaintShader()

    data class PaintShader(
        var uniforms: AG.UniformValues = AG.UniformValues(),
        var texUniforms: AG.UniformValues = AG.UniformValues(),
        var program: Program = DefaultShaders.PROGRAM_DEFAULT
    ) {
        fun setTo(uniforms: AG.UniformValues, texUniforms: AG.UniformValues, program: Program): PaintShader {
            this.uniforms.setTo(uniforms)
            this.texUniforms.setTo(texUniforms)
            this.program = program
            return this
        }
    }

    val stencilPaintShader = PaintShader(
        AG.UniformValues(GpuShapeViewPrograms.u_ProgramType to GpuShapeViewPrograms.PROGRAM_TYPE_COLOR.toFloat(),),
        AG.UniformValues(),
        GpuShapeViewPrograms.PROGRAM_COMBINED
    )

    fun paintToShaderInfo(
        stateTransform: Matrix,
        matrix: Matrix?,
        paint: Paint,
        globalAlpha: Double,
        lineWidth: Double,
        out: PaintShader = PaintShader()
    ): PaintShader? = when (paint) {
        is NonePaint -> {
            null
        }
        is ColorPaint -> {
            val color = paint
            color.writeFloat(colorF)
            out.setTo(colorUniforms.also { uniforms ->
                uniforms[GpuShapeViewPrograms.u_Color] = colorF
                uniforms[GpuShapeViewPrograms.u_GlobalAlpha] = globalAlpha.toFloat()
                uniforms[GpuShapeViewPrograms.u_LineWidth] = lineWidth.toFloat()
                uniforms[GpuShapeViewPrograms.u_ProgramType] = GpuShapeViewPrograms.PROGRAM_TYPE_COLOR.toFloat()
            //}, GpuShapeView.PROGRAM_COLOR)
            }, texUniforms.also { uniforms ->
            }, GpuShapeViewPrograms.PROGRAM_COMBINED)

        }
        is BitmapPaint -> {
            val mat = Matrix().apply {
                identity()
                preconcat(paint.transform)
                preconcat(stateTransform)
                if (matrix != null) preconcat(matrix)
                invert()
                scale(1.0 / paint.bitmap.width, 1.0 / paint.bitmap.height)
            }

            //val mat = (paint.transform * stateTransform)
            //mat.scale(1.0 / paint.bitmap.width, 1.0 / paint.bitmap.height)
            //println("mat=$mat")
            out.setTo(bitmapUniforms.also { uniforms ->
                uniforms[GpuShapeViewPrograms.u_Transform] = mat.toMatrix3D() // @TODO: Why is this transposed???
                uniforms[GpuShapeViewPrograms.u_GlobalAlpha] = globalAlpha.toFloat()
                uniforms[GpuShapeViewPrograms.u_LineWidth] = lineWidth.toFloat()
                uniforms[GpuShapeViewPrograms.u_ProgramType] = GpuShapeViewPrograms.PROGRAM_TYPE_BITMAP.toFloat()
            //}, GpuShapeView.PROGRAM_BITMAP)
            }, texUniforms.also { uniforms ->
                uniforms[DefaultShaders.u_Tex] = paint.bitmap
            }, GpuShapeViewPrograms.PROGRAM_COMBINED)
        }
        is GradientPaint -> {
            gradientBitmap.lock {
                paint.fillColors(gradientBitmap.dataPremult)
            }

            val npaint = paint.copy(transform = Matrix().apply {
                identity()
                preconcat(paint.transform)
                preconcat(stateTransform)
                if (matrix != null) preconcat(matrix)
            })
            //val mat = stateTransform * paint.gradientMatrix
            val mat = when (paint.kind) {
                GradientKind.LINEAR -> npaint.gradientMatrix
                else -> npaint.transform.inverted()
            }
            out.setTo(
                gradientUniforms.also { uniforms ->
                    uniforms[GpuShapeViewPrograms.u_Transform] = mat.toMatrix3D()
                    uniforms[GpuShapeViewPrograms.u_Gradientp0] =
                        floatArrayOf(paint.x0.toFloat(), paint.y0.toFloat(), paint.r0.toFloat())
                    uniforms[GpuShapeViewPrograms.u_Gradientp1] =
                        floatArrayOf(paint.x1.toFloat(), paint.y1.toFloat(), paint.r1.toFloat())
                    uniforms[GpuShapeViewPrograms.u_GlobalAlpha] = globalAlpha.toFloat()
                    uniforms[GpuShapeViewPrograms.u_LineWidth] = lineWidth.toFloat()
                    uniforms[GpuShapeViewPrograms.u_ProgramType] = when (paint.kind) {
                        GradientKind.RADIAL -> GpuShapeViewPrograms.PROGRAM_TYPE_GRADIENT_RADIAL
                        GradientKind.SWEEP -> GpuShapeViewPrograms.PROGRAM_TYPE_GRADIENT_SWEEP
                        else -> GpuShapeViewPrograms.PROGRAM_TYPE_GRADIENT_LINEAR
                    }
                }, texUniforms.also { uniforms ->
                    uniforms[DefaultShaders.u_Tex] = gradientBitmap
                }, GpuShapeViewPrograms.PROGRAM_COMBINED
                //when (paint.kind) {
                //    GradientKind.RADIAL -> GpuShapeView.PROGRAM_RADIAL_GRADIENT
                //    GradientKind.SWEEP -> GpuShapeView.PROGRAM_SWEEP_GRADIENT
                //    else -> GpuShapeView.PROGRAM_LINEAR_GRADIENT
                //}
            )
        }
        else -> {
            TODO("paint=$paint")
        }
    }
}
