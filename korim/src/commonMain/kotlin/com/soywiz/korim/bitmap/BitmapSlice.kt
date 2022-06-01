package com.soywiz.korim.bitmap

import com.soywiz.kds.Extra
import com.soywiz.kds.getCyclic
import com.soywiz.kmem.clamp
import com.soywiz.korim.color.RGBA
import com.soywiz.korim.color.RgbaArray
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
import com.soywiz.korma.geom.setTo
import kotlin.math.absoluteValue
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
    val frameOffsetX: Int get() = 0
    val frameOffsetY: Int get() = 0
    val frameWidth: Int get() = width
    val frameHeight: Int get() = height
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
    override val name: String? = null
) : BmpCoordsWithInstanceBase<T>(base, tl_x, tl_y, tr_x, tr_y, br_x, br_y, bl_x, bl_y, name) {
    constructor(base: T, coords: BmpCoords, name: String? = null) : this(
        base,
        coords.tl_x, coords.tl_y,
        coords.tr_x, coords.tr_y,
        coords.br_x, coords.br_y,
        coords.bl_x, coords.bl_y,
        name
    )
}

open class BmpCoordsWithInstanceBase<T : ISizeInt>(
    override val base: T,
    override val tl_x: Float, override val tl_y: Float,
    override val tr_x: Float, override val tr_y: Float,
    override val br_x: Float, override val br_y: Float,
    override val bl_x: Float, override val bl_y: Float,
    override val name: String? = null
) : BmpCoordsWithT<T> {
    constructor(base: T, coords: BmpCoords, name: String? = null) : this(
        base,
        coords.tl_x, coords.tl_y,
        coords.tr_x, coords.tr_y,
        coords.br_x, coords.br_y,
        coords.bl_x, coords.bl_y,
        name
    )
    constructor(base: BmpCoordsWithT<T>, name: String? = null) : this(base.base, base, name ?: base.name)

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
    override var name: String? = null
) : BmpCoordsWithT<T> {
    constructor(base: T, coords: BmpCoords, name: String? = null) : this(
        base,
        coords.tl_x, coords.tl_y,
        coords.tr_x, coords.tr_y,
        coords.br_x, coords.br_y,
        coords.bl_x, coords.bl_y,
        name
    )
    constructor(base: BmpCoordsWithT<T>, name: String? = null) : this(base.base, base, name ?: base.name)

    fun setTo(
        tl_x: Float, tl_y: Float,
        tr_x: Float, tr_y: Float,
        br_x: Float, br_y: Float,
        bl_x: Float, bl_y: Float,
    ) {
        this.tl_x = tl_x
        this.tl_y = tl_y
        this.tr_x = tr_x
        this.tr_y = tr_y
        this.br_x = br_x
        this.br_y = br_y
        this.bl_x = bl_x
        this.bl_y = bl_y
    }

    fun setTo(coords: BmpCoords) {
        setTo(
            coords.tl_x, coords.tl_y, coords.tr_x, coords.tr_y,
            coords.br_x, coords.br_y, coords.bl_x, coords.bl_y,
        )
    }

    fun setTo(base: T, coords: BmpCoords, name: String? = null) {
        this.base = base
        setTo(coords)
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
    name: String? = this.name
): BmpCoordsWithInstance<T> = BmpCoordsWithInstance(base, tl_x, tl_y, tr_x, tr_y, br_x, br_y, bl_x, bl_y, name)

fun <T : ISizeInt> BmpCoordsWithT<T>.rotatedLeft(): BmpCoordsWithInstance<T> =
    copy(base, tr_x, tr_y, br_x, br_y, bl_x, bl_y, tl_x, tl_y)

fun <T : ISizeInt> BmpCoordsWithT<T>.rotatedRight(): BmpCoordsWithInstance<T> =
    copy(base, bl_x, bl_y, tl_x, tl_y, tr_x, tr_y, br_x, br_y)

fun <T : ISizeInt> BmpCoordsWithT<T>.flippedX(): BmpCoordsWithInstance<T> =
    copy(base, tr_x, tr_y, tl_x, tl_y, bl_x, bl_y, br_x, br_y)

fun <T : ISizeInt> BmpCoordsWithT<T>.flippedY(): BmpCoordsWithInstance<T> =
    copy(base, bl_x, bl_y, br_x, br_y, tr_x, tr_y, tl_x, tl_y,)

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
    val bmpBase: Bitmap,
    val bounds: RectangleInt,
    override val name: String? = null,
    val rotated: Boolean = false,
    val virtFrame: RectangleInt? = null,
    val bmpCoords: BmpCoordsWithT<ISizeInt>? = null
) : Extra, BitmapCoords {
    override val base get() = bmpBase
    open val bmp: Bitmap = bmpBase
    val bmpWidth = bmpBase.width
    val bmpHeight = bmpBase.height

    private val tl = Point(left.toFloat() / bmpBase.width.toFloat(), top.toFloat() / bmpBase.height.toFloat())
    private val br = Point(right.toFloat() / bmpBase.width.toFloat(), bottom.toFloat() / bmpBase.height.toFloat())
    private val tr = Point(br.x, tl.y)
    private val bl = Point(tl.x, br.y)

    private val points = arrayOf(tl, tr, br, bl)
    private val offset = if (rotated) 1 else 0

    private val p0 = points.getCyclic(offset + 0)
    private val p1 = points.getCyclic(offset + 1)
    private val p2 = points.getCyclic(offset + 2)
    private val p3 = points.getCyclic(offset + 3)

    override val left: Int get() = bounds.left
    override val top: Int get() = bounds.top
    override val width: Int get() = bounds.width
    override val height: Int get() = bounds.height
    val right get() = bounds.right
    val bottom get() = bounds.bottom

    val trimmed: Boolean = virtFrame != null
    override val frameOffsetX: Int = virtFrame?.x ?: 0
    override val frameOffsetY: Int = virtFrame?.y ?: 0
    override val frameWidth: Int = virtFrame?.width ?: bounds.width
    override val frameHeight: Int = virtFrame?.height ?: bounds.height

	var parent: Any? = null

    override val tl_x = bmpCoords?.tl_x ?: p0.x.toFloat()
    override val tl_y = bmpCoords?.tl_y ?: p0.y.toFloat()
    override val tr_x = bmpCoords?.tr_x ?: p1.x.toFloat()
    override val tr_y = bmpCoords?.tr_y ?: p1.y.toFloat()
    override val br_x = bmpCoords?.br_x ?: p2.x.toFloat()
    override val br_y = bmpCoords?.br_y ?: p2.y.toFloat()
    override val bl_x = bmpCoords?.bl_x ?: p3.x.toFloat()
    override val bl_y = bmpCoords?.bl_y ?: p3.y.toFloat()

    val rotatedAngle: Int = 0

    fun readPixels(x: Int, y: Int, width: Int, height: Int, out: RgbaArray = RgbaArray(width * height), offset: Int = 0): RgbaArray {
        check(x in 0 until this.width)
        check(y in 0 until this.height)
        check((x + width) in 0 .. this.width)
        check((y + height) in 0 .. this.height)
        check(out.size >= offset + width * height)
        readPixelsUnsafe(x, y, width, height, out, offset)
        return out
    }

    fun writePixels(x: Int, y: Int, width: Int, height: Int, out: RgbaArray = RgbaArray(width * height), offset: Int = 0): RgbaArray {
        check(x in 0 until this.width)
        check(y in 0 until this.height)
        check((x + width) in 0 .. this.width)
        check((y + height) in 0 .. this.height)
        check(out.size >= offset + width * height)
        writePixelsUnsafe(x, y, width, height, out, offset)
        return out
    }

    open fun readPixelsUnsafe(x: Int, y: Int, width: Int, height: Int, out: RgbaArray, offset: Int = 0) {
        var n = offset
        for (y0 in 0 until height) for (x0 in 0 until width) out[n++] = getRgba(x0 + x, y0 + y)
    }
    open fun writePixelsUnsafe(x: Int, y: Int, width: Int, height: Int, out: RgbaArray, offset: Int = 0) {
        var n = offset
        for (y0 in 0 until height) for (x0 in 0 until width) setRgba(x0 + x, y0 + y, out[n++])
    }

    fun getRgba(x: Int, y: Int): RGBA = with(bmpCoords(x, y)) {
        bmpBase.getRgba(this.x, this.y)
    }
    fun setRgba(x: Int, y: Int, value: RGBA): Unit = with(bmpCoords(x, y)) {
        bmpBase.setRgba(this.x, this.y, value)
    }

    fun bmpCoords(x: Int, y: Int): PointInt {
        if (tl_x < br_x && tl_y < br_y) {
            // No transform
            return PointInt(left + x, top + y)
        }
        // Transformed
        val dx: Float
        val dy: Float
        if (width == ((tr_x - tl_x) * baseWidth).absoluteValue.roundToInt()) {
            // Not rotated
            dx = (br_x - tl_x) / (br_x - tl_x).absoluteValue * x
            dy = (br_y - tl_y) / (br_y - tl_y).absoluteValue * y
        } else {
            // Rotated
            dx = (br_x - tl_x) / (br_x - tl_x).absoluteValue * y
            dy = (br_y - tl_y) / (br_y - tl_y).absoluteValue * x
        }
        return PointInt(
            (tl_x * baseWidth + dx).roundToInt(),
            (tl_y * baseHeight + dy).roundToInt())
    }

    open fun sliceWithBounds(left: Int, top: Int, right: Int, bottom: Int, name: String? = null): BmpSlice =
        BitmapSlice(bmp, createRectangleInt(bounds.left, bounds.top, bounds.right, bounds.bottom, left, top, right, bottom), name)
    open fun sliceWithSize(x: Int, y: Int, width: Int, height: Int, name: String? = null): BmpSlice = sliceWithBounds(x, y, x + width, y + height, name)
    open fun slice(rect: RectangleInt, name: String? = null): BmpSlice = sliceWithBounds(rect.left, rect.top, rect.right, rect.bottom, name)
    open fun slice(rect: Rectangle, name: String? = null): BmpSlice = slice(rect.toInt(), name)
}

