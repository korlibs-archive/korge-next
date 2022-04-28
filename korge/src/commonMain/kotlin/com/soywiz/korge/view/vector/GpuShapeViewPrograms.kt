package com.soywiz.korge.view.vector

import com.soywiz.korag.*
import com.soywiz.korag.shader.*
import com.soywiz.korge.internal.*
import kotlin.math.*

@KorgeInternal
object GpuShapeViewPrograms {
    val u_ProgramType = Uniform("u_ProgramType", VarType.Float1)
    val u_LineWidth = Uniform("u_LineWidth", VarType.Float1)
    val u_Color = Uniform("u_Color", VarType.Float4)
    val u_GlobalAlpha = Uniform("u_GlobalAlpha", VarType.Float1)
    val u_Transform = Uniform("u_Transform", VarType.Mat4)
    val u_Gradientp0 = Uniform("u_Gradientp0", VarType.Float3)
    val u_Gradientp1 = Uniform("u_Gradientp1", VarType.Float3)
    val a_Dist: Attribute = Attribute("a_Dist", VarType.Float1, normalized = false, precision = Precision.MEDIUM)
    val v_Dist: Varying = Varying("v_Dist", VarType.Float1, precision = Precision.MEDIUM)
    val LAYOUT = VertexLayout(DefaultShaders.a_Pos)
    val LAYOUT_TEX = VertexLayout(DefaultShaders.a_Tex)
    val LAYOUT_DIST = VertexLayout(a_Dist)
    val LAYOUT_FILL = VertexLayout(DefaultShaders.a_Pos, DefaultShaders.a_Tex)

    val LAYOUT_POS_DIST = VertexLayout(DefaultShaders.a_Pos, a_Dist)
    val LAYOUT_POS_TEX_FILL_DIST = VertexLayout(DefaultShaders.a_Pos, DefaultShaders.a_Tex, a_Dist)

    val LW = (u_LineWidth)
    val LW1 = Program.ExpressionBuilder { (LW - 1f.lit) }

    val VERTEX_FILL = VertexShaderDefault {
        SET(out, (u_ProjMat * u_ViewMat) * vec4(a_Pos, 0f.lit, 1f.lit))
        //SET(out, vec4(a_Pos, 0f.lit, 1f.lit))
        SET(v_Tex, DefaultShaders.a_Tex)
        SET(v_Dist, a_Dist)
    }
    val PROGRAM_STENCIL = Program(
        vertex = VertexShaderDefault { SET(out, (u_ProjMat * u_ViewMat) * vec4(a_Pos, 0f.lit, 1f.lit)) },
        fragment = FragmentShaderDefault { SET(out, vec4(1f.lit, 0f.lit, 0f.lit, 1f.lit)) },
    )

    fun Program.Builder.PREFIX() {
        IF(abs(v_Dist) ge LW) {
            DISCARD()
        }
    }

    fun Program.Builder.UPDATE_GLOBAL_ALPHA() {
        SET(out.a, out.a * u_GlobalAlpha)
        IF(abs(v_Dist) ge LW1) {
            //run {
            val aaAlpha = 1f.lit - (abs(v_Dist) - LW1)
            SET(out["a"], out["a"] * aaAlpha)
            //SET(out["a"], out["a"] * clamp(aaAlpha, 0f.lit, 1f.lit))
        }
    }

    val Operand.pow2: Operand get() = Program.ExpressionBuilder { pow(this@pow2, 2f.lit) }

    const val PROGRAM_TYPE_COLOR = 0
    const val PROGRAM_TYPE_BITMAP = 1
    const val PROGRAM_TYPE_GRADIENT_LINEAR = 2
    const val PROGRAM_TYPE_GRADIENT_RADIAL = 3
    const val PROGRAM_TYPE_GRADIENT_SWEEP = 4

