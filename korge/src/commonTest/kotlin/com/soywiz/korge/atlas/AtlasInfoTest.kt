package com.soywiz.korge.atlas

import com.soywiz.korim.atlas.Atlas
import com.soywiz.korim.atlas.AtlasInfo
import com.soywiz.korim.atlas.readAtlas
import com.soywiz.korim.bitmap.BmpSlice
import com.soywiz.korio.async.suspendTest
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korio.lang.assert
import com.soywiz.korio.util.OS
import com.soywiz.korma.geom.Rectangle
import com.soywiz.korma.geom.RectangleInt
import com.soywiz.korma.geom.Size
import kotlin.test.Test
import kotlin.test.assertEquals

class AtlasInfoTest {
    @Test
    fun name() = suspendTest({ OS.isJvm }) {
        val atlas = AtlasInfo.loadJsonSpriter(resourcesVfs["demo.json"].readString())
        assertEquals("Spriter", atlas.app)
        assertEquals("r10", atlas.version)
        assertEquals("demo.png", atlas.image)
        assertEquals("RGBA8888", atlas.format)
        assertEquals(124, atlas.frames.size)
        assertEquals(1.0, atlas.scale)
        assertEquals(Size(1021, 1003), atlas.size.size)

        val firstFrame = atlas.frames.first()
        assertEquals("arms/forearm_jump_0.png", atlas.frames.map { it.name }.first())
        assertEquals(Rectangle(993, 319, 41, 28), firstFrame.frame.rect)
        assertEquals(Size(55, 47), firstFrame.sourceSize.size)
        assertEquals(Rectangle(8, 7, 41, 28), firstFrame.spriteSourceSize.rect)
        assertEquals(true, firstFrame.rotated)
        assertEquals(true, firstFrame.trimmed)
    }

    @Test
    fun atlasTestXml() = suspendTest({ OS.isJvm }) {
        testAtlas(resourcesVfs["atlas-test.xml"].readAtlas())
    }

    @Test
    fun atlasTestJson() = suspendTest({ OS.isJvm }) {
        testAtlas(resourcesVfs["atlas-test.json"].readAtlas(), ".png")
    }

    @Test
    fun atlasTestTxt() = suspendTest({ OS.isJvm }) {
        testAtlas(resourcesVfs["atlas-test-spine.atlas.txt"].readAtlas())
    }

    fun testAtlas(atlas: Atlas, ext:String = "") {
        // On the test textures, the edges are colorized with different intensity to test the transform
        testSlice(atlas["1$ext"], true, false, false) // red
        testSlice(atlas["2$ext"], false, true, false) // green
        testSlice(atlas["3$ext"], false, false, true) // blue
        testSlice(atlas["4$ext"], true, true, false)  // yellow
        testSlice(atlas["5$ext"], true, false, true)  // magenta
        testSlice(atlas["6$ext"], false, true, true)  // cyan
    }

    fun testSlice(slice: BmpSlice, r: Boolean, g: Boolean, b: Boolean) {
        when (slice.name) {
            "1" -> assert(slice.virtFrame == null || slice.virtFrame == RectangleInt(0, 0, 32, 46), { "Slice ${slice.name} failed on virtFrame" })
            "2" -> assert(slice.virtFrame == RectangleInt(0, 0, 42, 56), { "Slice ${slice.name} failed on virtFrame" })
            "3" -> assert(slice.virtFrame == RectangleInt(10, 0, 42, 56), { "Slice ${slice.name} failed on virtFrame" })
            "4" -> assert(slice.virtFrame == RectangleInt(10, 10, 42, 56), { "Slice ${slice.name} failed on virtFrame" })
            "5" -> assert(slice.virtFrame == RectangleInt(0, 10, 42, 56), { "Slice ${slice.name} failed on virtFrame" })
            "6" -> assert(slice.virtFrame == RectangleInt(5, 5, 42, 56), { "Slice ${slice.name} failed on virtFrame" })
        }
        assert(slice.width == 32, { "Slice ${slice.name} failed on width" })
        assert(slice.height == 46, { "Slice ${slice.name} failed on height" })

        for (i in 0 until 4) {
            val col = when(i) {
                0 -> slice.getRgba(0, 0)
                1 -> slice.getRgba(31, 0)
                2 -> slice.getRgba(31, 45)
                else -> slice.getRgba(0, 45)
            }
            val c = 250 - i * 50
            assert(col.r == if (r) c else 0, { "Slice ${slice.name} failed on pos $i at red" })
            assert(col.g == if (g) c else 0, { "Slice ${slice.name} failed on pos $i at green" })
            assert(col.b == if (b) c else 0, { "Slice ${slice.name} failed on pos $i at blue" })
        }
    }
}