val BmpSlice.nameSure: String get() = name ?: "unknown"
fun <T : Bitmap> BmpSlice.asBitmapSlice(): BitmapSlice<T> = this as BitmapSlice<T>

fun BmpSlice.getIntBounds(out: RectangleInt = RectangleInt()) = out.setTo(left, top, width, height)

fun BmpSlice.extract(): Bitmap = bmpBase.extract(left, top, width, height)

class BitmapSlice<out T : Bitmap>(
    override val bmp: T,
    bounds: RectangleInt,
    name: String? = null,
    rotated: Boolean = false,
    virtFrame: RectangleInt? = null,
    bmpCoords: BmpCoordsWithT<ISizeInt>? = null
) : BmpSlice(bmp, bounds, name, rotated, virtFrame, bmpCoords), Extra by Extra.Mixin() {
	val premultiplied get() = bmp.premultiplied

	fun extract(): T = bmp.extract(bounds.x, bounds.y, bounds.width, bounds.height)

	override fun sliceWithBounds(left: Int, top: Int, right: Int, bottom: Int, name: String?): BitmapSlice<T> =
		copy(bounds = createRectangleInt(bounds.left, bounds.top, bounds.right, bounds.bottom, left, top, right, bottom), name = name)
    override fun sliceWithSize(x: Int, y: Int, width: Int, height: Int, name: String?): BitmapSlice<T> = sliceWithBounds(x, y, x + width, y + height, name)
    override fun slice(rect: RectangleInt, name: String?): BitmapSlice<T> = sliceWithBounds(rect.left, rect.top, rect.right, rect.bottom, name)
    override fun slice(rect: Rectangle, name: String?): BitmapSlice<T> = slice(rect.toInt(), name)

    fun sliceWithBmpCoords(rect: RectangleInt, bmpCoords: BmpCoordsWithT<ISizeInt>, name: String?): BitmapSlice<T> =
        copy(bounds = rect, bmpCoords = bmpCoords, name = name)

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

    fun withName(name: String? = null) = copy(name = name)

	override fun toString(): String = "BitmapSlice($name:${SizeInt(bounds.width, bounds.height)})"
}

