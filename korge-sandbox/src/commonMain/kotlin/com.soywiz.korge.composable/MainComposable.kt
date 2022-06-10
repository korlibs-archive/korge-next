package com.soywiz.korge.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.soywiz.klock.milliseconds
import com.soywiz.klock.seconds
import com.soywiz.korev.Key
import com.soywiz.korge.compose.Button
import com.soywiz.korge.compose.Canvas
import com.soywiz.korge.compose.HStack
import com.soywiz.korge.compose.KeyDown
import com.soywiz.korge.compose.NodeApplier
import com.soywiz.korge.compose.Text
import com.soywiz.korge.compose.VStack
import com.soywiz.korge.compose.setComposeContent
import com.soywiz.korge.input.mouse
import com.soywiz.korge.ui.UIImage
import com.soywiz.korge.view.Circle
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.SolidRect
import com.soywiz.korge.view.Stage
import com.soywiz.korge.view.View
import com.soywiz.korge.view.mask
import com.soywiz.korge.view.position
import com.soywiz.korge.view.views
import com.soywiz.korim.bitmap.BaseBmpSlice
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.bitmap.Bitmaps
import com.soywiz.korim.bitmap.BmpSlice
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.color.interpolate
import com.soywiz.korim.format.readBitmapSlice
import com.soywiz.korio.async.delay
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.Anchor
import com.soywiz.korma.geom.Rectangle
import com.soywiz.korma.geom.ScaleMode
import com.soywiz.korma.geom.vector.roundRect
import kotlin.coroutines.cancellation.CancellationException
import kotlin.reflect.KProperty

suspend fun Stage.mainComposable() {
    setComposeContent(this) {
        MainApp()
    }
}

@Composable
fun MainApp() {
    //App()
    var color by remember { mutableStateOf(Colors.RED) }
    //val color2 by remember { Animatable(Colors.RED) }
    var count by remember { mutableStateOf(0) }
    var ratio by remember { mutableStateOf(0.0) }
    var bitmap by remember { mutableStateOf<BmpSlice?>(null) }

    //LaunchedEffect(true) { color2.animateTo(Colors.GREEN) }

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
    LaunchedEffect(true) {
        delay(0.5.seconds)
        bitmap = resourcesVfs["korge.png"].readBitmapSlice()
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
        HStack {
            Image(bitmap)
            Image(bitmap)
        }
        Image(bitmap, modifier = Modifier.size(64.0).clip())
    }
    Box(
        Modifier
            .anchor(Anchor.BOTTOM_RIGHT)
            .padding(16.0)
            .backgroundColor(color)
            .size(300.0, 200.0)
            .clickable {
                color = Colors.GREEN
                count++
            }
    ) {
        Box(Modifier.backgroundColor(Colors.RED).size(50.0))
    }
    Box(
        Modifier
            .anchor(Anchor.BOTTOM_LEFT)
            .padding(16.0)
            .backgroundColor(color)
            .size(300.0, 200.0)
            .clickable {
                color = Colors.GREEN
                count--
            }
    )
    Box(
        Modifier
            .anchor(Anchor.BOTTOM_LEFT)
            .padding(16.0)
            .backgroundColor(color)
            .fillMaxWidth()
            .clickable {
                color = Colors.GREEN
            }
    )
    KeyDown(Key.DOWN) { count-- }
    KeyDown(Key.UP) { count++ }

}

data class Animatable<T>(var value: T) {
    suspend fun animateTo(other: T) {
        while (true) {
            value
            delay(10.milliseconds)
        }
    }

    operator fun getValue(t: Any, property: KProperty<*>): T {
        return value
    }

    operator fun setValue(t: Any, property: KProperty<*>, value: T) {
        this.value = value
    }
}

open class Modifier(val parts: List<ModifierPart> = listOf()) {
    fun then(other: ModifierPart): Modifier = Modifier(parts + listOf(other))

    companion object : Modifier()
}

interface ModifierPart

data class AnchorModifier(val anchor: Anchor) : ModifierPart
data class PaddingModifier(val padding: Double) : ModifierPart
data class ClickableModifier(val onClick: (() -> Unit)? = null) : ModifierPart
data class BackgroundColorModifier(val bgcolor: RGBA) : ModifierPart
data class FillMaxWidthModifier(val ratio: Double) : ModifierPart
data class SizeModifier(val width: Double, val height: Double) : ModifierPart
data class ClipModifier(val dummy: Unit = Unit) : ModifierPart


fun Modifier.backgroundColor(color: RGBA): Modifier = this.then(BackgroundColorModifier(color))
fun Modifier.anchor(anchor: Anchor): Modifier = this.then(AnchorModifier(anchor))
fun Modifier.padding(padding: Double): Modifier = this.then(PaddingModifier(padding))
fun Modifier.size(width: Number, height: Number): Modifier = this.then(SizeModifier(width.toDouble(), height.toDouble()))
fun Modifier.size(side: Number): Modifier = this.then(SizeModifier(side.toDouble(), side.toDouble()))
fun Modifier.clip(): Modifier = this.then(ClipModifier())
fun Modifier.fillMaxWidth(ratio: Double = 1.0): Modifier = this.then(FillMaxWidthModifier(ratio))
fun Modifier.clickable(onClick: (() -> Unit)? = null): Modifier = this.then(ClickableModifier(onClick))

fun View.applyModifiers(modifier: Modifier) {
    var anchor: Anchor? = null
    var padding: Double? = null
    for (mod in modifier.parts) {
        when (mod) {
            is PaddingModifier -> padding = mod.padding
            is AnchorModifier -> anchor = mod.anchor
            is BackgroundColorModifier -> colorMul = mod.bgcolor
            is ClickableModifier -> this.mouse.click.also { it.clear() }.add { mod.onClick?.invoke() }
            is SizeModifier -> setSize(mod.width, mod.height)
            is FillMaxWidthModifier -> {
                this.x = 0.0
                this.scaledWidth = parent!!.width * mod.ratio
            }
            is ClipModifier -> {
                // @TODO: Fix this!
                val mask = Circle(32.0)
                this.mask = mask
                (this as Container).addChild(mask)
            }
        }
    }
    if (anchor != null || padding != null) {
        val anchor = anchor ?: Anchor.TOP_LEFT
        val padding = padding ?: 0.0
        val parentBounds = this.parent!!.getLocalBounds(Rectangle())
        this.position((parentBounds.width - this.width) * anchor.sx - padding, (parentBounds.height - this.height) * anchor.sy - padding)
    }

}

@Composable
fun Box(modifier: Modifier = Modifier, content: @Composable () -> Unit = {}) {
    ComposeNode<SolidRect, NodeApplier>(
        { SolidRect(100.0, 100.0, Colors.WHITE) },
        {
            set(modifier) { applyModifiers(modifier) }
        },
        content
    )
}

@Composable
fun Image(bitmap: BmpSlice?, modifier: Modifier = Modifier, content: @Composable () -> Unit = {}) {
    ComposeNode<UIImage, NodeApplier>(
        { UIImage(100.0, 100.0, Bitmaps.transparent, ScaleMode.SHOW_ALL, Anchor.CENTER) },
        {
            set(bitmap) { this.bitmap = bitmap ?: Bitmaps.transparent }
            set(modifier) { applyModifiers(modifier) }
        },
        content
    )
}
