package com.soywiz.korag

import com.soywiz.kds.*
import com.soywiz.kgl.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.*
import com.soywiz.korio.async.*
import com.soywiz.korma.geom.*
import kotlinx.coroutines.*

class AGDispatched(val ag: AG, val dispatcher: CoroutineDispatcher) : AG() {
    override val nativeComponent: Any get() = ag.nativeComponent

    override val maxTextureSize: Size
        get() = executeSync { ag.maxTextureSize }
    override val devicePixelRatio: Double
        get() = executeSync { ag.devicePixelRatio }
    override val pixelsPerInch: Double
        get() = executeSync { ag.pixelsPerInch }
    override val backWidth: Int
        get() = executeSync { ag.backWidth }
    override val backHeight: Int
        get() = executeSync { ag.backHeight }

    inner class DeferredTexture : AG.Texture(), AG.TextureRef {
        var deferred: AG.Texture? = null
        override val texture: Texture get() = deferred!!
    }

    inner class DeferredBuffer(kind: Kind) : Buffer(kind) {
        var deferred: Buffer? = null
    }

    override fun createTexture(premultiplied: Boolean): Texture =
        DeferredTexture().also { deferred -> executeNoWait { deferred.deferred = ag.createTexture(premultiplied) } }

    // @TODO: Remove this function!!! We shouldn't expose KmlGl
    override fun createTexture(targetKind: TextureTargetKind, init: Texture.(gl: KmlGl) -> Unit): Texture =
        executeSync { ag.createTexture(targetKind, init) }

    override fun createBuffer(kind: Buffer.Kind): Buffer =
        DeferredBuffer(kind).also { deferred -> executeNoWait { deferred.deferred = ag.createBuffer(kind) } }

    override fun createMainRenderBuffer(): BaseRenderBuffer {
        return executeSync { ag.createMainRenderBuffer() }
    }

    override fun createRenderBuffer(): RenderBuffer {
        return executeSync { ag.createRenderBuffer() }
    }

    override fun contextLost() {
        super.contextLost()
        executeNoWait { ag.contextLost() }
    }

    override fun beforeDoRender() {
        executeNoWait { ag.beforeDoRender() }
    }

    override fun offscreenRendering(callback: () -> Unit) {
        executeNoWait { ag.offscreenRendering(callback) }
    }

    override fun repaint() {
        executeNoWait { ag.repaint() }
    }

    override fun resized(width: Int, height: Int) {
        executeNoWait { ag.resized(width, height) }
    }

    override fun resized(x: Int, y: Int, width: Int, height: Int, fullWidth: Int, fullHeight: Int) {
        executeNoWait { ag.resized(x, y, width, height, fullWidth, fullHeight) }
    }

    override fun dispose() {
        executeNoWait { ag.dispose() }
    }

    fun sync() {
        executeSync { }
    }

    override fun disposeTemporalPerFrameStuff() {
        executeSync { ag.disposeTemporalPerFrameStuff() }
    }

    override fun flipInternal() {
        executeSync { ag.flip() }
    }

    override fun startFrame() {
        executeSync { ag.startFrame() }
    }

    override fun clear(
        color: RGBA,
        depth: Float,
        stencil: Int,
        clearColor: Boolean,
        clearDepth: Boolean,
        clearStencil: Boolean
    ) {
        executeNoWait { ag.clear(color, depth, stencil, clearColor, clearDepth, clearStencil) }
    }

    override fun readColor(bitmap: Bitmap32) {
        executeSync { ag.readColor(bitmap) }
    }

    override fun readDepth(width: Int, height: Int, out: FloatArray) {
        executeSync { ag.readDepth(width, height, out) }
    }

    override fun readDepth(out: FloatArray2) {
        executeSync { ag.readDepth(out) }
    }

    override fun readColorTexture(texture: Texture, width: Int, height: Int) {
        executeSync { ag.readColorTexture(texture, width, height) }
    }

    override fun draw(batch: Batch) {
        // @TODO: Copy Batch instance into an object from a pool + replace textures
        executeNoWait { ag.draw(batch) }
    }

    private inline fun executeNoWait(crossinline block: () -> Unit) {
        dispatcher.launchUnscoped { block() }
    }

    private inline fun <T> executeSync(crossinline block: () -> T): T = runBlockingNoJs {
        val deferred = CompletableDeferred<T>()
        dispatcher.launchUnscoped {
            deferred.completeWith(runCatching(block))
        }
        deferred.await()
    }
}
