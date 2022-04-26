import com.soywiz.korev.*
import com.soywiz.korge.input.*
import com.soywiz.korge.view.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.*

suspend fun Stage.mainClipChecks() {
    val bitmap = resourcesVfs["korge.png"].readBitmap()
    val cc = clipContainer(600, 600) {
        image(bitmap)
        image(bitmap).xy(512, 0)
    }
    keys {
        downFrame(Key.LEFT) { cc.rotation -= 1.degrees }
        downFrame(Key.RIGHT) { cc.rotation += 1.degrees }
    }
}
