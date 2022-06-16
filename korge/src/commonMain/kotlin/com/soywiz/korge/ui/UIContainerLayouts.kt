package com.soywiz.korge.ui

import com.soywiz.korge.view.Container
import com.soywiz.korge.view.View
import com.soywiz.korge.view.ViewDslMarker
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.size
import com.soywiz.korge.view.xy

inline fun Container.uiContainer(
    width: Double = 128.0,
    height: Double = 128.0,
    block: @ViewDslMarker UIContainer.() -> Unit = {}
) = UIContainer(width, height).addTo(this).apply(block)

open class UIContainer(width: Double = 128.0, height: Double = 128.0) : UIBaseContainer(width, height) {
    override fun relayout() {}
}

abstract class UIBaseContainer(width: Double, height: Double) : UIView(width, height) {
    override fun onChildAdded(view: View) {
        relayout()
    }

    override fun onSizeChanged() {
        relayout()
    }

    abstract fun relayout()

    var deferredRendering: Boolean? = true
    //var deferredRendering: Boolean? = false

    //override fun renderInternal(ctx: RenderContext) {
    //    ctx.batch.mode(if (deferredRendering == true) BatchBuilder2D.RenderMode.DEFERRED else null) {
    //        super.renderInternal(ctx)
    //    }
    //    //ctx.flush()
    //}
}

inline fun Container.uiVerticalStack(
    width: Double = UI_DEFAULT_WIDTH,
    padding: Double = UI_DEFAULT_PADDING,
    block: @ViewDslMarker UIVerticalStack.() -> Unit = {}
) = UIVerticalStack(width, padding).addTo(this).apply(block)

open class UIVerticalStack(width: Double = UI_DEFAULT_WIDTH, padding: Double = UI_DEFAULT_PADDING) : UIVerticalHorizontalStack(width, 0.0, padding) {
    override fun relayout() {
        var y = 0.0
        forEachChild {
            it.y = y
            it.scaledWidth = width
            y += it.height + padding
        }
        height = y
    }
}

inline fun Container.uiHorizontalStack(
    height: Double = UI_DEFAULT_HEIGHT,
    padding: Double = UI_DEFAULT_PADDING,
    block: @ViewDslMarker UIHorizontalStack.() -> Unit = {}
) = UIHorizontalStack(height, padding).addTo(this).apply(block)

open class UIHorizontalStack(height: Double = UI_DEFAULT_HEIGHT, padding: Double = UI_DEFAULT_PADDING) : UIVerticalHorizontalStack(0.0, height, padding) {
    override fun relayout() {
        var x = 0.0
        forEachChild {
            it.x = x
            it.scaledHeight = height
            x += it.width + padding
        }
        width = x
    }
}

abstract class UIVerticalHorizontalStack(width: Double = UI_DEFAULT_WIDTH, height: Double = UI_DEFAULT_HEIGHT, padding: Double = UI_DEFAULT_PADDING) : UIContainer(width, height) {
    var padding: Double = padding
        set(value) {
            field = value
            relayout()
        }
}

inline fun Container.uiHorizontalFill(
    width: Double = 128.0,
    height: Double = 20.0,
    block: @ViewDslMarker UIHorizontalFill.() -> Unit = {}
) = UIHorizontalFill(width, height).addTo(this).apply(block)

open class UIHorizontalFill(width: Double = 128.0, height: Double = 20.0) : UIContainer(width, height) {
    override fun relayout() {
        var x = 0.0
        val elementWidth = width / numChildren
        forEachChild {
            it.x = x
            it.scaledHeight = height
            it.width = elementWidth
            x += elementWidth
        }
    }
}

inline fun Container.uiVerticalFill(
    width: Double = 128.0,
    height: Double = 128.0,
    block: @ViewDslMarker UIVerticalFill.() -> Unit = {}
) = UIVerticalFill(width, height).addTo(this).apply(block)

open class UIVerticalFill(width: Double = 128.0, height: Double = 128.0) : UIContainer(width, height) {
    override fun relayout() {
        var y = 0.0
        val elementHeight = height / numChildren
        forEachChild {
            it.y = y
            it.scaledWidth = width
            it.height = elementHeight
            y += elementHeight
        }
    }
}

inline fun Container.uiGridFill(
    width: Double = 128.0,
    height: Double = 128.0,
    cols: Int = 3,
    rows: Int = 3,
    block: @ViewDslMarker UIGridFill.() -> Unit = {}
) = UIGridFill(width, height, cols, rows).addTo(this).apply(block)

open class UIGridFill(width: Double = 128.0, height: Double = 128.0, cols: Int = 3, rows: Int = 3) : UIContainer(width, height) {
    var cols: Int = cols
    var rows: Int = rows

    override fun relayout() {
        val elementHeight = height / rows
        val elementWidth = width / cols
        forEachChildWithIndex { index, view ->
            val ex = index % cols
            val ey = index / cols
            view.xy(ex * elementWidth, ey * elementHeight)
            view.size(elementWidth, elementHeight)
        }
    }
}

inline fun Container.uiFillLayeredContainer(
    width: Double = 128.0,
    height: Double = 20.0,
    block: @ViewDslMarker UIFillLayeredContainer.() -> Unit = {}
) = UIFillLayeredContainer(width, height).addTo(this).apply(block)

open class UIFillLayeredContainer(width: Double = 128.0, height: Double = 20.0) : UIContainer(width, height) {
    override fun relayout() {
        val width = this.width
        val height = this.height
        forEachChild {
            it.xy(0, 0)
            it.size(width, height)
        }
    }
}
