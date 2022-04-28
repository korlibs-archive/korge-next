package com.soywiz.korag

import com.soywiz.kmem.*
import com.soywiz.korma.geom.*

fun AGList.setBlendingState(blending: AG.Blending = AG.Blending.NORMAL) {
    enableDisable(AGEnable.BLEND, blending.enabled) {
        blendEquation(blending.eqRGB, blending.eqA)
        blendFunction(blending.srcRGB, blending.dstRGB, blending.srcA, blending.dstA)
    }
}

fun AGList.setRenderState(renderState: AG.RenderState) {
    enableDisable(AGEnable.CULL_FACE, renderState.frontFace != AG.FrontFace.BOTH) {
        frontFace(renderState.frontFace)
    }

    depthMask(renderState.depthMask)
    depthRange(renderState.depthNear, renderState.depthFar)

    enableDisable(AGEnable.DEPTH, renderState.depthFunc != AG.CompareMode.ALWAYS) {
        depthFunction(renderState.depthFunc)
    }
}

fun AGList.setColorMaskState(colorMask: AG.ColorMaskState) {
    colorMask(colorMask.red, colorMask.green, colorMask.blue, colorMask.alpha)
}

fun AGList.setStencilState(stencil: AG.StencilState) {
    if (stencil.enabled) {
        enable(AGEnable.STENCIL)
        stencilFunction(stencil.compareMode, stencil.referenceValue, stencil.readMask)
        stencilOperation(
            stencil.actionOnDepthFail,
            stencil.actionOnDepthPassStencilFail,
            stencil.actionOnBothPass
        )
        stencilMask(stencil.writeMask)
    } else {
        disable(AGEnable.STENCIL)
        stencilMask(0)
    }
}

fun AGList.setScissorState(currentRenderBuffer: AG.BaseRenderBuffer?, mainRenderBuffer: AG.BaseRenderBuffer, scissor: AG.Scissor? = null) {
    if (currentRenderBuffer == null) return

    //println("applyScissorState")
    val finalScissorBL = tempRect

    val realBackWidth = mainRenderBuffer.fullWidth
    val realBackHeight = mainRenderBuffer.fullHeight

    if (currentRenderBuffer === mainRenderBuffer) {
        var realScissors: Rectangle? = finalScissorBL
        realScissors?.setTo(0.0, 0.0, realBackWidth.toDouble(), realBackHeight.toDouble())
        if (scissor != null) {
            tempRect.setTo(
                currentRenderBuffer.x + scissor.x,
                ((currentRenderBuffer.y + currentRenderBuffer.height) - (scissor.y + scissor.height)),
                (scissor.width),
                scissor.height
            )
            realScissors = realScissors?.intersection(tempRect, realScissors)
        }

        //println("currentRenderBuffer: $currentRenderBuffer")

        val renderBufferScissor = currentRenderBuffer.scissor
        if (renderBufferScissor != null) {
            realScissors = realScissors?.intersection(renderBufferScissor.rect, realScissors)
        }

        //println("[MAIN_BUFFER] realScissors: $realScissors")

        enable(AGEnable.SCISSOR)
        if (realScissors != null) {
            scissor(realScissors.x.toInt(), realScissors.y.toInt(), realScissors.width.toInt(), realScissors.height.toInt())
        } else {
            scissor(0, 0, 0, 0)
        }
    } else {
        //println("[RENDER_TARGET] scissor: $scissor")

        enableDisable(AGEnable.SCISSOR, scissor != null) {
            scissor(scissor!!.x.toIntRound(), scissor.y.toIntRound(), scissor.width.toIntRound(), scissor.height.toIntRound())
        }
    }
}

fun AGList.setState(
    blending: AG.Blending = AG.Blending.NORMAL,
    stencil: AG.StencilState = AG.StencilState.DUMMY,
    colorMask: AG.ColorMaskState = AG.ColorMaskState.DUMMY,
    renderState: AG.RenderState = AG.RenderState.DUMMY,
) {
    setBlendingState(blending)
    setRenderState(renderState)
    setColorMaskState(colorMask)
    setStencilState(stencil)
}