    val PROGRAM_COMBINED = Program(
        vertex = VERTEX_FILL,
        fragment = FragmentShaderDefault {
            PREFIX()
            IF_ELSE_LIST(u_ProgramType, 0, 4) {
                when (it) {
                    // Color paint
                    PROGRAM_TYPE_COLOR -> {
                        SET(out, u_Color)
                    }
                    // Bitmap paint
                    PROGRAM_TYPE_BITMAP -> {
                        // @TODO: we should convert 0..1 to texture slice coordinates
                        SET(out, texture2D(u_Tex, fract(vec2((u_Transform * vec4(v_Tex, 0f.lit, 1f.lit))["xy"]))))
                    }
                    // Linear gradient paint
                    PROGRAM_TYPE_GRADIENT_LINEAR -> {
                        SET(out, texture2D(u_Tex, (u_Transform * vec4(v_Tex.x, v_Tex.y, 0f.lit, 1f.lit))["xy"]))
                    }
                    // Radial gradient paint
                    PROGRAM_TYPE_GRADIENT_RADIAL -> {
                        val rpoint = createTemp(VarType.Float2)
                        SET(rpoint["xy"], (u_Transform * vec4(v_Tex.x, v_Tex.y, 0f.lit, 1f.lit))["xy"])
                        val x = rpoint.x
                        val y = rpoint.y
                        val x0 = u_Gradientp0.x
                        val y0 = u_Gradientp0.y
                        val r0 = u_Gradientp0.z
                        val x1 = u_Gradientp1.x
                        val y1 = u_Gradientp1.y
                        val r1 = u_Gradientp1.z
                        val ratio = t_Temp0.x
                        val r0r1_2 = t_Temp0.y
                        val r0pow2 = t_Temp0.z
                        val r1pow2 = t_Temp0.w
                        val y0_y1 = t_Temp1.x
                        val x0_x1 = t_Temp1.y
                        val r0_r1 = t_Temp1.z
                        val radial_scale = t_Temp1.w

                        SET(r0r1_2, 2f.lit * r0 * r1)
                        SET(r0pow2, r0.pow2)
                        SET(r1pow2, r1.pow2)
                        SET(x0_x1, x0 - x1)
                        SET(y0_y1, y0 - y1)
                        SET(r0_r1, r0 - r1)
                        SET(radial_scale, 1f.lit / ((r0 - r1).pow2 - (x0 - x1).pow2 - (y0 - y1).pow2))

                        SET(
                            ratio,
                            1f.lit - (-r1 * r0_r1 + x0_x1 * (x1 - x) + y0_y1 * (y1 - y) - sqrt(r1pow2 * ((x0 - x).pow2 + (y0 - y).pow2) - r0r1_2 * ((x0 - x) * (x1 - x) + (y0 - y) * (y1 - y)) + r0pow2 * ((x1 - x).pow2 + (y1 - y).pow2) - (x1 * y0 - x * y0 - x0 * y1 + x * y1 + x0 * y - x1 * y).pow2)) * radial_scale
                        )
                        SET(out, texture2D(u_Tex, vec2(ratio, 0f.lit)))
                    }
                    // Sweep gradient paint
                    PROGRAM_TYPE_GRADIENT_SWEEP -> {
                        val rpoint = createTemp(VarType.Float2)
                        SET(rpoint["xy"], (u_Transform * vec4(v_Tex.x, v_Tex.y, 0f.lit, 1f.lit))["xy"])
                        val x = rpoint.x
                        val y = rpoint.y
                        val ratio = t_Temp0.x
                        val angle = t_Temp0.y
                        val x0 = u_Gradientp0.x
                        val y0 = u_Gradientp0.y
                        val PI2 = (PI * 2).toFloat().lit

                        SET(angle, atan(y - y0, x - x0))
                        IF(angle lt 0f.lit) { SET(angle, angle + PI2) }
                        SET(ratio, angle / PI2)
                        SET(out, texture2D(u_Tex, fract(vec2(ratio, 0f.lit))))
                    }
                }
            }
            UPDATE_GLOBAL_ALPHA()
        },
    )

