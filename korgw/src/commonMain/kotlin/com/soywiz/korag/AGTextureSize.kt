package com.soywiz.korag

import com.soywiz.kmem.nextPowerOfTwo
import com.soywiz.korma.geom.ISizeInt
import com.soywiz.korma.geom.SizeInt
import com.soywiz.korma.geom.setTo

class AGTextureSize(
    val requireMin64: Boolean,
    val requirePot: Boolean,
    val requireSquare: Boolean,
) {
    fun computeSize(width: Int, height: Int, out: SizeInt = SizeInt()): ISizeInt {
        var rwidth = width
        var rheight = height
        if (requireMin64) {
            rwidth = kotlin.math.max(64, rwidth)
            rheight = kotlin.math.max(64, rheight)
        }
        if (requirePot) {
            rwidth = rwidth.nextPowerOfTwo
            rheight = rheight.nextPowerOfTwo
        }
        if (requireSquare) {
            val side = kotlin.math.max(rwidth, rheight)
            rwidth = side
            rheight = side
        }
        return out.setTo(rwidth, rheight)
    }
}
