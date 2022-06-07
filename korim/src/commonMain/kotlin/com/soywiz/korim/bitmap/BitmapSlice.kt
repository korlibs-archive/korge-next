package com.soywiz.korim.bitmap

import com.soywiz.kds.Extra
import com.soywiz.kds.getCyclic
import com.soywiz.kmem.clamp
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.color.RgbaArray
import com.soywiz.korim.format.ImageOrientation
import com.soywiz.korim.format.withImageOrientation
import com.soywiz.korio.lang.Closeable
import com.soywiz.korio.resources.Resourceable
import com.soywiz.korma.geom.ISizeInt
import com.soywiz.korma.geom.Matrix
import com.soywiz.korma.geom.Matrix3D
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.PointInt
import com.soywiz.korma.geom.Rectangle
import com.soywiz.korma.geom.RectangleInt
import com.soywiz.korma.geom.SizeInt
import com.soywiz.korma.geom.setBoundsTo
import com.soywiz.korma.geom.setTo
import com.soywiz.korma.geom.size
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

interface BmpCoords {
    val tl_x: Float
    val tl_y: Float

    val tr_x: Float
    val tr_y: Float

    val br_x: Float
    val br_y: Float

    val bl_x: Float
    val bl_y: Float
}

interface BmpCoordsWithT<T : ISizeInt> : BmpCoords, Closeable, Resourceable<BmpCoordsWithT<T>> {
    override fun getOrNull(): BmpCoordsWithT<T>? = this
    override suspend fun get(): BmpCoordsWithT<T> = this
    val baseWidth: Int get() = base.width
    val baseHeight: Int get() = base.height
    val name: String? get() = null
    val base: T
    val left: Int get() = (tl_x * baseWidth).toInt()
    val top: Int get() = (tl_y * baseHeight).toInt()
    val width: Int get() = (Point.distance(tl_x * baseWidth, tl_y * baseHeight, tr_x * baseWidth, tr_y * baseHeight)).toInt()
    val height: Int get() = (Point.distance(tl_x * baseWidth, tl_y * baseHeight, bl_x * baseWidth, bl_y * baseHeight)).toInt()
    val virtFrame: RectangleInt? get() = null
    val frameOffsetX: Int get() = virtFrame?.x ?: 0
    val frameOffsetY: Int get() = virtFrame?.y ?: 0
    val frameWidth: Int get() = virtFrame?.width ?: width
    val frameHeight: Int get() = virtFrame?.height ?: height
    val area: Int get() = width * height
    override fun close() = Unit
    val sizeString: String get() = "${width}x${height}"
}

// @TODO: Fix & enable to support slicing transformed textures

/*
private fun transformInRange(ratio: Float, a: Float, b: Float) = (b - a) * ratio + a

private fun <T : ISizeInt> BmpCoordsWithT<T>.sliceRatio(ratioLeft: Float, ratioRight: Float, ratioTop: Float, ratioBottom: Float, name: String? = null): BmpCoordsWithT<T> {
    //println("($ratioLeft, $ratioRight), ($ratioTop, $ratioBottom)")
    return BmpCoordsWithInstance(
        base,
        transformInRange(ratioLeft, tl_x, tr_x), transformInRange(ratioTop, tl_y, bl_y),
        transformInRange(ratioRight, tl_x, tr_x), transformInRange(ratioTop, tr_y, br_y),

        transformInRange(ratioRight, bl_x, br_x), transformInRange(ratioBottom, tr_y, br_y),
        transformInRange(ratioLeft, bl_x, br_x), transformInRange(ratioBottom, tl_y, bl_y),
    )
}

fun <T : ISizeInt> BmpCoordsWithT<T>.slice(bounds: RectangleInt = RectangleInt(0, 0, width, height), name: String? = null): BmpCoordsWithT<T> {
    return sliceRatio(
        bounds.left.toFloat() / base.width,
        bounds.right.toFloat() / base.width,
        bounds.top.toFloat() / base.height,
        bounds.bottom.toFloat() / base.height,
        name
    )
}
fun <T : ISizeInt> BmpCoordsWithT<T>.sliceWithBounds(left: Int, top: Int, right: Int, bottom: Int, name: String? = null): BmpCoordsWithT<T> = slice(createRectangleInt(0, 0, this.width, this.height, left, top, right, bottom), name)
fun <T : ISizeInt> BmpCoordsWithT<T>.sliceWithSize(x: Int, y: Int, width: Int, height: Int, name: String? = null): BmpCoordsWithT<T> = sliceWithBounds(x, y, x + width, y + height, name)
*/

