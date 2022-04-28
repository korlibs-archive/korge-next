package com.soywiz.korge.view.vector

import com.soywiz.kds.*
import com.soywiz.kds.iterators.*
import com.soywiz.korag.*
import com.soywiz.korge.internal.*
import com.soywiz.korge.render.*

@KorgeInternal
class GpuShapeViewCommands {
    private var vertexIndex = 0
    private val bufferVertexData = floatArrayListOf()
    private val commands = arrayListOf<ShapeCommand>()

    fun clear() {
        vertexIndex = 0
        bufferVertexData.clear()
        commands.clear()
    }

    fun addVertex(x: Float, y: Float, u: Float, v: Float, lw: Float) {
        bufferVertexData.add(x, y, u, v, lw)
        vertexIndex++
    }

    private var commandStartIndex: Int = 0
    fun startCommand() {
        commandStartIndex = vertexIndex
    }

    fun endCommand(drawType: AG.DrawType, paintShader: GpuShapeViewPaintShader.PaintShader?) {
        commands += ShapeCommand(
            drawType = drawType,
            vertexIndex = commandStartIndex,
            vertexCount = vertexIndex - commandStartIndex,
            paintShader = paintShader,
        )
    }

    fun draw(ctx: RenderContext) {
        val ag = ctx.ag
        ag.commandsNoWait { list ->
        //ag.commandsSync { list ->
            // Set to default state
            list.setState()
            ag.getProgram(GpuShapeViewPrograms.PROGRAM_COMBINED).use(list)
            //println(bufferVertexData)
            list.vertexArrayObjectSet(ag, GpuShapeViewPrograms.LAYOUT_POS_TEX_FILL_DIST, bufferVertexData) {
                commands.fastForEach { cmd ->
                    val paintShader = cmd.paintShader
                    if (paintShader != null) {
                        //println("cmd.vertexCount=${cmd.vertexCount}, cmd.vertexIndex=${cmd.vertexIndex}, paintShader=$paintShader")
                        ctx.useBatcher { batcher ->
                            batcher.updateStandardUniforms()
                            batcher.simulateBatchStats(cmd.vertexCount)

                            //println(paintShader.uniforms)

                            batcher.setTemporalUniforms(paintShader.uniforms) {
                                list.uniformsSet(it) {
                                    list.draw(cmd.drawType, cmd.vertexCount, cmd.vertexIndex)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    data class ShapeCommand(
        var drawType: AG.DrawType = AG.DrawType.LINE_STRIP,
        var vertexIndex: Int = 0,
        var vertexCount: Int = 0,
        var paintShader: GpuShapeViewPaintShader.PaintShader? = null,
    )
}
