import com.soywiz.korag.*
import com.soywiz.korag.shader.*
import com.soywiz.korge.render.*
import com.soywiz.korge.view.*
import com.soywiz.korge.view.BlendMode
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.*
import com.soywiz.korim.format.*
import com.soywiz.korim.paint.*
import com.soywiz.korim.vector.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.shape.*
import com.soywiz.korma.geom.vector.*

suspend fun Stage.mainGpuVectorRendering() {
    val korgeBitmap = resourcesVfs["korge.png"].readBitmap()

    fun Context2d.buildGraphics() {
        /*
       fill(Colors.BLUE) {
           rect(-100, -100, 500, 500)
           rectHole(40, 40, 320, 320)
       }
       fill(Colors.RED) {
           regularPolygon(6, 100.0)
           //rect(-100, -100, 500, 500)
           //rectHole(40, 40, 320, 320)
       }
       fill(Colors.YELLOW) {
           this.circle(100, 100, 100)
           //rect(-100, -100, 500, 500)
           //rectHole(40, 40, 320, 320)
       }
       */
        translate(100, 100)
        scale(2.0)
        globalAlpha = 0.75
        fillStyle = BitmapPaint(korgeBitmap, Matrix().translate(50, 50).scale(0.125), cycleX = CycleMethod.REPEAT, cycleY = CycleMethod.REPEAT)
        fillRect(0.0, 0.0, 100.0, 100.0)
        fillStyle = createLinearGradient(0.0, 0.0, 200.0, 200.0, transform = Matrix().scale(0.5).pretranslate(130, 30))
            .addColorStop(0.0, Colors.RED)
            .addColorStop(1.0, Colors.BLUE)
        fillRect(100.0, 0.0, 100.0, 100.0)
    }

    //solidRect(0.25, 0.25, Colors.RED).xy(120, 100)
    //solidRect(0.5, 0.5, Colors.RED).xy(110, 100)
    //solidRect(1.0, 1.0, Colors.RED).xy(100, 100)
    gpuShapeView { buildGraphics() }.xy(0, 0)//.rotation(45.degrees)

    //val bitmap = korgeBitmap.resized(100, 100, ScaleMode.FILL, Anchor.CENTER)
    //image(bitmap)
    image(NativeImage(512, 512).context2d { buildGraphics() }).xy(700, 0)
    image(Bitmap32(512, 512).context2d { buildGraphics() }).xy(700, 300)
}

inline fun Container.gpuShapeView(buildContext2d: Context2d.() -> Unit)
    = GpuShapeView(buildShape { buildContext2d() }).addTo(this)

inline fun Container.gpuShapeView(shape: Shape, callback: @ViewDslMarker GpuShapeView.() -> Unit = {})
    = GpuShapeView(shape).addTo(this, callback)

class GpuShapeView(var shape: Shape) : View() {
    companion object {
        val u_Color = Uniform("u_Color", VarType.Float4)
        val u_Transform = Uniform("u_Transform", VarType.Mat4)
        val LAYOUT = VertexLayout(DefaultShaders.a_Pos)
        val VERTEX = VertexShader { DefaultShaders {
            SET(v_Tex, a_Pos)
            SET(out, u_ProjMat * u_ViewMat * vec4(a_Pos, 0f.lit, 1f.lit))
        } }
        val VERTEX_01 = VertexShader {
            DefaultShaders {
                //SET(v_Tex, a_Tex)
                SET(out, vec4(a_Pos, 0f.lit, 1f.lit))
            }
        }
        val PROGRAM = Program(
            vertex = VERTEX,
            fragment = FragmentShader {
                //SET(out, vec4(1f.lit, 0f.lit, 0f.lit, .5f.lit))
                SET(out, vec4(1f.lit, 0f.lit, 0f.lit, 1f.lit))
            },
        )
        val PROGRAM_COLOR = Program(
            vertex = VERTEX,
            fragment = FragmentShader { DefaultShaders {
                SET(out, u_Color)
            } },
        )
        val PROGRAM_BITMAP = Program(
            vertex = VERTEX,
            fragment = FragmentShader { DefaultShaders {
                //SET(out, vec4(1f.lit, 0f.lit, 1f.lit, 1f.lit))
                SET(out, texture2D(u_Tex, fract(vec2((vec4(v_Tex, 0f.lit, 1f.lit) * u_Transform)["xy"]))))
                //SET(out, vec4(v_Tex / 512f.lit, 0f.lit, 1f.lit))
            } },
        )
        val PROGRAM_LINEAR_GRADIENT = Program(
            vertex = VERTEX,
            fragment = FragmentShader { DefaultShaders {
                SET(out, texture2D(u_Tex, (vec4(v_Tex.x, v_Tex.y, 0f.lit, 1f.lit) * u_Transform)["xy"]))
            } },
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
        renderFill(ctx, shape.paint, shape.transform)
    }

    private val colorUniforms = AG.UniformValues()
    private val bitmapUniforms = AG.UniformValues()
    private val gradientUniforms = AG.UniformValues()
    private val gradientBitmap = Bitmap32(256, 1)

    private fun renderFill(ctx: RenderContext, paint: Paint, transform: Matrix) {
        if (paint is NonePaint) return

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
                batch.updateStandardUniforms()
                var uniforms: AG.UniformValues = colorUniforms
                var program: Program = PROGRAM_COLOR
                when (paint) {
                    is ColorPaint -> {
                        val color = paint
                        colorUniforms[u_Color] = floatArrayOf(color.rf, color.gf, color.bf, color.af)
                        program = PROGRAM_COLOR
                        uniforms = colorUniforms
                    }
                    is BitmapPaint -> {
                        bitmapUniforms[DefaultShaders.u_Tex] = AG.TextureUnit(ctx.getTex(paint.bitmap).base)
                        bitmapUniforms[u_Transform] = (transform * paint.transform * Matrix().scale(1.0 / paint.bitmap.width, 1.0 / paint.bitmap.height)).toMatrix3D()
                        program = PROGRAM_BITMAP
                        uniforms = bitmapUniforms
                    }
                    is GradientPaint -> {
                        paint.fillColors(gradientBitmap.dataPremult)
                        gradientUniforms[DefaultShaders.u_Tex] = AG.TextureUnit(ctx.getTex(gradientBitmap).base)
                        val mat = paint.transform * transform * paint.gradientMatrix
                        gradientUniforms[u_Transform] = mat.toMatrix3D()
                        program = PROGRAM_LINEAR_GRADIENT
                        uniforms = gradientUniforms
                    }
                    else -> {
                        TODO("paint=$paint")
                    }
                }
                batch.setTemporalUniforms(uniforms) {
                    ctx.ag.draw(
                        vertices = vertices,
                        program = program,
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