typealias BitmapCoords = BmpCoordsWithT<Bitmap>
typealias BaseBmpSlice = BmpCoordsWithT<Bitmap>
val BaseBmpSlice.bmpBase get() = base

data class BmpCoordsWithInstance<T : ISizeInt>(
    override val base: T,
    override val tl_x: Float, override val tl_y: Float,
    override val tr_x: Float, override val tr_y: Float,
    override val br_x: Float, override val br_y: Float,
    override val bl_x: Float, override val bl_y: Float,
    override val name: String? = null,
    override val virtFrame: RectangleInt? = null
) : BmpCoordsWithInstanceBase<T>(base, tl_x, tl_y, tr_x, tr_y, br_x, br_y, bl_x, bl_y, name, virtFrame) {
    constructor(base: T, coords: BmpCoords, name: String? = null, virtFrame: RectangleInt? = null) : this(
        base,
        coords.tl_x, coords.tl_y,
        coords.tr_x, coords.tr_y,
        coords.br_x, coords.br_y,
        coords.bl_x, coords.bl_y,
        name,
        virtFrame
    )
}

open class BmpCoordsWithInstanceBase<T : ISizeInt>(
    override val base: T,
    override val tl_x: Float, override val tl_y: Float,
    override val tr_x: Float, override val tr_y: Float,
    override val br_x: Float, override val br_y: Float,
    override val bl_x: Float, override val bl_y: Float,
    override val name: String? = null,
    override val virtFrame: RectangleInt? = null
) : BmpCoordsWithT<T> {
    constructor(base: T, coords: BmpCoords, name: String? = null, virtFrame: RectangleInt? = null) : this(
        base,
        coords.tl_x, coords.tl_y,
        coords.tr_x, coords.tr_y,
        coords.br_x, coords.br_y,
        coords.bl_x, coords.bl_y,
        name,
        virtFrame
    )
    constructor(base: BmpCoordsWithT<T>, name: String? = null, virtFrame: RectangleInt? = null) : this(base.base, base, name ?: base.name, virtFrame)

    override fun close() {
        (base as? Closeable)?.close()
    }
}

open class MutableBmpCoordsWithInstanceBase<T : ISizeInt>(
    override var base: T,
    override var tl_x: Float, override var tl_y: Float,
    override var tr_x: Float, override var tr_y: Float,
    override var br_x: Float, override var br_y: Float,
    override var bl_x: Float, override var bl_y: Float,
    override var name: String? = null,
    override var virtFrame: RectangleInt? = null
) : BmpCoordsWithT<T> {
    constructor(base: T, coords: BmpCoords, name: String? = null, virtFrame: RectangleInt? = null) : this(
        base,
        coords.tl_x, coords.tl_y,
        coords.tr_x, coords.tr_y,
        coords.br_x, coords.br_y,
        coords.bl_x, coords.bl_y,
        name,
        virtFrame
    )
    constructor(base: BmpCoordsWithT<T>, name: String? = null, virtFrame: RectangleInt? = null) : this(base.base, base, name ?: base.name, virtFrame)

    fun setTo(
        tl_x: Float, tl_y: Float,
        tr_x: Float, tr_y: Float,
        br_x: Float, br_y: Float,
        bl_x: Float, bl_y: Float,
        virtFrame: RectangleInt? = null,
    ) {
        this.tl_x = tl_x
        this.tl_y = tl_y
        this.tr_x = tr_x
        this.tr_y = tr_y
        this.br_x = br_x
        this.br_y = br_y
        this.bl_x = bl_x
        this.bl_y = bl_y
        this.virtFrame = virtFrame
    }

    fun setTo(coords: BmpCoords, virtFrame: RectangleInt? = null) {
        setTo(
            coords.tl_x, coords.tl_y, coords.tr_x, coords.tr_y,
            coords.br_x, coords.br_y, coords.bl_x, coords.bl_y,
            virtFrame
        )
    }

    fun setTo(base: T, coords: BmpCoords, name: String? = null, virtFrame: RectangleInt? = null) {
        this.base = base
        setTo(coords, virtFrame)
        this.name = name
    }

    override fun close() {
        (base as? Closeable)?.close()
    }

    fun setBasicCoords(x0: Float, y0: Float, x1: Float, y1: Float) {
        setTo(x0, y0, x1, y0, x1, y1, x0, y1)
    }
}

