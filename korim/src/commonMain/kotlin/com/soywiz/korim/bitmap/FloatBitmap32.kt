package com.soywiz.korim.bitmap

import com.soywiz.korim.color.*

class FloatBitmap32(
    width: Int,
    height: Int,
    val data: FloatArray = FloatArray(width * height * 4),
    premultiplied: Boolean = false
) : Bitmap(width, height, 32, premultiplied, data) {
    override fun setRgba(x: Int, y: Int, v: RGBA): Unit {
        val rindex = index(x, y) * 4
        data[rindex + 0] = v.rf
        data[rindex + 1] = v.gf
        data[rindex + 2] = v.bf
        data[rindex + 3] = v.af
    }
    override fun getRgba(x: Int, y: Int): RGBA {
        val rindex = index(x, y) * 4
        return RGBA.float(data[rindex + 0], data[rindex + 1], data[rindex + 2], data[rindex + 3])
    }
}
