package com.soywiz.korge.compose

import androidx.compose.runtime.*
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.*

@Composable
fun Text(text: String, onClick: () -> Unit = {}) {
    ComposeNode<UIText, NodeApplier>({
        UIText("DUMMY", height = UIButton.DEFAULT_HEIGHT).also {
            println("Created UIText")
        }
    }) {
        set(text) { this.text = it }
        set(onClick) { this.onClick { onClick() } }
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
