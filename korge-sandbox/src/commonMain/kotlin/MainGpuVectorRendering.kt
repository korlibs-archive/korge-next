import com.soywiz.kmem.*
import com.soywiz.korag.*
import com.soywiz.korag.shader.*
import com.soywiz.korge.render.*
import com.soywiz.korge.view.*
import com.soywiz.korge.view.BlendMode
import com.soywiz.korim.color.*
import com.soywiz.korim.paint.*
import com.soywiz.korim.vector.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.shape.*
import com.soywiz.korma.geom.vector.*

suspend fun Stage.mainGpuVectorRendering() {
    //solidRect(0.25, 0.25, Colors.RED).xy(120, 100)
    //solidRect(0.5, 0.5, Colors.RED).xy(110, 100)
    //solidRect(1.0, 1.0, Colors.RED).xy(100, 100)
    addChild(GpuShapeView(buildShape {
        fill(Colors.BLUE) {
            rect(-100, -100, 500, 500)
            rectHole(40, 40, 320, 320)
        }
        fill(Colors.RED) {
            regularPolygon(6, 100.0)
            //rect(-100, -100, 500, 500)
            //rectHole(40, 40, 320, 320)
        }
    }).xy(300, 300).rotation(45.degrees))
}

class GpuShapeView(val shape: Shape) : View() {
    companion object {
        val u_Color = Uniform("u_Color", VarType.Float4)
        val LAYOUT = VertexLayout(DefaultShaders.a_Pos)
        val VERTEX = VertexShader {
            SET(out, DefaultShaders.u_ProjMat * DefaultShaders.u_ViewMat * vec4(DefaultShaders.a_Pos, 0f.lit, 1f.lit))
        }
        val VERTEX_01 = VertexShader {
            SET(out, vec4(DefaultShaders.a_Pos, 0f.lit, 1f.lit))
        }
        val PROGRAM = Program(
            vertex = VERTEX,
            fragment = FragmentShader {
                //SET(out, vec4(1f.lit, 0f.lit, 0f.lit, .5f.lit))
                SET(out, vec4(1f.lit, 0f.lit, 0f.lit, 1f.lit))
            },
        )
        val COLOR_PROGRAM = Program(
            vertex = VERTEX,
            fragment = FragmentShader {
                SET(out, u_Color)
            },
        )
    }

    override fun renderInternal(ctx: RenderContext) {
        ctx.flush()
        val currentRenderBuffer = ctx.ag.currentRenderBufferOrMain
        ctx.renderToTexture(currentRenderBuffer.width, currentRenderBuffer.height, {
            renderShape(ctx, shape)
        }) { texture ->
            ctx.useBatcher {
                it.drawQuad(texture, x = 0f, y = 0f)
            }
        }
    }

    private fun renderShape(ctx: RenderContext, shape: Shape) {
        when (shape) {
            is FillShape -> renderShape(ctx, shape)
            is CompoundShape -> for (v in shape.components) renderShape(ctx, v)
            EmptyShape -> Unit
            else -> TODO()
        }
    }

    private fun renderShape(ctx: RenderContext, shape: FillShape) {
        val path = shape.path
        val points = PointArrayList()
        val m = globalMatrix
        path.emitPoints2 { x, y, move ->
            points.add(m.transformX(x, y), m.transformY(x, y))
        }

        ctx.dynamicVertexBufferPool { vertices ->
            val data = FloatArray(points.size * 2 + 4)
            for (n in 0 until points.size + 1) {
                data[(n + 1) * 2 + 0] = points.getX(n % points.size).toFloat()
                data[(n + 1) * 2 + 1] = points.getY(n % points.size).toFloat()
            }
            var sumX = 0.0
            var sumY = 0.0
            for (n in 0 until points.size) {
                sumX += points.getX(n)
                sumY += points.getY(n)
            }
            data[0] = (sumX / points.size).toFloat()
            data[1] = (sumY / points.size).toFloat()
            vertices.upload(data)
            ctx.batch.updateStandardUniforms()

            if (shape.path.winding != Winding.EVEN_ODD) {
                error("Currently only supported EVEN_ODD winding")
            }

            ctx.ag.clearStencil(0xFF)
            ctx.ag.draw(
                vertices = vertices,
                program = PROGRAM,
                type = AG.DrawType.TRIANGLE_FAN,
                vertexLayout = LAYOUT,
                vertexCount = points.size + 1,
                uniforms = ctx.batch.uniforms,
                stencil = AG.StencilState(
                    enabled = true,
                    readMask = 0xFF,
                    compareMode = AG.CompareMode.ALWAYS,
                    referenceValue = 0xFF,
                    writeMask = 0xFF,
                    actionOnDepthFail = AG.StencilOp.KEEP,
                    actionOnDepthPassStencilFail = AG.StencilOp.KEEP,
                    actionOnBothPass = AG.StencilOp.INVERT,
                ),
                blending = BlendMode.NONE.factors,
                colorMask = AG.ColorMaskState(false, false, false, false),
            )
        }
        renderFill(ctx, shape.paint)
    }

    private fun renderFill(ctx: RenderContext, paint: Paint) {
        ctx.dynamicVertexBufferPool { vertices ->
            val data = FloatArray(4 * 2)
            var n = 0
            val currentRenderBuffer = ctx.ag.currentRenderBufferOrMain
            val w = currentRenderBuffer.width.toFloat()
            val h = currentRenderBuffer.height.toFloat()
            data[n++] = 0f; data[n++] = 0f
            data[n++] = w; data[n++] = 0f
            data[n++] = w; data[n++] = h
            data[n++] = 0f; data[n++] = h

            vertices.upload(data)
            ctx.useBatcher { batch ->
                val color = (paint as RGBA)
                batch.updateStandardUniforms()
                batch.setTemporalUniform(u_Color, floatArrayOf(color.rf, color.gf, color.bf, color.af)) {
                    ctx.ag.draw(
                        vertices = vertices,
                        program = COLOR_PROGRAM,
                        type = AG.DrawType.TRIANGLE_FAN,
                        vertexLayout = LAYOUT,
                        vertexCount = 4,
                        uniforms = ctx.batch.uniforms,
                        stencil = AG.StencilState(
                            enabled = true,
                            compareMode = AG.CompareMode.EQUAL,
                            writeMask = 0,
                        ),
                        blending = BlendMode.NONE.factors,
                        colorMask = AG.ColorMaskState(true, true, true, true),
                    )
                }
            }
        }
    }
}
