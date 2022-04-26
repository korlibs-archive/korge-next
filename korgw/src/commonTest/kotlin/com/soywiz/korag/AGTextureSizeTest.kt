package com.soywiz.korag

import com.soywiz.korma.geom.SizeInt
import kotlin.test.Test
import kotlin.test.assertEquals

class AGTextureSizeTest {
    val none = AGTextureSize(requireMin64 = false, requirePot = false, requireSquare = false)
    val m64 = AGTextureSize(requireMin64 = true, requirePot = false, requireSquare = false)
    val pot = AGTextureSize(requireMin64 = false, requirePot = true, requireSquare = false)
    val square = AGTextureSize(requireMin64 = false, requirePot = true, requireSquare = true)

    @Test
    fun test() {
        assertEquals(SizeInt(111, 33), none.computeSize(111, 33))
        assertEquals(SizeInt(111, 64), m64.computeSize(111, 33))
        assertEquals(SizeInt(128, 64), pot.computeSize(111, 33))
        assertEquals(SizeInt(128, 128), square.computeSize(111, 33))
    }
}
