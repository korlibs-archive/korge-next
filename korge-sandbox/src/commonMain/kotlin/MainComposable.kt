import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.soywiz.korge.Korge
import com.soywiz.korge.compose.Button
import com.soywiz.korge.compose.HStack
import com.soywiz.korge.compose.Text
import com.soywiz.korge.compose.VStack
import com.soywiz.korge.compose.setComposeContent
import com.soywiz.korge.view.Stage

suspend fun Stage.mainComposable() {
    setComposeContent(this) {
        var count by remember { mutableStateOf(0) }
        VStack {
            Text("$count")
            HStack {
                Button("-") { count-- }
                Button("+") { count++ }
            }
        }
    }
}
