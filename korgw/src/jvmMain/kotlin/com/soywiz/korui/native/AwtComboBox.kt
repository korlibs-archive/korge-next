package com.soywiz.korui.native

import com.soywiz.korio.lang.*
import java.awt.event.ActionListener
import javax.swing.*

open class AwtComboBox<T>(
    factory: BaseAwtUiFactory,
    val comboBox: JComboBox<T> = factory.createJComboBox<T>()
) : AwtComponent(factory, comboBox), NativeUiFactory.NativeComboBox<T> {
    override var items: List<T>
        get() {
            val model = comboBox.model
            return (0 until model.size).map { model.getElementAt(it) }
        }
        set(value) {
            comboBox.model = DefaultComboBoxModel((value as List<Any>).toTypedArray()) as DefaultComboBoxModel<T>
        }

    override var selectedItem: T?
        get() = comboBox.selectedItem as T?
        set(value) { comboBox.selectedItem = value }

    override fun open() {
        comboBox.showPopup()
        //println("ComboBox.open")
    }

    override fun close() {
        comboBox.hidePopup()
    }

    override fun onChange(block: () -> Unit): Disposable {
        val listener = ActionListener { block() }
        comboBox.addActionListener(listener)
        return Disposable {
            comboBox.removeActionListener(listener)
        }
    }
}
