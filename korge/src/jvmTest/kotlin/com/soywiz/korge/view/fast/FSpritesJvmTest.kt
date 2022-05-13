package com.soywiz.korge.view.fast

import com.soywiz.korge.test.assertEqualsFileReference
import com.soywiz.korge.tests.ViewsForTesting
import com.soywiz.korim.bitmap.Bitmaps
import com.soywiz.korma.geom.degrees
import kotlin.test.Test

class FSpritesJvmTest : ViewsForTesting(log = true) {
    @Test
    fun test() = viewsTest {
        val sprites = FSprites(64)
        val view = sprites.createView(Bitmaps.white.bmpBase)
        addChild(view)
        sprites.apply {
            for (n in 0 until 10) {
                alloc().also {
                    it.xy(n + 1f, n + 2f)
                    it.scale(n + 3f, n + 4f)
                    it.setAnchor(.5f, .5f)
                    it.angle = (n * 30).degrees
                    it.setTex(Bitmaps.white)
                }
            }
        }
        delayFrame()
        assertEqualsFileReference("korge/fsprites/FSprites.log", logAg.getLogAsString())
    }
}
