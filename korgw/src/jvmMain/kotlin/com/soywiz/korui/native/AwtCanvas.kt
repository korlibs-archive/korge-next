package com.soywiz.korui.native

import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korui.native.util.toAwtIcon
import javax.swing.JLabel

open class AwtCanvas(factory: BaseAwtUiFactory, val label: JLabel = JLabel()) : AwtComponent(factory, label), NativeUiFactory.NativeCanvas {
    override var image: Bitmap? = null
        set(value) {
            field = value
            label.icon = value?.toAwtIcon()
        }
}
