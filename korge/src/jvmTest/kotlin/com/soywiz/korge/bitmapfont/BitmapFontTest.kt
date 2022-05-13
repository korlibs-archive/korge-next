package com.soywiz.korge.bitmapfont

import com.soywiz.korag.log.LogAG
import com.soywiz.korge.render.RenderContext
import com.soywiz.korge.test.assertEqualsFileReference
import com.soywiz.korim.font.readBitmapFont
import com.soywiz.korio.async.suspendTest
import com.soywiz.korio.file.std.resourcesVfs
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.test.Test
import kotlin.test.assertEquals

class BitmapFontTest {
	val ag = object : LogAG() {
        override fun log(str: String, kind: Kind) {
            if (kind != Kind.SHADER) {
                super.log(str, kind)
            }
        }
    }
	val ctx = RenderContext(ag, coroutineContext = EmptyCoroutineContext)

	@Test
	fun simple() = suspendTest {
		val font = resourcesVfs["font/font.fnt"].readBitmapFont()
		assertEquals(81, font.glyphs.size)
		val glyph = font[64]
		assertEquals(69, glyph.texture.width)
		assertEquals(70, glyph.texture.height)
		assertEquals(4, glyph.xoffset)
		assertEquals(4, glyph.yoffset)
		assertEquals(73, glyph.xadvance)

		font.drawText(ctx, 72.0 / 4.0, "ABF,", 0, 0)
		ctx.flush()

        assertEqualsFileReference("korge/bitmapfont/BitmapFontSimple.log", ag.getLogAsString())
	}

	@Test
	fun font2() = suspendTest {
		val font = resourcesVfs["font2/font1.fnt"].readBitmapFont()
		assertEquals(95, font.glyphs.size)
		val glyph = font[64]
		assertEquals(52, glyph.texture.width)
		assertEquals(52, glyph.texture.height)
		assertEquals(3, glyph.xoffset)
		assertEquals(8, glyph.yoffset)
		assertEquals(51, glyph.xadvance)
	}
}
