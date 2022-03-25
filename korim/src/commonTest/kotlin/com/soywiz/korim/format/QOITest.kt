package com.soywiz.korim.format

import com.soywiz.klock.*
import com.soywiz.korim.atlas.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import kotlin.test.*

class QOITest {
    val formats = ImageFormats(PNG, QOI)

    @Test
    fun qoiTest() = suspendTestNoBrowser {
        repeat(4) { resourcesVfs["testcard_rgba.png"].readBitmapOptimized() }
        repeat(4) { resourcesVfs["testcard_rgba.png"].readBitmapNoNative(formats) }
        repeat(4) { resourcesVfs["testcard_rgba.qoi"].readBitmapNoNative(formats) }

        val (expectedNative, expectedNativeTime) = measureTimeWithResult { resourcesVfs["dice.png"].readBitmapOptimized() }
        val (expected, expectedTime) = measureTimeWithResult { resourcesVfs["dice.png"].readBitmapNoNative(formats) }
        val (output, outputTime) = measureTimeWithResult { resourcesVfs["dice.qoi"].readBitmapNoNative(formats) }

        //QOI=4.280875ms, PNG=37.361000000000004ms, PNG_native=24.31941600036621ms
        //println("QOI=$outputTime, PNG=$expectedTime, PNG_native=$expectedNativeTime")
        //AtlasPacker.pack(listOf(output.slice(), expected.slice())).atlases.first().tex.showImageAndWait()

        assertEquals(0, output.matchContentsDistinctCount(expected))
    }
}
