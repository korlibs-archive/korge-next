package com.soywiz.korui.native

import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korui.native.util.toAwtIcon
import javax.swing.JToggleButton

open class AwtToggleButton(factory: BaseAwtUiFactory, val button: JToggleButton = JToggleButton()) : AwtComponent(factory, button), NativeUiFactory.NativeToggleButton {
    override var text: String
        get() = button.text
        set(value) { button.text = value }

    override var icon: Bitmap? = null
        set(value) {
            field = value
            button.icon = value?.toAwtIcon()
        }

    override var pressed: Boolean
        get() = button.isSelected
        set(value) { button.isSelected = value }
}
