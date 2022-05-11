package com.esotericsoftware.spine

import com.soywiz.korim.atlas.*

class SpineRegion(val entry: Atlas.Entry) {
    val bmpSlice = entry.slice
    val bmp = bmpSlice.bmpBase
    val texture = bmp
    var rotate = entry.info.bmpCoords != null
    val u: Float = bmpSlice.tl_x
    val u2: Float = bmpSlice.br_x
    val v: Float = if (rotate) bmpSlice.br_y else bmpSlice.tl_y
    val v2: Float = if (rotate) bmpSlice.tl_y else bmpSlice.br_y
    var offsetX = entry.info.offset.x.toFloat()
    var offsetY = entry.info.offset.y.toFloat()
    var originalWidth = entry.info.orig.width.toFloat()
    var originalHeight = entry.info.orig.height.toFloat()
    var packedWidth = (if (rotate) entry.info.sourceSize.height else entry.info.sourceSize.width).toFloat()
    var packedHeight = (if (rotate) entry.info.sourceSize.width else entry.info.sourceSize.height).toFloat()
    var degrees = if (rotate) 90 else 0
}