open class UntransformedSizeBmpCoordsWithInstance<T : ISizeInt>(
    val baseCoords: BmpCoordsWithT<T>
) : BmpCoordsWithInstanceBase<T>(baseCoords) {
    override val width: Int get() = baseCoords.baseWidth
    override val height: Int get() = baseCoords.baseHeight

    override fun toString(): String =
        "UntransformedSizeBmpCoordsWithInstance(width=$width, height=$height, baseCoords=$baseCoords)"
}

// @TODO: This was failing because frameWidth, and frameHeight was being delegated to the original instance

//open class UntransformedSizeBmpCoordsWithInstance<T : ISizeInt>(
//    val baseCoords: BmpCoordsWithT<T>
//) : BmpCoordsWithT<T> by baseCoords {
//    override val width: Int get() = baseCoords.baseWidth
//    override val height: Int get() = baseCoords.baseHeight
//
//    override fun toString(): String =
//        "UntransformedSizeBmpCoordsWithInstance(width=$width, height=$height, baseCoords=$baseCoords)"
//}

fun <T : ISizeInt> BmpCoordsWithT<T>.copy(
    base: T = this.base,
    tl_x: Float = this.tl_x, tl_y: Float = this.tl_y,
    tr_x: Float = this.tr_x, tr_y: Float = this.tr_y,
    br_x: Float = this.br_x, br_y: Float = this.br_y,
    bl_x: Float = this.bl_x, bl_y: Float = this.bl_y,
    name: String? = this.name,
    virtFrame: RectangleInt? = null
): BmpCoordsWithInstance<T> = BmpCoordsWithInstance(base, tl_x, tl_y, tr_x, tr_y, br_x, br_y, bl_x, bl_y, name, virtFrame)

fun <T : ISizeInt> BmpCoordsWithT<T>.rotatedLeft(): BmpCoordsWithInstance<T> =
    copy(base, tr_x, tr_y, br_x, br_y, bl_x, bl_y, tl_x, tl_y,
        virtFrame = if (virtFrame != null) {
            RectangleInt(frameOffsetY, frameWidth - width - frameOffsetX, frameHeight, frameWidth)
        } else {
            null
        }
    )

fun <T : ISizeInt> BmpCoordsWithT<T>.rotatedRight(): BmpCoordsWithInstance<T> =
    copy(base, bl_x, bl_y, tl_x, tl_y, tr_x, tr_y, br_x, br_y,
        virtFrame = if (virtFrame != null) {
            RectangleInt(frameHeight - height - frameOffsetY, frameOffsetX, frameHeight, frameWidth)
        } else {
            null
        }
    )

fun <T : ISizeInt> BmpCoordsWithT<T>.flippedX(): BmpCoordsWithInstance<T> =
    copy(base, tr_x, tr_y, tl_x, tl_y, bl_x, bl_y, br_x, br_y,
        virtFrame = if (virtFrame != null) {
            RectangleInt(frameWidth - width - frameOffsetX, frameOffsetY, frameWidth, frameHeight)
        } else {
            null
        }
    )

fun <T : ISizeInt> BmpCoordsWithT<T>.flippedY(): BmpCoordsWithInstance<T> =
    copy(base, bl_x, bl_y, br_x, br_y, tr_x, tr_y, tl_x, tl_y,
        virtFrame = if (virtFrame != null) {
            RectangleInt(frameOffsetX, frameHeight - height - frameOffsetY, frameWidth, frameHeight)
        } else {
            null
        }
    )

fun <T : ISizeInt> BmpCoordsWithT<T>.transformed(m: Matrix): BmpCoordsWithInstance<T> = copy(
    base,
    m.transformXf(tl_x, tl_y), m.transformYf(tl_x, tl_y),
    m.transformXf(tr_x, tr_y), m.transformYf(tr_x, tr_y),
    m.transformXf(br_x, br_y), m.transformYf(br_x, br_y),
    m.transformXf(bl_x, bl_y), m.transformYf(bl_x, bl_y),
)

