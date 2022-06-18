package samples

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.text
import com.soywiz.korge.view.xy
import com.soywiz.korim.font.DefaultTtfFont
import com.soywiz.korim.font.TtfFont
import com.soywiz.korim.font.readTtfFont
import com.soywiz.korim.font.withFallback
import com.soywiz.korio.file.std.resourcesVfs

class MainEmojiColrv1 : Scene() {
    override suspend fun Container.sceneMain() {
        val font = resourcesVfs["twemoji-glyf_colr_1.ttf"].readTtfFont(preload = true)
        //val font = resourcesVfs["noto-glyf_colr_1.ttf"].readTtfFont(preload = true)

        //val font = DefaultTtfFont.withFallback()
        text("HELLO! 😀😁🤤👨‍🦳👨🏻‍🦳👨🏻‍🦳👩🏽‍🦳", font = font, textSize = 64.0).xy(0, 100)
    }
}
