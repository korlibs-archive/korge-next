package com.soywiz.korim.bitmap

import com.soywiz.kmem.divCeil
import com.soywiz.korim.color.RgbaArray

class Bitmap2(
	width: Int,
	height: Int,
	data: ByteArray = ByteArray((width * height) divCeil 4),
	palette: RgbaArray = RgbaArray(4)
) : BitmapIndexed(2, width, height, data, palette) {
	override fun createWithThisFormat(width: Int, height: Int): Bitmap = Bitmap2(width, height, palette = palette)
}