fun <T : ISizeInt> BmpCoordsWithT<T>.transformed(m: Matrix3D): BmpCoordsWithInstance<T> {
    // @TODO: This allocates
    val v1 = m.transform(tl_x, tl_y, 0f, 1f)
    val v2 = m.transform(tr_x, tr_y, 0f, 1f)
    val v3 = m.transform(br_x, br_y, 0f, 1f)
    val v4 = m.transform(bl_x, bl_y, 0f, 1f)
    return copy(base, v1.x, v1.y, v2.x, v2.y, v3.x, v3.y, v4.x, v4.y)
}

fun <T : ISizeInt> BmpCoordsWithT<T>.named(name: String?): BmpCoordsWithInstance<T> = copy(name = name)

/**
 * @property virtFrame This defines a virtual frame [RectangleInt] which surrounds the bounds [RectangleInt] of the [Bitmap].
 *                     It is used in a trimmed texture atlas to specify the original size of a single texture.
 *                     X and y of virtFrame is the offset of the virtual frame to the top left edge of
 *                     the bounds rectangle. Width and height defines the size of the virtual frame.
 */
abstract class BmpSlice(
    bmpBase: Bitmap,
    val bounds: RectangleInt,
    override val name: String? = null,
    imageOrientation: ImageOrientation = ImageOrientation.ORIGINAL,
    final override val virtFrame: RectangleInt? = null,
    parentCoords: BitmapCoords? = null,
    bmpCoords: BmpCoordsWithT<ISizeInt>? = null
) : Extra, BitmapCoords {

    @Deprecated("Use imageOrientation instead of rotation")
    constructor(
        bmpBase: Bitmap,
        bounds: RectangleInt,
        name: String? = null,
        rotated: Boolean = false,
        virtFrame: RectangleInt? = null
    ): this(bmpBase, bounds, name, if (rotated) ImageOrientation.ROTATE_90 else ImageOrientation.ORIGINAL, virtFrame)

    var bmpBase: Bitmap = bmpBase
        private set
    var imageOrientation: ImageOrientation = imageOrientation
        private set

    override val base get() = bmpBase
    open val bmp: Bitmap = bmpBase
    val bmpWidth: Int get() = bmpBase.width
    val bmpHeight: Int get() = bmpBase.height

    override val left: Int get() = bounds.left
    override val top: Int get() = bounds.top
    override val width: Int get() = bounds.width
    override val height: Int get() = bounds.height
    val right get() = bounds.right
    val bottom get() = bounds.bottom

    var bmpCoords: BmpCoordsWithT<ISizeInt>
        private set

    init {
        if (bounds.x < 0) {
            bounds.width += bounds.x
            bounds.x = 0
        }
        if (bounds.y < 0) {
            bounds.height += bounds.y
            bounds.y = 0
        }
        if (bounds.width < 0) bounds.width = 0
        if (bounds.height < 0) bounds.height = 0
        if (bmpCoords != null) {
            this.bmpCoords = bmpCoords.withImageOrientation(imageOrientation)
        } else {
            val tlX: Float
            val tlY: Float
            val trX: Float
            val trY: Float
            val brX: Float
            val brY: Float
            val blX: Float
            val blY: Float

            if (parentCoords != null) {
                // Calculate bmpCoords based on parentCoords
                val dx = parentCoords.br_x - parentCoords.tl_x
                val dy = parentCoords.br_y - parentCoords.tl_y
                val x = bounds.x.toFloat()
                val y = bounds.y.toFloat()
                val w = bounds.width.toFloat()
                val h = bounds.height.toFloat()
                val bw = parentCoords.width.toFloat()
                val bh = parentCoords.height.toFloat()

                if (parentCoords.tl_x == parentCoords.tr_x) {
                    // Base is rotated
                    tlX = parentCoords.tl_x + y / bh * dx
                    tlY = parentCoords.tl_y + x / bw * dy
                    when (imageOrientation.rotation) {
                        ImageOrientation.Rotation.R90, ImageOrientation.Rotation.R270 -> {
                            brX = tlX + w / bh * dx
                            brY = tlY + h / bw * dy
                        }
                        else -> {
                            brX = tlX + h / bh * dx
                            brY = tlY + w / bw * dy
                        }
                    }
                    trX = tlX
                    trY = brY
                    blX = brX
                    blY = tlY
                } else {
                    // Base is not rotated
                    tlX = parentCoords.tl_x + x / bw * dx
                    tlY = parentCoords.tl_y + y / bh * dy
                    when (imageOrientation.rotation) {
                        ImageOrientation.Rotation.R90, ImageOrientation.Rotation.R270 -> {
                            brX = tlX + h / bw * dx
                            brY = tlY + w / bh * dy
                        }
                        else -> {
                            brX = tlX + w / bw * dx
                            brY = tlY + h / bh * dy
                        }
                    }
                    trX = brX
                    trY = tlY
                    blX = tlX
                    blY = brY
                }
            } else {
                // Calculate bmpCoords based on bounds
                val tl = Point(bounds.left.toFloat() / bmpBase.width.toFloat(), bounds.top.toFloat() / bmpBase.height.toFloat())
                val br = Point(bounds.right.toFloat() / bmpBase.width.toFloat(), bounds.bottom.toFloat() / bmpBase.height.toFloat())
                val tr = Point(br.x, tl.y)
                val bl = Point(tl.x, br.y)

                val points = arrayOf(tl, tr, br, bl)
                val offset = imageOrientation.rotation.ordinal

                val p0 = points.getCyclic(offset + 0)
                val p1 = points.getCyclic(offset + 1)
                val p2 = points.getCyclic(offset + 2)
                val p3 = points.getCyclic(offset + 3)

                tlX = p0.x.toFloat()
                tlY = p0.y.toFloat()
                trX = p1.x.toFloat()
                trY = p1.y.toFloat()
                brX = p2.x.toFloat()
                brY = p2.y.toFloat()
                blX = p3.x.toFloat()
                blY = p3.y.toFloat()
            }
            val pMinX = min(parentCoords?.tl_x ?: 0f, parentCoords?.br_x ?: 0f)
            val pMinY = min(parentCoords?.tl_y ?: 0f, parentCoords?.br_y ?: 0f)
            val pMaxX = max(parentCoords?.tl_x ?: 1f, parentCoords?.br_x ?: 1f)
            val pMaxY = max(parentCoords?.tl_y ?: 1f, parentCoords?.br_y ?: 1f)
            val coords = arrayOf(
                tlX.clamp(pMinX, pMaxX), tlY.clamp(pMinY, pMaxY),
                trX.clamp(pMinX, pMaxX), trY.clamp(pMinY, pMaxY),
                brX.clamp(pMinX, pMaxX), brY.clamp(pMinY, pMaxY),
                blX.clamp(pMinX, pMaxX), blY.clamp(pMinY, pMaxY)
            )
            val minX = coords.slice(0..6 step 2).minOrNull()!!
            val minY = coords.slice(1..7 step 2).minOrNull()!!

            for (i in 0..6 step 2) {
                if (coords[i] == minX && coords[i + 1] == minY) {
                    val rotated = imageOrientation.rotation == ImageOrientation.Rotation.R90 || imageOrientation.rotation == ImageOrientation.Rotation.R270
                    if ((tlX == trX && !rotated) || (tlX != trX && rotated)) {
                        // rotated
                        bounds.height = ((coords[(i + 4) % 8] - coords[i]) * bmpBase.width).roundToInt()
                        bounds.width = ((coords[(i + 5) % 8] - coords[i + 1]) * bmpBase.height).roundToInt()
                    } else {
                        bounds.width = ((coords[(i + 4) % 8] - coords[i]) * bmpBase.width).roundToInt()
                        bounds.height = ((coords[(i + 5) % 8] - coords[i + 1]) * bmpBase.height).roundToInt()
                    }
                    break
                }
            }

            this.bmpCoords = BmpCoordsWithInstance<ISizeInt>(bounds.size, coords[0], coords[1], coords[2], coords[3], coords[4], coords[5], coords[6], coords[7])
                .withImageOrientation(imageOrientation)
        }
    }

    val trimmed: Boolean = virtFrame != null
    override val frameWidth: Int = virtFrame?.width ?: bounds.width
    override val frameHeight: Int = virtFrame?.height ?: bounds.height

    var parent: Any? = null

    override val tl_x: Float get() = this.bmpCoords.tl_x
    override val tl_y: Float get() = this.bmpCoords.tl_y
    override val tr_x: Float get() = this.bmpCoords.tr_x
    override val tr_y: Float get() = this.bmpCoords.tr_y
    override val br_x: Float get() = this.bmpCoords.br_x
    override val br_y: Float get() = this.bmpCoords.br_y
    override val bl_x: Float get() = this.bmpCoords.bl_x
    override val bl_y: Float get() = this.bmpCoords.bl_y

    @Deprecated("Use imageOrientation")
    val rotated: Boolean = imageOrientation == ImageOrientation.ROTATE_90 || imageOrientation == ImageOrientation.ROTATE_270
    val rotatedAngle: Int = 0

    val isRotatedInBmpDeg90 = (width > 1 && tl_x == tr_x) || (height > 1 && tl_y == bl_y)

    private val pixelOffsets: IntArray by lazy {
        val x = (tl_x * baseWidth).roundToInt()
        val y = (tl_y * baseHeight).roundToInt()
        val xOff = if (tl_x > br_x) x - 1 else x
        val yOff = if (tl_y > br_y) y - 1 else y
        val xDir = if (tl_x < br_x) 1 else -1
        val yDir = if (tl_y < br_y) 1 else -1

        if (isRotatedInBmpDeg90) {
            intArrayOf(xOff, yOff, 0, xDir, 0, yDir)
        } else {
            intArrayOf(xOff, yOff, xDir, 0, yDir, 0)
        }
    }

    fun isValidBasePixelPos(x: Int, y: Int): Boolean = x in 0 until frameWidth && y in 0 until frameHeight

    fun basePixelPos(x: Int, y: Int, out: PointInt = PointInt()): PointInt? = if (isValidBasePixelPos(x, y)) basePixelPosUnsafe(x, y, out) else throw IllegalArgumentException("Point $x,$y is not in bounds of slice")

    fun basePixelPosUnsafe(x: Int, y: Int, out: PointInt = PointInt()): PointInt? = out.also {
        val offsetX = x - frameOffsetX
        val offsetY = y - frameOffsetY
        if (offsetX < 0 || offsetY < 0 || offsetX >= width || offsetY >= height)
            return null

        it.x = pixelOffsets[0] + pixelOffsets[2] * offsetX + pixelOffsets[3] * offsetY
        it.y = pixelOffsets[1] + pixelOffsets[4] * offsetY + pixelOffsets[5] * offsetX
    }

    fun readPixels(x: Int, y: Int, width: Int, height: Int, out: RgbaArray = RgbaArray(width * height), offset: Int = 0): RgbaArray {
        check(isValidBasePixelPos(x, y))
        check(isValidBasePixelPos(x + width - 1, y + height - 1))
        check(out.size >= offset + width * height)
        readPixelsUnsafe(x, y, width, height, out, offset)
        return out
    }

    fun readPixelsUnsafe(x: Int, y: Int, width: Int, height: Int, out: RgbaArray, offset: Int = 0) {
        var n = offset
        val p = PointInt()
        for (y0 in 0 until height) for (x0 in 0 until width) {
            if (basePixelPosUnsafe(x0 + x, y0 + y, p) != null) {
                out[n++] = bmpBase.getRgba(p.x, p.y)
            } else {
                out[n++] = Colors.TRANSPARENT_BLACK
            }
        }
    }

    fun getRgba(x: Int, y: Int): RGBA {
        basePixelPos(x, y)?.let {
            return bmpBase.getRgba(it.x, it.y)
        }
        return Colors.TRANSPARENT_BLACK
    }

    fun setRgba(x: Int, y: Int, value: RGBA) {
        basePixelPos(x, y).also {
            if (it != null) {
                bmpBase.setRgba(it.x, it.y, value)
            } else {
                if (x < 0 || y < 0 || x >= frameWidth || y >= frameHeight) {
                    throw IllegalArgumentException("Point $x,$y is not in bounds of slice")
                }
                bmpBase = extract()
                bounds.setBoundsTo(0, 0, bmpBase.width, bmpBase.height)
                virtFrame?.setBoundsTo(0, 0, bmpBase.width, bmpBase.height)
                bmpCoords = BmpCoordsWithInstanceBase(
                    SizeInt(bmpBase.width, bmpBase.height),
                    0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f
                )
                intArrayOf(0, 0, 1, 0, 1, 0).copyInto(pixelOffsets)
                imageOrientation = ImageOrientation.ORIGINAL
                bmpBase.setRgba(x, y, value)
            }
        }
    }

    open fun sliceWithBounds(left: Int, top: Int, right: Int, bottom: Int, name: String? = null, imageOrientation: ImageOrientation = ImageOrientation.ORIGINAL): BmpSlice = slice(RectangleInt(left, top, right - left, bottom - top), name, imageOrientation)
    open fun sliceWithSize(x: Int, y: Int, width: Int, height: Int, name: String? = null, imageOrientation: ImageOrientation = ImageOrientation.ORIGINAL): BmpSlice = slice(RectangleInt(x, y, width, height), name, imageOrientation)
    open fun slice(rect: RectangleInt, name: String? = null, imageOrientation: ImageOrientation = ImageOrientation.ORIGINAL): BmpSlice =
        BitmapSlice(bmp, rect, name, imageOrientation = imageOrientation, parentCoords = this)
    open fun slice(rect: Rectangle, name: String? = null, imageOrientation: ImageOrientation = ImageOrientation.ORIGINAL): BmpSlice = slice(rect.toInt(), name, imageOrientation)

    internal fun <T: Bitmap> extractWithBase(base: T): T
    {
        val out: T
        val x = (min(min(tl_x, tr_x), min(bl_x, br_x)) * baseWidth).roundToInt()
        val y = (min(min(tl_y, tr_y), min(bl_y, br_y)) * baseHeight).roundToInt()
        val rotated = isRotatedInBmpDeg90
        val reverseX = width > 1 && if (rotated) tl_y > br_y else tl_x > br_x
        val reverseY = height > 1 && if (rotated) tl_x > br_x else tl_y > br_y

        if (frameOffsetX == 0 && frameOffsetY == 0 && frameWidth == width && frameHeight == height) {
            out = base.extract(x, y, width, height)
        } else {
            out = base.createWithThisFormatTyped(frameWidth, frameHeight)
            if (!rotated) {
                bmp.copyUnchecked(x, y, out, frameOffsetX, frameOffsetY, width, height)
            } else {
                val rgbaArray = RgbaArray(width)
                for (x0 in 0 until height) {
                    bmp.readPixelsUnsafe(x + x0, y, 1, width, rgbaArray)
                    out.writePixelsUnsafe(frameOffsetX, frameOffsetY + x0, width, 1, rgbaArray)
                }
            }
        }
        if (reverseX) {
            out.flipX()
        }
        if (reverseY) {
            out.flipY()
        }
        return out
    }
}

