package com.soywiz.korge.compose

import androidx.compose.runtime.*
import com.soywiz.korev.Key
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.DummyView
import com.soywiz.korge.view.addUpdater
import com.soywiz.korge.view.vector.GpuShapeView
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.vector.Context2d

@Composable
fun Text(text: String, color: RGBA = Colors.WHITE, onClick: () -> Unit = {}) {
    ComposeNode<UIText, NodeApplier>({
        UIText("DUMMY", height = UIButton.DEFAULT_HEIGHT).also {
            println("Created UIText")
        }
    }) {
        set(text) { this.text = it }
        set(color) { this.colorMul = it }
        set(onClick) {
            this.onClick { onClick() }
        }
    }
}

@Composable
fun Button(text: String, onClick: () -> Unit = {}) {
    ComposeNode<UIButton, NodeApplier>({
        UIButton().also {
            println("Created UIButton")
        }
    }) {
        set(text) { this.text = it }
        set(onClick) { this.onClick { onClick() } }
    }
}

@Composable
fun VStack(content: @Composable () -> Unit) {
    ComposeNode<UIVerticalStack, NodeApplier>(::UIVerticalStack, {}, content)
}

@Composable
fun HStack(content: @Composable () -> Unit) {
    ComposeNode<UIHorizontalStack, NodeApplier>(::UIHorizontalStack, {}, content)
}

@Composable
fun KeyDown(key: Key, onPress: (Key) -> Unit = {}) {
    KeyDown { if (it == key) onPress(it) }
}

@Composable
fun KeyDown(onPress: (Key) -> Unit = {}) {
    ComposeNode<DummyView, NodeApplier>({
        DummyView()
    }) {
        set(onPress) { this.keys.down { onPress(it.key) } }
    }
}

@Composable
fun Canvas(onDraw: Context2d.() -> Unit = {}) {
    ComposeNode<GpuShapeView, NodeApplier>({
        GpuShapeView().also {
            it.updateShape { onDraw() }
        }.also {
            it.addUpdater { dt ->
                it.updateShape { onDraw() }
            }
        }
    }) {
    }
}

