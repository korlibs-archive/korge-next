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
import com.soywiz.korge.compose.HStack
import com.soywiz.korge.compose.NodeApplier
import com.soywiz.korge.compose.Text
import com.soywiz.korge.compose.VStack
import com.soywiz.korge.compose.setComposeContent
import com.soywiz.korge.input.keys
import com.soywiz.korge.view.DummyView
import com.soywiz.korge.view.Stage
import com.soywiz.korge.view.View
import com.soywiz.korge.view.ViewRenderPhase
import com.soywiz.korge.view.addUpdater
import com.soywiz.korge.view.vector.GpuShapeView
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.interpolate
import com.soywiz.korim.vector.Context2d
import com.soywiz.korio.async.delay
import com.soywiz.korma.geom.vector.roundRect
import kotlin.coroutines.cancellation.CancellationException

suspend fun Stage.mainComposable() {
    setComposeContent(this) {
        App()
    }
}

@Composable
fun App() {
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
                roundRect(0.0, 0.0, 100.0, 100.0, 20 * ratio, 20 * ratio)
            }
        }
    }
    KeyDown(Key.DOWN) { count-- }
    KeyDown(Key.UP) { count++ }
}