val BmpSlice.nameSure: String get() = name ?: "unknown"
fun <T : Bitmap> BmpSlice.asBitmapSlice(): BitmapSlice<T> = this as BitmapSlice<T>

fun BmpSlice.getIntBounds(out: RectangleInt = RectangleInt()) = out.setTo(left, top, width, height)

fun BmpSlice.extract(): Bitmap = this.extractWithBase(bmpBase)

class BitmapSlice<out T : Bitmap>(
    override val bmp: T,
    bounds: RectangleInt,
    name: String? = null,
    imageOrientation: ImageOrientation = ImageOrientation.ORIGINAL,
    virtFrame: RectangleInt? = null,
    parentCoords: BitmapCoords? = null,
    bmpCoords: BmpCoordsWithT<ISizeInt>? = null,
) : BmpSlice(bmp, bounds, name, imageOrientation, virtFrame, parentCoords, bmpCoords), Extra by Extra.Mixin() {

    @Deprecated("Use imageOrientation instead of rotation")
    constructor(
        bmp: T,
        bounds: RectangleInt,
        name: String? = null,
        rotated: Boolean = false,
        virtFrame: RectangleInt? = null,
    ): this(bmp, bounds, name, if (rotated) ImageOrientation.ROTATE_90 else ImageOrientation.ORIGINAL, virtFrame)

    val premultiplied get() = bmp.premultiplied

    fun extract(): T = extractWithBase(bmp)

    override fun sliceWithBounds(left: Int, top: Int, right: Int, bottom: Int, name: String?, imageOrientation: ImageOrientation): BitmapSlice<T> = slice(RectangleInt(left, top, right - left, bottom - top), name, imageOrientation)
    override fun sliceWithSize(x: Int, y: Int, width: Int, height: Int, name: String?, imageOrientation: ImageOrientation): BitmapSlice<T> = slice(RectangleInt(x, y, width, height), name, imageOrientation)
    override fun slice(rect: RectangleInt, name: String?, imageOrientation: ImageOrientation): BitmapSlice<T> =
        BitmapSlice(bmp, rect, name, imageOrientation = imageOrientation, parentCoords = this)
    override fun slice(rect: Rectangle, name: String?, imageOrientation: ImageOrientation): BitmapSlice<T> = slice(rect.toInt(), name, imageOrientation)

    fun split(width: Int, height: Int): List<BitmapSlice<T>> = splitInRows(width, height)

    fun splitInRows(width: Int, height: Int): List<BitmapSlice<T>> {
        val self = this
        val nheight = self.height / height
        val nwidth = self.width / width
        return arrayListOf<BitmapSlice<T>>().apply {
            for (y in 0 until nheight) {
                for (x in 0 until nwidth) {
                    add(self.sliceWithSize(x * width, y * height, width, height))
                }
            }
        }
    }

    fun withName(name: String? = null) = copy(name = name, imageOrientation = this.imageOrientation)

    override fun toString(): String = "BitmapSlice($name:${SizeInt(bounds.width, bounds.height)})"
}

