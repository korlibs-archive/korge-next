package com.soywiz.korim.font

import com.soywiz.korim.format.showImageAndWait
import com.soywiz.korim.vector.SvgBuilder
import com.soywiz.korim.vector.render
import com.soywiz.korim.vector.renderToImage
import com.soywiz.korim.vector.scaled
import com.soywiz.korim.vector.toSvg
import com.soywiz.korio.async.suspendTest
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korio.lang.WChar
import com.soywiz.korio.lang.WString
import kotlin.test.Test

class TTfTest {
    @Test
    fun test() = suspendTest {
        val font = resourcesVfs["twemoji-glyf_colr_1.ttf"].readTtfFont()
        val wstr = WString("😀👩🏽‍🦳👨🏻‍🦳")
        val glyph = font[wstr.codePointAt(0)]!!
        val colorPath = glyph.colorEntry!!.getColorPath()
        //colorPath.scaled(0.01, 0.01).render().showImageAndWait()
    }
}