    /*
    val PROGRAM_COLOR = Program(
        vertex = VERTEX_FILL,
        fragment = FragmentShaderDefault {
            PREFIX()
            SET(out, u_Color)
            UPDATE_GLOBAL_ALPHA()
        },
    )
    val PROGRAM_BITMAP = Program(
        vertex = VERTEX_FILL,
        fragment = FragmentShaderDefault {
            PREFIX()
            // @TODO: we should convert 0..1 to texture slice coordinates
            SET(out, texture2D(u_Tex, fract(vec2((u_Transform * vec4(v_Tex, 0f.lit, 1f.lit))["xy"]))))
            UPDATE_GLOBAL_ALPHA()
            //SET(out, vec4(1f.lit, 1f.lit, 0f.lit, 1f.lit))
        },
    )
    val PROGRAM_LINEAR_GRADIENT = Program(
        vertex = VERTEX_FILL,
        fragment = FragmentShaderDefault {
            PREFIX()
            SET(out, texture2D(u_Tex, (u_Transform * vec4(v_Tex.x, v_Tex.y, 0f.lit, 1f.lit))["xy"]))
            UPDATE_GLOBAL_ALPHA()
        },
    )
    val PROGRAM_RADIAL_GRADIENT = Program(
        vertex = VERTEX_FILL,
        fragment = FragmentShaderDefault {
            PREFIX()
            val rpoint = createTemp(VarType.Float2)
            SET(rpoint["xy"], (u_Transform * vec4(v_Tex.x, v_Tex.y, 0f.lit, 1f.lit))["xy"])
            val x = rpoint.x
            val y = rpoint.y
            val x0 = u_Gradientp0.x
            val y0 = u_Gradientp0.y
            val r0 = u_Gradientp0.z
            val x1 = u_Gradientp1.x
            val y1 = u_Gradientp1.y
            val r1 = u_Gradientp1.z
            val ratio = t_Temp0.x
            val r0r1_2 = t_Temp0.y
            val r0pow2 = t_Temp0.z
            val r1pow2 = t_Temp0.w
            val y0_y1 = t_Temp1.x
            val x0_x1 = t_Temp1.y
            val r0_r1 = t_Temp1.z
            val radial_scale = t_Temp1.w

            SET(r0r1_2, 2f.lit * r0 * r1)
            SET(r0pow2, r0.pow2)
            SET(r1pow2, r1.pow2)
            SET(x0_x1, x0 - x1)
            SET(y0_y1, y0 - y1)
            SET(r0_r1, r0 - r1)
            SET(radial_scale, 1f.lit / ((r0 - r1).pow2 - (x0 - x1).pow2 - (y0 - y1).pow2))

            SET(
                ratio,
                1f.lit - (-r1 * r0_r1 + x0_x1 * (x1 - x) + y0_y1 * (y1 - y) - sqrt(r1pow2 * ((x0 - x).pow2 + (y0 - y).pow2) - r0r1_2 * ((x0 - x) * (x1 - x) + (y0 - y) * (y1 - y)) + r0pow2 * ((x1 - x).pow2 + (y1 - y).pow2) - (x1 * y0 - x * y0 - x0 * y1 + x * y1 + x0 * y - x1 * y).pow2)) * radial_scale
            )
            SET(out, texture2D(u_Tex, vec2(ratio, 0f.lit)))
            UPDATE_GLOBAL_ALPHA()
        },
    )
    val PROGRAM_SWEEP_GRADIENT = Program(
        vertex = VERTEX_FILL,
        fragment = FragmentShaderDefault {
            PREFIX()
            val rpoint = createTemp(VarType.Float2)
            SET(rpoint["xy"], (u_Transform * vec4(v_Tex.x, v_Tex.y, 0f.lit, 1f.lit))["xy"])
            val x = rpoint.x
            val y = rpoint.y
            val ratio = t_Temp0.x
            val angle = t_Temp0.y
            val x0 = u_Gradientp0.x
            val y0 = u_Gradientp0.y
            val PI2 = (PI * 2).toFloat().lit

            SET(angle, atan(y - y0, x - x0))
            IF(angle lt 0f.lit) { SET(angle, angle + PI2) }
            SET(ratio, angle / PI2)
            SET(out, texture2D(u_Tex, fract(vec2(ratio, 0f.lit))))
            UPDATE_GLOBAL_ALPHA()
        },
    )
    */
}