@Deprecated("Use copy with ImageOrientation")
inline fun <T : Bitmap> BitmapSlice<T>.copy(
    bmp: T = this.bmp,
    bounds: RectangleInt = this.bounds,
    name: String? = this.name,
    rotated: Boolean = this.rotated,
    virtFrame: RectangleInt? = this.virtFrame
) = BitmapSlice(bmp, bounds, name, rotated, virtFrame)

inline fun <T : Bitmap> BitmapSlice<T>.copy(
    bmp: T = this.bmp,
    bounds: RectangleInt = this.bounds,
    name: String? = this.name,
    imageOrientation: ImageOrientation = ImageOrientation.ORIGINAL,
    virtFrame: RectangleInt? = this.virtFrame,
    bmpCoords: BmpCoordsWithT<ISizeInt>? = null
) = BitmapSlice(bmp, bounds, name, imageOrientation, virtFrame, this, bmpCoords)

fun BitmapSlice<Bitmap>.virtFrame(rect: RectangleInt?) =
    if (rect != null)
        copy(imageOrientation = ImageOrientation.ORIGINAL, virtFrame = rect, bmpCoords = this.bmpCoords)
    else
        this
fun BitmapSlice<Bitmap>.virtFrame(x: Int, y: Int, w: Int, h: Int) = virtFrame(RectangleInt(x, y, w, h))

