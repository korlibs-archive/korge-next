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
    private val gradientBitmap = Bitmap32(256, 1)

    data class PaintShader(
        val uniforms: AG.UniformValues = AG.UniformValues(),
        val texUniforms: AG.UniformValues = AG.UniformValues(),
        val program: Program = GpuShapeViewPrograms.PROGRAM_COMBINED
    )

    val stencilPaintShader = PaintShader(
        AG.UniformValues(GpuShapeViewPrograms.u_ProgramType to GpuShapeViewPrograms.PROGRAM_TYPE_STENCIL.toFloat(),),
        AG.UniformValues(),
    )

    fun paintToShaderInfo(
        stateTransform: Matrix,
        matrix: Matrix?,
        paint: Paint,
        globalAlpha: Double,
        lineWidth: Double,
    ): PaintShader? = when (paint) {
        is NonePaint -> {
            null
        }
        is ColorPaint -> {
            PaintShader(AG.UniformValues(
                GpuShapeViewPrograms.u_ProgramType to GpuShapeViewPrograms.PROGRAM_TYPE_COLOR.toFloat(),
                GpuShapeViewPrograms.u_Color to FloatArray(4).also { paint.writeFloat(it) },
                GpuShapeViewPrograms.u_GlobalAlpha to globalAlpha.toFloat(),
                GpuShapeViewPrograms.u_LineWidth to lineWidth.toFloat(),
            ), AG.UniformValues())

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
            PaintShader(AG.UniformValues(
                GpuShapeViewPrograms.u_ProgramType to GpuShapeViewPrograms.PROGRAM_TYPE_BITMAP.toFloat(),
                GpuShapeViewPrograms.u_Transform to mat.toMatrix3D(), // @TODO: Why is this transposed???
                GpuShapeViewPrograms.u_GlobalAlpha to globalAlpha.toFloat(),
                GpuShapeViewPrograms.u_LineWidth to lineWidth.toFloat(),
            //}, GpuShapeView.PROGRAM_BITMAP)
            ), AG.UniformValues(
                DefaultShaders.u_Tex to paint.bitmap
            ))
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
            PaintShader(
                AG.UniformValues(
                    GpuShapeViewPrograms.u_ProgramType to when (paint.kind) {
                        GradientKind.RADIAL -> GpuShapeViewPrograms.PROGRAM_TYPE_GRADIENT_RADIAL
                        GradientKind.SWEEP -> GpuShapeViewPrograms.PROGRAM_TYPE_GRADIENT_SWEEP
                        else -> GpuShapeViewPrograms.PROGRAM_TYPE_GRADIENT_LINEAR
                    },
                    GpuShapeViewPrograms.u_Transform to mat.toMatrix3D(),
                    GpuShapeViewPrograms.u_Gradientp0 to floatArrayOf(paint.x0.toFloat(), paint.y0.toFloat(), paint.r0.toFloat()),
                    GpuShapeViewPrograms.u_Gradientp1 to floatArrayOf(paint.x1.toFloat(), paint.y1.toFloat(), paint.r1.toFloat()),
                    GpuShapeViewPrograms.u_GlobalAlpha to globalAlpha.toFloat(),
                    GpuShapeViewPrograms.u_LineWidth to lineWidth.toFloat(),
                ), AG.UniformValues(
                    DefaultShaders.u_Tex to gradientBitmap
                )
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
