package com.soywiz.korge.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.soywiz.klock.milliseconds
import com.soywiz.korev.Key
import com.soywiz.korge.compose.Button
import com.soywiz.korge.compose.Canvas
import com.soywiz.korge.compose.HStack
import com.soywiz.korge.compose.KeyDown
import com.soywiz.korge.compose.NodeApplier
import com.soywiz.korge.compose.Text
import com.soywiz.korge.compose.VStack
import com.soywiz.korge.compose.setComposeContent
import com.soywiz.korge.input.keys
import com.soywiz.korge.input.mouse
import com.soywiz.korge.input.onClick
import com.soywiz.korge.ui.UIContainer
import com.soywiz.korge.ui.UIVerticalStack
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.DummyView
import com.soywiz.korge.view.SolidRect
import com.soywiz.korge.view.Stage
import com.soywiz.korge.view.View
import com.soywiz.korge.view.ViewRenderPhase
import com.soywiz.korge.view.addUpdater
import com.soywiz.korge.view.position
import com.soywiz.korge.view.vector.GpuShapeView
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.color.interpolate
import com.soywiz.korim.vector.Context2d
import com.soywiz.korio.async.delay
import com.soywiz.korma.geom.Anchor
import com.soywiz.korma.geom.Rectangle
import com.soywiz.korma.geom.vector.roundRect
import kotlin.coroutines.cancellation.CancellationException

suspend fun Stage.mainComposable() {
    setComposeContent(this) {
        //App()
        var color by remember { mutableStateOf(Colors.RED) }
        var count by remember { mutableStateOf(0) }
        var ratio by remember { mutableStateOf(0.0) }
        LaunchedEffect(count) {
            println("LaunchedEffect=$count..started")
            try {
                val nsteps = 20
                for (n in 0..nsteps) {
                    val r = n.toDouble() / nsteps.toDouble()
                    ratio = r
                    color = r.interpolate(Colors.RED, Colors.WHITE)
                    delay(10.milliseconds)
                }
                println("LaunchedEffect=$count..ended")
            } catch (e: CancellationException) {
                println("LaunchedEffect=$count..cancelled")
            }
            //stage!!.tween(::color[Colors.BLUE])
        }
        VStack {
            Text("$count", color)
            HStack {
                Button("-") { count-- }
                Button("+") { count++ }
            }
            Canvas {
                fill(color) {
                    roundRect(0.0, 0.0, 100.0, 100.0, 50 * ratio, 50 * ratio)
                }
            }
        }
        Box(
            Modifier
                .anchor(Anchor.BOTTOM_RIGHT)
                .padding(16.0)
                .backgroundColor(color)
                .clickable {
                    color = Colors.GREEN
                    count++
                }
        ) {

        }
        KeyDown(Key.DOWN) { count-- }
        KeyDown(Key.UP) { count++ }

    }
}

open class Modifier(val modifiers: List<Modifier> = listOf()) {
    fun then(other: Modifier): Modifier = Modifier(modifiers + listOf(other))

    companion object : Modifier()
}

data class AnchorModifier(val anchor: Anchor) : Modifier()
data class PaddingModifier(val padding: Double) : Modifier()
data class ClickableModifier(val onClick: (() -> Unit)? = null) : Modifier()
data class BackgroundColorModifier(val bgcolor: RGBA) : Modifier()

fun Modifier.backgroundColor(color: RGBA): Modifier = this.then(BackgroundColorModifier(color))
fun Modifier.anchor(anchor: Anchor): Modifier = this.then(AnchorModifier(anchor))
fun Modifier.padding(padding: Double): Modifier = this.then(PaddingModifier(padding))
fun Modifier.clickable(onClick: (() -> Unit)? = null): Modifier = this.then(ClickableModifier(onClick))

@Composable
fun Box(modifier: Modifier = Modifier, content: @Composable () -> Unit = {}) {
    ComposeNode<SolidRect, NodeApplier>(
        { SolidRect(100.0, 100.0, Colors.WHITE) },
        {
            set(modifier) {
                var anchor: Anchor? = null
                var padding: Double? = null
                for (mod in modifier.modifiers) {
                    when (mod) {
                        is PaddingModifier -> padding = mod.padding
                        is AnchorModifier -> anchor = mod.anchor
                        is BackgroundColorModifier -> this.color = mod.bgcolor
                        is ClickableModifier -> {
                            this.mouse.click.clear()
                            this.mouse.click.add { mod.onClick?.invoke() }
                        }
                    }
                }
                if (anchor != null || padding != null) {
                    val anchor = anchor ?: Anchor.TOP_LEFT
                    val padding = padding ?: 0.0
                    val parentBounds = this.parent!!.getLocalBounds(Rectangle())
                    this.position((parentBounds.width - this.width) * anchor.sx - padding, (parentBounds.height - this.height) * anchor.sy - padding)
                }

                //this.parent.
            }
        },
        content
    )
}
