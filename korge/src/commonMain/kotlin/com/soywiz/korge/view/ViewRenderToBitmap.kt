package com.soywiz.korge.view

import com.soywiz.korge.annotations.KorgeExperimental
import com.soywiz.korge.render.RenderContext
import com.soywiz.korim.bitmap.Bitmap32
import com.soywiz.korma.geom.Rectangle
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.completeWith

/**
 * Asynchronously renders this [View] (with the provided [views]) to a [Bitmap32] and returns it.
 * The rendering will happen before the next frame.
 */
suspend fun View.renderToBitmap(views: Views, region: Rectangle? = null): Bitmap32 {
    val done = CompletableDeferred<Bitmap32>()

    views.onBeforeRender.once { ctx ->
        done.completeWith(kotlin.runCatching {
            unsafeRenderToBitmapSync(ctx, region).also {
                //println("/renderToBitmap")
            }
        })
    }

    return done.await()
}

@KorgeExperimental
fun View.unsafeRenderToBitmapSync(ctx: RenderContext, region: Rectangle? = null): Bitmap32 {
    val view = this
    val bounds = getLocalBoundsOptimizedAnchored()
    val boundsWidth = bounds.width
    val boundsHeight = bounds.height
    return Bitmap32(
        (if (region != null) boundsWidth - region.width else boundsWidth).toInt(),
        (if (region != null) boundsHeight - region.height else boundsHeight).toInt()
    ).also { bmp ->
        //val ctx = RenderContext(views.ag, coroutineContext = views.coroutineContext)
        //views.ag.renderToBitmap(bmp) {
        ctx.renderToBitmap(bmp, hasStencil = true) {
            ctx.useBatcher { batch ->
                val matrix = view.globalMatrixInv.clone()
                if (region != null) {
                    matrix.pretranslate(-region.x, -region.y)
                }
                batch.setViewMatrixTemp(matrix) {
                    view.render(ctx)
                }
            }
        }
    }
}
