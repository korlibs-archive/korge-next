package com.soywiz.korim.font.ttf

import com.soywiz.korim.font.DefaultTtfFont
import com.soywiz.korim.font.SystemFont
import com.soywiz.korim.font.getTextBounds
import com.soywiz.korio.async.suspendTest
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.std.applicationVfs
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korio.lang.WString
import com.soywiz.korma.geom.int
import kotlin.test.Test
import kotlin.test.assertEquals

class TtfFontTest {
    lateinit var root: VfsFile

    fun ttfTest(callback: suspend () -> Unit) = suspendTest {
        for (path in listOf(applicationVfs["src/test/resources"], resourcesVfs)) {
            root = path
            if (root["kotlin8.png"].exists()) break
        }
        callback()
    }

    @Test
    fun testColon() {
        assertEquals(
            "M300,0 L100,0 L100,-200 L300,-200 L300,0 Z M300,-800 L100,-800 L100,-1000 L300,-1000 L300,-800 Z",
            DefaultTtfFont.getGlyphByChar(':')!!.path!!.toSvgString()
        )
    }

    @Test
    fun testBounds() {
        assertEquals(
            "Rectangle(x=-3, y=-12, width=342, height=56)",
            DefaultTtfFont.getTextBounds(64.0, "jHello : Worljg").bounds.int.toString()
        )
    }

    @Test
    fun testColorFont() {
        println(SystemFont.getEmojiFont().name)
        val smileyGlyph = SystemFont.getEmojiFont().ttf[WString("😀")[0]]
        //Colors["#ffc83dff"]
        //println("smileyGlyph=${smileyGlyph?.codePoint},$smileyGlyph")
        //println(smileyGlyph?.colorEntry)
        //for (path in smileyGlyph!!.paths) println("path = $path")
    }

    //@Test
    //fun name() = ttfTest {
    //    val font = TtfFont(root["Comfortaa-Regular.ttf"].readAll().openSync())
    //    NativeImage(512, 128).apply {
    //        getContext2d()
    //            .fillText(
    //                "HELLO WORLD. This 0123 ñáéíóúç",
    //                font = font,
    //                size = 32.0,
    //                x = 0.0,
    //                y = 0.0,
    //                color = Colors.RED,
    //                origin = TtfFont.Origin.TOP
    //            )
    //    }.showImageAndWait()
    //}
}
