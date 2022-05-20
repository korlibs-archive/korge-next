package com.soywiz.korge.ui

import com.soywiz.korge.input.mouse
import com.soywiz.korge.render.RenderContext
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.ViewDslMarker
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.solidRect
import com.soywiz.korge.view.text
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korma.geom.Rectangle

inline fun Container.uiText(
    text: String,
    width: Double = 128.0,
    height: Double = 18.0,
    block: @ViewDslMarker UIText.() -> Unit = {}
): UIText = UIText(text, width, height).addTo(this).apply(block)

class UIText(
    var text: String,
    width: Double = 128.0,
    height: Double = 64.0,
) : UIView(width, height) {
    protected var bover by uiObservable(false) { updateState() }
    protected var bpressing by uiObservable(false) { updateState() }

    private val background = solidRect(width, height, Colors.TRANSPARENT_BLACK)
    private val textView = text(text)
    var bgcolor: RGBA = Colors.TRANSPARENT_BLACK

    init {
        mouse {
            onOver {
                simulateOver()
            }
            onOut {
                simulateOut()
            }
            onDown {
                simulateDown()
            }
            onUpAnywhere {
                simulateUp()
            }
        }
    }

    fun simulateOver() {
        bover = true
    }

    fun simulateOut() {
        bover = false
    }

    fun simulatePressing(value: Boolean) {
        bpressing = value
    }

    fun simulateDown() {
        bpressing = true
    }

    fun simulateUp() {
        bpressing = false
    }

    private val textBounds = Rectangle()

    override fun renderInternal(ctx: RenderContext) {
        background.visible = bgcolor.a != 0
        background.colorMul = bgcolor
        textBounds.setTo(0.0, 0.0, width, height)
        textView.setFormat(face = textFont, size = textSize.toInt(), color = textColor, align = textAlignment)
        textView.setTextBounds(textBounds)
        //background.size(width, height)
        textView.text = text
        super.renderInternal(ctx)
    }

    override fun updateState() {
    }
}
