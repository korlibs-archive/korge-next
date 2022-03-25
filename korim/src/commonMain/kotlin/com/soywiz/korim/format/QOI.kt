package com.soywiz.korim.format

import com.soywiz.kmem.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.*
import com.soywiz.korio.lang.*
import com.soywiz.korio.stream.*

object QOI : ImageFormat("qoi") {
    override fun decodeHeader(s: SyncStream, props: ImageDecodingProps): ImageInfo? {
        if (s.readStringz(4, ASCII) != "qoif") return null
        val width = s.readS32BE()
        val height = s.readS32BE()
        val channels = s.readU8()
        val colorspace = s.readU8()
        return ImageInfo {
            this.width = width
            this.height = height
            this.bitsPerPixel = channels * 8
        }
    }

    override fun readImage(s: SyncStream, props: ImageDecodingProps): ImageData {
        val header = decodeHeader(s, props) ?: error("Not a QOI image")
        val bytes = UByteArrayInt(s.readAvailable())
        val index = RgbaArray(64)
        val out = Bitmap32(header.width, header.height)
        val outp = out.data
        val totalPixels = out.area
        var o = 0
        var p = 0

        var r = 0
        var g = 0
        var b = 0
        var a = 0xFF
        var lastCol = RGBA(0, 0, 0, 0xFF)

        while (o < totalPixels && p < bytes.size) {
            val b1 = bytes[p++]

            when (b1) {
                QOI_OP_RGB -> {
                    r = bytes[p++]
                    g = bytes[p++]
                    b = bytes[p++]
                }
                QOI_OP_RGBA -> {
                    r = bytes[p++]
                    g = bytes[p++]
                    b = bytes[p++]
                    a = bytes[p++]
                }
                else -> {
                    when (b1.extract2(6)) {
                        QOI_OP_INDEX -> {
                            val col = index[b1]
                            r = col.r
                            g = col.g
                            b = col.b
                            a = col.a
                        }
                        QOI_OP_DIFF -> {
                            r = (r + (b1.extract2(4) - 2)) and 0xFF
                            g = (g + (b1.extract2(2) - 2)) and 0xFF
                            b = (b + (b1.extract2(0) - 2)) and 0xFF
                        }
                        QOI_OP_LUMA -> {
                            val b2 = bytes[p++]
                            val vg = (b1.extract6(0)) - 32
                            r = (r + (vg - 8 + b2.extract4(4))) and 0xFF
                            g = (g + (vg)) and 0xFF
                            b = (b + (vg - 8 + b2.extract4(0))) and 0xFF
                        }
                        QOI_OP_RUN -> {
                            val np = b1.extract6(0) + 1
                            for (n in 0 until np) outp[o++] = lastCol
                            continue
                        }
                    }
                }
            }

            lastCol = RGBA.packUnsafe(r, g, b, a)
            index[QOI_COLOR_HASH(r, g, b, a) % 64] = lastCol
            outp[o++] = lastCol
        }
        return ImageData(out)
    }

    private const val QOI_SRGB   = 0
    private const val QOI_LINEAR = 1

    private const val QOI_OP_INDEX  = 0b00 /* 00xxxxxx */
    private const val QOI_OP_DIFF   = 0b01 /* 01xxxxxx */
    private const val QOI_OP_LUMA   = 0b10 /* 10xxxxxx */
    private const val QOI_OP_RUN    = 0b11 /* 11xxxxxx */

    private const val QOI_OP_RGB    = 0xfe /* 11111110 */
    private const val QOI_OP_RGBA   = 0xff /* 11111111 */

    private const val QOI_MASK_2    = 0xc0 /* 11000000 */

    private fun QOI_COLOR_HASH(r: Int, g: Int, b: Int, a: Int): Int = (r*3 + g*5 + b*7 + a*11)
    private fun QOI_COLOR_HASH(C: RGBA): Int = QOI_COLOR_HASH(C.r, C.g, C.b, C.a)
    private const val QOI_HEADER_SIZE = 14

    /* 2GB is the max file size that this implementation can safely handle. We guard
    against anything larger than that, assuming the worst case with 5 bytes per
    pixel, rounded down to a nice clean value. 400 million pixels ought to be
    enough for anybody. */
    private const val QOI_PIXELS_MAX = 400_000_000
}
