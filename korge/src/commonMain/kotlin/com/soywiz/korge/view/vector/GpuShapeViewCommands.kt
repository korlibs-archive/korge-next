package com.soywiz.korge.view.vector

import com.soywiz.kds.*
import com.soywiz.kds.iterators.*
import com.soywiz.korag.*
import com.soywiz.korag.shader.*
import com.soywiz.korge.internal.*
import com.soywiz.korge.render.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korma.geom.*

@KorgeInternal
class GpuShapeViewCommands {
    private var vertexIndex = 0
    private val bufferVertexData = floatArrayListOf()
    private val commands = arrayListOf<ICommand>()
    private var vertices: AgCachedBuffer? = null
    private val verticesToDelete = FastArrayList<AgCachedBuffer>()

    fun clear() {
        vertexIndex = 0
        bufferVertexData.clear()
        commands.clear()
    }

    fun addVertex(x: Float, y: Float, u: Float = x, v: Float = y, lw: Float = 0f) {
        bufferVertexData.add(x, y, u, v, lw)
        vertexIndex++
    }

    private var verticesStartIndex: Int = 0
    fun verticesStart(): Int {
        verticesStartIndex = vertexIndex
        return verticesStartIndex
    }

    fun verticesEnd(): Int {
        return vertexIndex
    }

    fun draw(
        drawType: AG.DrawType,
        paintShader: GpuShapeViewPaintShader.PaintShader?,
        colorMask: AG.ColorMaskState? = null,
        stencil: AG.StencilState? = null,
        blendMode: AG.Blending? = null,
        startIndex: Int = this.verticesStartIndex,
        endIndex: Int = this.vertexIndex
    ) {
        commands += ShapeCommand(
            drawType = drawType,
            vertexIndex = startIndex,
            vertexCount = endIndex - startIndex,
            paintShader = paintShader,
            colorMask = colorMask,
            stencil = stencil,
            blendMode = blendMode,
        )
    }

    fun clearStencil(i: Int = 0, scissor: AG.Scissor? = null) {
        commands += ClearCommand(i, scissor)
    }

    fun setScissor(scissor: Rectangle) {
        commands += ScissorCommand(scissor)
    }

    fun finish() {
        vertices?.let { verticesToDelete += it }
        vertices = AgCachedBuffer(AG.Buffer.Kind.VERTEX, bufferVertexData)
    }

    private val decomposed = Matrix.Transform()
    fun render(ctx: RenderContext, globalMatrix: Matrix, localMatrix: Matrix, globalAlpha: Double) {
        val vertices = this.vertices ?: return
        ctx.agBufferManager.delete(verticesToDelete)
        verticesToDelete.clear()

        ctx.flush()
        val ag = ctx.ag
        ctx.useBatcher { batcher ->
            batcher.updateStandardUniforms()
            batcher.setTemporalUniform(GpuShapeViewPrograms.u_GlobalAlpha, globalAlpha.toFloat()) {
                batcher.setViewMatrixTemp(globalMatrix) {
                    globalMatrix.decompose(decomposed)
                    // @TODO: Use this scale
                    decomposed.scaleX
                    decomposed.scaleY
                    ag.commandsNoWait { list ->
                        //ag.commandsSync { list ->
                        // Set to default state
                        //list.useProgram(ag.getProgram(GpuShapeViewPrograms.PROGRAM_COMBINED))
                        //println(bufferVertexData)
                        list.useProgram(ag, GpuShapeViewPrograms.PROGRAM_COMBINED)
                        //list.vertexArrayObjectSet(ag, GpuShapeViewPrograms.LAYOUT_POS_TEX_FILL_DIST, bufferVertexData) {
                        list.vertexArrayObjectSet(
                            AG.VertexArrayObject(
                                fastArrayListOf(
                                    AG.VertexData(
                                        ctx.getBuffer(
                                            vertices
                                        ), GpuShapeViewPrograms.LAYOUT_POS_TEX_FILL_DIST
                                    )
                                )
                            )
                        ) {
                            commands.fastForEach { cmd ->
                                when (cmd) {
                                    is ScissorCommand -> {
                                        val rect = cmd.scissor.clone()
                                        rect.applyTransform(globalMatrix)
                                        list.setScissorState(ag, AG.Scissor().setTo(rect))
                                    }
                                    is ClearCommand -> {
                                        list.clearStencil(cmd.i)
                                        list.clear(false, false, true)
                                    }
                                    is ShapeCommand -> {
                                        val paintShader = cmd.paintShader
                                        //println("cmd.vertexCount=${cmd.vertexCount}, cmd.vertexIndex=${cmd.vertexIndex}, paintShader=$paintShader")
                                        batcher.simulateBatchStats(cmd.vertexCount)
                                        //println(paintShader.uniforms)
                                        paintShader?.uniforms?.let { resolve(ctx, it, paintShader.texUniforms) }
                                        batcher.setTemporalUniforms(paintShader?.uniforms) {
                                            list.uniformsSet(it) {
                                                list.setStencilState(cmd.stencil)
                                                list.setColorMaskState(cmd.colorMask)
                                                list.setBlendingState(cmd.blendMode)
                                                //println(ctx.batch.viewMat2D)
                                                list.draw(cmd.drawType, cmd.vertexCount, cmd.vertexIndex)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun resolve(ctx: RenderContext, uniforms: AG.UniformValues, texUniforms: AG.UniformValues) {
        texUniforms.fastForEach { uniform, value ->
            if (value is Bitmap) {
                uniforms[uniform] = AG.TextureUnit(ctx.getTex(value).base)
            }
        }
    }

    sealed interface ICommand

    data class ScissorCommand(val scissor: Rectangle) : ICommand

    data class ClearCommand(val i: Int, val scissor: AG.Scissor?) : ICommand

    data class ShapeCommand(
        var drawType: AG.DrawType = AG.DrawType.LINE_STRIP,
        var vertexIndex: Int = 0,
        var vertexCount: Int = 0,
        var paintShader: GpuShapeViewPaintShader.PaintShader?,
        var program: Program? = null,
        var colorMask: AG.ColorMaskState? = null,
        var stencil: AG.StencilState? = null,
        var blendMode: AG.Blending? = null,
    ) : ICommand
}
