package com.soywiz.korge.view.fast

import com.soywiz.korge.test.assertEqualsFileReference
import com.soywiz.korge.tests.ViewsForTesting
import com.soywiz.korge.view.image
import com.soywiz.korge.view.xy
import com.soywiz.korim.bitmap.Bitmap32
import com.soywiz.korim.color.Colors
import org.junit.Test

class MultiTextureFastRender : ViewsForTesting(log = true) {
    @Test
    @Suppress("UNUSED_CHANGED_VALUE")
    fun test() = viewsTest {
        val tex0 = Bitmap32(16, 16) { x, y -> Colors.RED }
        val tex1 = Bitmap32(16, 16) { x, y -> Colors.GREEN }
        val tex2 = Bitmap32(16, 16) { x, y -> Colors.BLUE }
        val tex3 = Bitmap32(16, 16) { x, y -> Colors.YELLOW }
        val tex4 = Bitmap32(16, 16) { x, y -> Colors.PINK }
        var n = 0
        image(tex0).xy(16 * n, 16 * n); n++
        image(tex1).xy(16 * n, 16 * n); n++
        image(tex2).xy(16 * n, 16 * n); n++
        image(tex3).xy(16 * n, 16 * n); n++
        image(tex0).xy(16 * n, 16 * n); n++
        image(tex2).xy(16 * n, 16 * n); n++
        image(tex1).xy(16 * n, 16 * n); n++
        image(tex3).xy(16 * n, 16 * n); n++
        image(tex4).xy(16 * n, 16 * n); n++
        image(tex4).xy(16 * n, 16 * n); n++
        delayFrame()
        assertEqualsFileReference("korge/fast/MultiTextureRendering.log", logAg.getLogAsString())
    }
}
