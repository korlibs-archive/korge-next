package com.soywiz.korim.format

import com.soywiz.kds.iterators.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.*
import com.soywiz.korim.paint.*
import com.soywiz.korim.vector.*
import com.soywiz.korim.vector.renderer.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.bezier.*
import com.soywiz.korma.geom.vector.*
import com.soywiz.korma.geom.vector.LineCap
import com.soywiz.korma.geom.vector.LineJoin
import kotlinx.cinterop.*
import platform.gdiplus.*
import platform.windows.*

class GdiNativeImage(bitmap: Bitmap32) : BitmapNativeImage(bitmap) {
    // @TODO: Enable this once ready!
    //override fun getContext2d(antialiasing: Boolean): Context2d = Context2d(GdiRenderer(bitmap, antialiasing))
}

@OptIn(ExperimentalUnsignedTypes::class)
class GdiRenderer(val bitmap: Bitmap32, val antialiasing: Boolean) : BufferedRenderer() {
    override val width: Int get() = bitmap.width
    override val height: Int get() = bitmap.height

    val gdipErrors = arrayOf(
        "Ok", "GenericError", "InvalidParameter", "OutOfMemory",
        "ObjectBusy", "InsufficientBuffer", "NotImplemented", "Win32Error", "WrongState", "Aborted",
        "FileNotFound", "ValueOverflow", "AccessDenied", "UnknownImageFormat", "FontFamilyNotFound",
        "FontStyleNotFound", "NotTrueTypeFont", "UnsupportedGdiplusVersion", "GdiplusNotInitialized",
        "PropertyNotFound", "PropertyNotSupported", "ProfileNotFound",
    )

    private fun UInt.checkp(name: String): UInt {
        val error = this
        if (error != Ok) {
            println("ERROR: $name : $error : ${gdipErrors.getOrNull(error.toInt())}")
        }
        return this
    }

    override fun isBuffering(): Boolean = true

    fun Winding.toGdip() = when (this) {
        Winding.EVEN_ODD -> FillModeAlternate
        Winding.NON_ZERO -> FillModeWinding
    }

    fun LineCap.toGdip() = when (this) {
        LineCap.BUTT -> LineCapFlat
        LineCap.SQUARE -> LineCapSquare
        LineCap.ROUND -> LineCapRound
    }

    fun LineJoin.toGdip() = when (this) {
        LineJoin.BEVEL -> LineJoinBevel
        LineJoin.MITER -> LineJoinMiter
        LineJoin.ROUND -> LineJoinRound
    }

    private fun createPath(ppath: CArrayPointer<COpaquePointerVar>, vpath: VectorPath?): COpaquePointer? {
        if (vpath == null) return null
        GdipCreatePath(vpath.winding.toGdip(), ppath).checkp("GdipCreatePath")
        val path = ppath[0]
        GdipStartPathFigure(path).checkp("GdipStartPathFigure")
        vpath.visitEdgesSimple(
            { x0, y0, x1, y1 ->
                GdipAddPathLine(path, x0.toFloat(), y0.toFloat(), x1.toFloat(), y1.toFloat()).checkp("GdipAddPathLine")
            },
            { x0, y0, x1, y1, x2, y2, x3, y3 ->
                GdipAddPathBezier(path,
                    x0.toFloat(), y0.toFloat(),
                    x1.toFloat(), y1.toFloat(),
                    x2.toFloat(), y2.toFloat(),
                    x3.toFloat(), y3.toFloat()
                ).checkp("GdipAddPathBezier")
            },
            {
                GdipClosePathFigure(path).checkp("GdipClosePathFigure")
            }
        )
        return path
    }

