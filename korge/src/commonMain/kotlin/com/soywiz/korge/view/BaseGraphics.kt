package com.soywiz.korge.view

import com.soywiz.kds.iterators.*
import com.soywiz.kmem.*
import com.soywiz.korge.render.*
import com.soywiz.korge.view.internal.*
import com.soywiz.korge.view.internal.InternalViewAutoscaling
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.vector.*
import com.soywiz.korma.geom.*
import kotlin.jvm.*

abstract class BaseGraphics(
    var autoScaling: Boolean = false
) : BaseImage(Bitmaps.transparent) {
    var useNativeRendering = true

    private fun createImage(width: Int, height: Int): Bitmap {
        return if (useNativeRendering) NativeImage(width, height) else Bitmap32(width, height, premultiplied = true)
    }

    @PublishedApi
    internal val bitmapsToRemove = arrayListOf<Bitmap>()

    private var autoscaling = InternalViewAutoscaling()

    override val bwidth: Double get() = bitmap.width.toDouble() / autoscaling.renderedAtScaleX
    override val bheight: Double get() = bitmap.height.toDouble() / autoscaling.renderedAtScaleY

    @PublishedApi
    internal var dirty = true

    private val bb = BoundsBuilder()
    private val bounds = Rectangle()

    override fun renderInternal(ctx: RenderContext) {
        bitmapsToRemove.fastForEach {
            ctx.agBitmapTextureManager.removeBitmap(it)
        }
        bitmapsToRemove.clear()

        redrawIfRequired()
        super.renderInternal(ctx)
    }

    @PublishedApi
    internal fun redrawIfRequired() {
        if (autoscaling.onRender(autoScaling, globalMatrix)) {
            dirty = true
        }

        if (dirty) {
            dirty = false

            getLocalBoundsInternalNoAnchor(bounds)

            // Removes old image
            run {
                bitmapsToRemove.add(this.bitmap.bmpBase)
            }
            // Generates new image
            run {
                //println("Regenerate image: bounds=${bounds}, renderedAtScale=${renderedAtScaleX},${renderedAtScaleY}, sLeft=$sLeft, sTop=$sTop, bwidth=$bwidth, bheight=$bheight")

                val image = createImage(
                    (bounds.width * autoscaling.renderedAtScaleX).toIntCeil().coerceAtLeast(1) + 1,
                    (bounds.height * autoscaling.renderedAtScaleY).toIntCeil().coerceAtLeast(1) + 1
                )
                image.context2d {
                    scale(this@BaseGraphics.autoscaling.renderedAtScaleX, this@BaseGraphics.autoscaling.renderedAtScaleY)
                    translate(-this@BaseGraphics.bounds.x, -this@BaseGraphics.bounds.y)
                    drawShape(this)
                    //this@BaseGraphics.compoundShape.draw(this)
                }
                this.bitmap = image.slice()
            }
        }
    }

    internal val _sLeft get() = sLeft
    internal val _sTop get() = sTop

    override val sLeft: Double get() {
        var out = bounds.x - anchorDispX
        if (bwidth < 0) out -= bwidth
        return out
    }
    override val sTop: Double get() {
        var out = bounds.y - anchorDispY
        if (bheight < 0) out -= bheight
        return out
    }

    protected abstract fun drawShape(ctx: Context2d) // this@BaseGraphics.compoundShape.draw(this)
    protected abstract fun getShapeBounds(bb: BoundsBuilder) // shapes.fastForEach { it.addBounds(bb) }

    fun getLocalBoundsInternalNoAnchor(out: Rectangle) {
        bb.reset()
        getShapeBounds(bb)
        bb.getBounds(out)
        //println("Graphics.BOUNDS: $out")
    }

    override fun getLocalBoundsInternal(out: Rectangle) {
        getLocalBoundsInternalNoAnchor(out)
        out.displace(-anchorDispX, -anchorDispY)
    }
}