// http://pixijs.download/dev/docs/PIXI.Texture.html#Texture
fun BitmapSliceCompat(
	bmp: Bitmap,
	frame: Rectangle,
	orig: Rectangle,
	trim: Rectangle,
	rotated: Boolean,
	name: String = "unknown"
) = BitmapSlice(bmp, frame.toInt(), name = name, rotated = rotated)

fun <T : Bitmap> T.slice(bounds: RectangleInt = RectangleInt(0, 0, width, height), name: String? = null, imageOrientation: ImageOrientation = ImageOrientation.ORIGINAL): BitmapSlice<T> = BitmapSlice(this, bounds, name, imageOrientation, parentCoords = BmpCoordsWithInstance(this, 0f, 0f, 1f, 0f, 1f, 1f, 0f, 1f))
fun <T : Bitmap> T.sliceWithBounds(left: Int, top: Int, right: Int, bottom: Int, name: String? = null, imageOrientation: ImageOrientation = ImageOrientation.ORIGINAL): BitmapSlice<T> = slice(RectangleInt(left, top, right - left, bottom - top), name, imageOrientation)
fun <T : Bitmap> T.sliceWithSize(x: Int, y: Int, width: Int, height: Int, name: String? = null, imageOrientation: ImageOrientation = ImageOrientation.ORIGINAL): BitmapSlice<T> = slice(RectangleInt(x, y, width, height), name, imageOrientation)

private fun createRectangleInt(
    bleft: Int, btop: Int, bright: Int, bbottom: Int,
    left: Int, top: Int, right: Int, bottom: Int,
    allowInvalidBounds: Boolean = false
): RectangleInt = RectangleInt.fromBounds(
    (bleft + left).clamp(bleft, bright),
    (btop + top).clamp(btop, bbottom),
    (bleft + right).clamp(if (allowInvalidBounds) bleft else bleft + left, bright),
    (btop + bottom).clamp(if (allowInvalidBounds) btop else btop + top, bbottom)
)