    override fun flushCommands(commands: List<RenderCommand>) {
        bitmap.flipY()
        memScoped {
            bitmap.intData.usePinned {  dataPin ->
                val dataPtr = dataPin.addressOf(0)
                val bmpInfo = alloc<BITMAPINFO>()
                initGdiPlusOnce()
                val pgraphics = allocArray<COpaquePointerVar>(1)
                val winhdc = GetDC(null) ?: error("winhdc = null")
                val hdc = CreateCompatibleDC(winhdc) ?: error("hdc = null")
                val bmap = CreateCompatibleBitmap(winhdc, width, height) ?: error("bmap = null")
                SelectObject(hdc, bmap)

                bmpInfo.bmiHeader.also {
                    it.biSize = sizeOf<BITMAPINFO>().convert()
                    it.biWidth = width
                    it.biHeight = height
                    it.biPlanes = 1.convert()
                    it.biBitCount = 32.convert()
                    it.biCompression = BI_RGB.convert()
                    it.biSizeImage = (width * height * 4).convert()
                    it.biXPelsPerMeter = 1024
                    it.biYPelsPerMeter = 1024
                    it.biClrUsed = 0.convert()
                    it.biClrImportant = 0.convert()
                }
                SetDIBits(hdc, bmap, 0, height.convert(), dataPtr, bmpInfo.ptr, DIB_RGB_COLORS)

                GdipCreateFromHDC(hdc, pgraphics).checkp("GdipCreateFromHDC")
                val graphics = pgraphics[0]
                GdipSetSmoothingMode(graphics, if (antialiasing) SmoothingModeHighQuality else SmoothingModeHighSpeed).checkp("GdipSetSmoothingMode")

                //val bmp = alloc<IntVar>()
                //GdipCreateBitmapFromScan0(width, height, width * 4, if (this.bitmap.premultiplied) PixelFormat32bppARGB else PixelFormat32bppPARGB, scan, bmp)
                try {
                    commands.fastForEach { command ->
                        val state = command.state
                        val fill = command.fill
                        val ppath = allocArray<COpaquePointerVar>(1)
                        val pclip = allocArray<COpaquePointerVar>(1)
                        val path = createPath(ppath, state.path)
                        val clip = createPath(pclip, state.clip)

                        val pbrush = allocArray<COpaquePointerVar>(1)
                        val pmatrix = allocArray<COpaquePointerVar>(1)
                        val pbitmap = allocArray<COpaquePointerVar>(1)
                        val style = if (fill) state.fillStyle else state.strokeStyle

                        when (style) {
                            is RGBA -> {
                                GdipCreateSolidFill(style.value.toUInt(), pbrush).checkp("GdipCreateSolidFill")
                            }
                            is BitmapPaint -> {
                                GdipCreateMatrix(pmatrix)
                                val matrix = pmatrix[0]
                                //val transform = Matrix().copyFrom(style.transform)
                                val transform = Matrix().apply {
                                    identity()
                                    multiply(this, style.transform)
                                    multiply(this, state.transform)
                                }

                                //transform.invert()
                                //style.transform
                                //transform.invert()
                                //val transform = style.transform
                                GdipSetMatrixElements(matrix, transform.af, transform.bf, transform.cf, transform.df, transform.txf, transform.tyf)
                                val bmp = style.bmp32
                                bmp.intData.usePinned { bmpPin ->
                                    val ptr = bmpPin.addressOf(0)
                                    GdipCreateBitmapFromScan0(bmp.width, bmp.height, 4 * bmp.width, if (bmp.premultiplied) PixelFormat32bppARGB else PixelFormat32bppPARGB, ptr.reinterpret(), pbitmap)
                                }
                                val wrapMode = when {
                                    style.cycleX == CycleMethod.NO_CYCLE_CLAMP && style.cycleY == CycleMethod.NO_CYCLE_CLAMP -> WrapModeClamp
                                    style.cycleX == CycleMethod.NO_CYCLE && style.cycleY == CycleMethod.NO_CYCLE -> WrapModeTile
                                    style.cycleX == CycleMethod.REFLECT && style.cycleY == CycleMethod.REPEAT -> WrapModeTileFlipX
                                    style.cycleX == CycleMethod.REPEAT && style.cycleY == CycleMethod.REFLECT -> WrapModeTileFlipY
                                    style.cycleX == CycleMethod.REFLECT && style.cycleY == CycleMethod.REFLECT -> WrapModeTileFlipXY
                                    else -> WrapModeTile
                                }
                                GdipCreateTexture(pbitmap[0], wrapMode, pbrush)
                                GdipSetTextureTransform(pbrush[0], matrix)
                            }
                            // Missing gradients
                            else -> {
                                GdipCreateSolidFill(Colors.RED.value.toUInt(), pbrush).checkp("GdipCreateSolidFill")
                            }
                        }
                        val brush = pbrush[0]
                        if (state.clip != null) {
                            GdipSetClipPath(graphics, clip, CombineModeIntersect)
                        } else {
                            GdipResetClip(graphics)
                        }

                        if (brush != null) {
                            if (fill) {
                                GdipFillPath(graphics, brush, path).checkp("GdipFillPath")
                            } else {
                                val ppen = allocArray<COpaquePointerVar>(1)
                                GdipCreatePen2(brush, state.lineWidth.toFloat(), UnitPixel, ppen).checkp("GdipCreatePen2")
                                val pen = ppen[0]
                                GdipSetPenEndCap(pen, state.endLineCap.toGdip()).checkp("GdipSetPenEndCap")
                                GdipSetPenStartCap(pen, state.startLineCap.toGdip()).checkp("GdipSetPenStartCap")
                                GdipSetPenLineJoin(pen, state.lineJoin.toGdip()).checkp("GdipSetPenLineJoin")
                                GdipSetPenMiterLimit(pen, state.miterLimit.toFloat()).checkp("GdipSetPenMiterLimit")
                                GdipDrawPath(graphics, pen, path).checkp("GdipDrawPath")
                                GdipDeletePen(pen).checkp("GdipDeletePen")
                            }
                        }

                        if (pbitmap[0] != null) GdipDeleteCachedBitmap(pbitmap[0])
                        if (pmatrix[0] != null) GdipDeleteMatrix(pmatrix[0]).checkp("GdipDeleteMatrix")
                        GdipDeleteBrush(brush).checkp("GdipDeleteBrush")
                        GdipDeletePath(path).checkp("GdipDeletePath")
                        if (clip != null) GdipDeletePath(clip).checkp("GdipDeletePath")
                        GdipFlush(graphics, FlushIntentionSync).checkp("GdipFlush")

                        GetDIBits(hdc, bmap, 0, height.convert(), dataPtr, bmpInfo.ptr, DIB_RGB_COLORS)
                    }
                } finally {
                    GdipDeleteGraphics(graphics)
                    DeleteObject(bmap)
                    DeleteDC(hdc)
                }
            }
        }
        bitmap.flipY()
    }
}
