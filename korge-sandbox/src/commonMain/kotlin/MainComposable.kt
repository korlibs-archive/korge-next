import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.soywiz.klock.milliseconds
import com.soywiz.korge.Korge
import com.soywiz.korge.compose.Button
import com.soywiz.korge.compose.HStack
import com.soywiz.korge.compose.Text
import com.soywiz.korge.compose.VStack
import com.soywiz.korge.compose.setComposeContent
import com.soywiz.korge.time.delay
import com.soywiz.korge.view.Stage
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.interpolate
import kotlin.coroutines.cancellation.CancellationException

suspend fun Stage.mainComposable() {
    setComposeContent(this) {
        var color by remember { mutableStateOf(Colors.RED) }
        var count by remember { mutableStateOf(0) }
        LaunchedEffect(count) {
            println("LaunchedEffect=$count..started")
            try {
                val nsteps = 20
                for (n in 0..nsteps) {
                    val ratio = n.toDouble() / nsteps.toDouble()
                    color = ratio.interpolate(Colors.RED, Colors.WHITE)
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
        }
    }
}
