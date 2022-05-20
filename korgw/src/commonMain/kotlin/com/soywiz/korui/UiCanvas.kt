package com.soywiz.korui

import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korui.layout.preferredHeight
import com.soywiz.korui.layout.preferredWidth
import com.soywiz.korui.native.NativeUiFactory

open class UiCanvas(app: UiApplication, val canvas: NativeUiFactory.NativeCanvas = app.factory.createCanvas()) : UiComponent(app, canvas) {
    var image: Bitmap?
        get() = canvas.image
        set(value) {
            canvas.image = value
            this.preferredWidth = value?.width?.pt
            this.preferredHeight = value?.height?.pt
        }

    override fun copyFrom(that: UiComponent) {
        super.copyFrom(that)
        that as UiCanvas
        this.image = that.image
    }
}

inline fun UiContainer.canvas(image: Bitmap? = null, block: UiCanvas.() -> Unit): UiCanvas {
    return UiCanvas(app).also { it.image = image }.also { it.parent = this }.also(block)
}