inline fun <T : Bitmap> BitmapSlice<T>.copy(
    bmp: T = this.bmp,
    bounds: RectangleInt = this.bounds,
    name: String? = this.name,
    rotated: Boolean = this.rotated,
    virtFrame: RectangleInt? = this.virtFrame,
    bmpCoords: BmpCoordsWithT<ISizeInt>? = this.bmpCoords
) = BitmapSlice(bmp, bounds, name, rotated, virtFrame, bmpCoords)

// http://pixijs.download/dev/docs/PIXI.Texture.html#Texture
fun BitmapSliceCompat(
	bmp: Bitmap,
	frame: Rectangle,
	orig: Rectangle,
	trim: Rectangle,
	rotated: Boolean,
	name: String = "unknown"
) = BitmapSlice(bmp, frame.toInt(), name = name, rotated = rotated)

fun <T : Bitmap> T.slice(bounds: RectangleInt = RectangleInt(0, 0, width, height), name: String? = null): BitmapSlice<T> = BitmapSlice<T>(this, bounds, name)
fun <T : Bitmap> T.sliceWithBounds(left: Int, top: Int, right: Int, bottom: Int, name: String? = null): BitmapSlice<T> = slice(createRectangleInt(0, 0, this.width, this.height, left, top, right, bottom), name)
fun <T : Bitmap> T.sliceWithSize(x: Int, y: Int, width: Int, height: Int, name: String? = null): BitmapSlice<T> = sliceWithBounds(x, y, x + width, y + height, name)

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
