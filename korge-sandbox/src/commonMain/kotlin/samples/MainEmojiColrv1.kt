package samples

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.text
import com.soywiz.korge.view.xy
import com.soywiz.korim.font.readTtfFont
import com.soywiz.korio.file.std.resourcesVfs

class MainEmojiColrv1 : Scene() {
    override suspend fun Container.sceneMain() {
        val font = resourcesVfs["twemoji-glyf_colr_1.ttf"].readTtfFont()
        text("HELLO WORLD! 😀", font = font).xy(100, 100)
    }
}
