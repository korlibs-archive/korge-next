package com.soywiz.korge.view.vector

import com.soywiz.kds.*
import com.soywiz.klock.*
import com.soywiz.klogger.*
import com.soywiz.korag.*
import com.soywiz.korge.annotations.*
import com.soywiz.korge.internal.*
import com.soywiz.korge.render.*
import com.soywiz.korge.view.*
import com.soywiz.korge.view.BlendMode
import com.soywiz.korim.paint.*
import com.soywiz.korim.vector.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.Line
import com.soywiz.korma.geom.bezier.*
import com.soywiz.korma.geom.shape.*
import com.soywiz.korma.geom.vector.*
import kotlin.math.*

@KorgeExperimental
inline fun Container.gpuShapeView(
    build: ShapeBuilder.() -> Unit,
    antialiased: Boolean = true,
    callback: @ViewDslMarker GpuShapeView.() -> Unit = {}
) =
    GpuShapeView(buildShape { build() }, antialiased).addTo(this, callback)

@KorgeExperimental
inline fun Container.gpuShapeView(
    shape: Shape,
    antialiased: Boolean = true,
    callback: @ViewDslMarker GpuShapeView.() -> Unit = {}
) =
    GpuShapeView(shape, antialiased).addTo(this, callback)

// @TODO: Optimize convex shapes (for example a circle, a rect, a rounded rect, shouldn't require stencils)
@KorgeExperimental
@OptIn(KorgeInternal::class)
open class GpuShapeView(
    shape: Shape = EmptyShape,
    antialiased: Boolean = true,
    // @TODO: Not used, but to be compatible with Graphics
    var autoScaling: Boolean = true
) : View(), Anchorable {
    private val pointCache = FastIdentityMap<VectorPath, PointArrayList>()
    private val gpuShapeViewCommands = GpuShapeViewCommands()
    private val bb = BoundsBuilder()
    var bufferWidth = 1000
    var bufferHeight = 1000
    private val pointsScope = PointPool(128)
    private val ab = SegmentInfo()
    private val bc = SegmentInfo()
    val gpuShapeViewPaintShader = GpuShapeViewPaintShader()
    private var notifyAboutEvenOdd = false

    override var anchorX: Double = 0.0 ; set(value) { field = value; invalidate() }
    override var anchorY: Double = 0.0 ; set(value) { field = value; invalidate() }

    var applyScissor: Boolean = true

    var antialiasedMasks: Boolean = false

    var antialiased: Boolean = antialiased
        set(value) {
            field = value
            invalidateShape()
        }
    var shape: Shape = shape
        set(value) {
            field = value
            invalidateShape()
        }

    private fun invalidateShape() {
        pointCache.clear()

        gpuShapeViewCommands.clear()
        gpuShapeViewCommands.clearStencil()
        renderShape(shape)
        gpuShapeViewCommands.finish()

        //strokeCache.clear()
    }

    override fun getLocalBoundsInternal(out: Rectangle) {
        shape.getBounds(out, bb)
        out.setXY(
            out.x - out.width * anchorX,
            out.y - out.height * anchorY
        )
    }


    inline fun updateShape(block: ShapeBuilder.() -> Unit) {
        this.shape = buildShape { block() }
    }

    val Shape.requireStencil: Boolean
        get() {
            return when (this) {
                EmptyShape -> false
                is CompoundShape -> this.components.any { it.requireStencil }
                is TextShape -> this.primitiveShapes.requireStencil
                is FillShape -> {
                    // @TODO: Check if the shape is convex. If it is context we might not need the stencil
                    true
                }
                is PolylineShape -> {
                    false
                }
                else -> true // UNKNOWN
            }
        }

    override fun renderInternal(ctx: RenderContext) {
        ctx.flush()

        //val doRequireTexture = shape.requireStencil
        val doRequireTexture = false

        val time = measureTime {
            if (doRequireTexture) {
                val currentRenderBuffer = ctx.ag.currentRenderBufferOrMain
                //ctx.renderToTexture(currentRenderBuffer.width, currentRenderBuffer.height, {
                bufferWidth = currentRenderBuffer.width
                bufferHeight = currentRenderBuffer.height
                ctx.renderToTexture(bufferWidth, bufferHeight, {
                    gpuShapeViewCommands.draw(ctx, globalMatrix)
                }, hasDepth = false, hasStencil = true, msamples = 1) { texture ->
                    gpuShapeViewCommands.clearStencil()
                    ctx.useBatcher {
                        it.drawQuad(texture, x = 0f, y = 0f)
                    }
                }
            } else {
                gpuShapeViewCommands.draw(ctx, globalMatrix)
            }
        }

        //println("GPU RENDER IN: $time, doRequireTexture=$doRequireTexture")
    }

    private fun renderShape(shape: Shape) {
        when (shape) {
            EmptyShape -> Unit
            is CompoundShape -> for (v in shape.components) renderShape(v)
            is TextShape -> renderShape(shape.primitiveShapes)
            is FillShape -> renderFill(shape)
            is PolylineShape -> renderStroke(
                shape.transform,
                shape.path,
                shape.paint,
                shape.globalAlpha,
                shape.thickness,
                shape.scaleMode,
                shape.startCaps,
                shape.endCaps,
                shape.lineJoin,
                shape.miterLimit,
            )
            //is PolylineShape -> renderShape(ctx, shape.fillShape)
            else -> TODO("shape=$shape")
        }
    }

    class SegmentInfo {
        lateinit var s: IPoint // start
        lateinit var e: IPoint // end
        lateinit var line: Line
        var angleSE: Angle = 0.degrees
        var angleSE0: Angle = 0.degrees
        var angleSE1: Angle = 0.degrees
        lateinit var s0: IPoint
        lateinit var s1: IPoint
        lateinit var e0: IPoint
        lateinit var e1: IPoint
        lateinit var e0s: IPoint
        lateinit var e1s: IPoint
        lateinit var s0s: IPoint
        lateinit var s1s: IPoint

        fun p(index: Int) = if (index == 0) s else e
        fun p0(index: Int) = if (index == 0) s0 else e0
        fun p1(index: Int) = if (index == 0) s1 else e1

        fun setTo(s: Point, e: Point, lineWidth: Double, scope: PointPool): Unit {
            this.s = s
            this.e = e
            scope.apply {
                line = Line(s, e)
                angleSE = Angle.between(s, e)
                angleSE0 = angleSE - 90.degrees
                angleSE1 = angleSE + 90.degrees
                s0 = Point(s, angleSE0, length = lineWidth)
                s1 = Point(s, angleSE1, length = lineWidth)
                e0 = Point(e, angleSE0, length = lineWidth)
                e1 = Point(e, angleSE1, length = lineWidth)

                s0s = Point(s0, angleSE + 180.degrees, length = lineWidth)
                s1s = Point(s1, angleSE + 180.degrees, length = lineWidth)

                e0s = Point(e0, angleSE, length = lineWidth)
                e1s = Point(e1, angleSE, length = lineWidth)
            }
        }
    }

    private fun pointsAdd(p1: IPoint, p2: IPoint, lineWidth: Float) {
        //val lineWidth = 0f
        val p1x = p1.x.toFloat()
        val p1y = p1.y.toFloat()
        val p2x = p2.x.toFloat()
        val p2y = p2.y.toFloat()
        gpuShapeViewCommands.addVertex(p1x, p1y, p1x, p1y, -lineWidth)
        gpuShapeViewCommands.addVertex(p2x, p2y, p2x, p2y, +lineWidth)
    }

    private fun pointsAddCubicOrLine(
        scope: PointPool, fix: IPoint,
        p0: IPoint, p0s: IPoint, p1s: IPoint, p1: IPoint,
        lineWidth: Double,
        reverse: Boolean = false,
        start: Boolean = true,
    ) {
        val NPOINTS = 15
        scope.apply {
            for (i in 0..NPOINTS) {
                val ratio = i.toDouble() / NPOINTS.toDouble()
                val pos = when {
                    start -> Bezier.cubicCalc(p0, p0s, p1s, p1, ratio, MPoint())
                    else -> Bezier.cubicCalc(p1, p1s, p0s, p0, ratio, MPoint())
                }
                when {
                    reverse -> pointsAdd(fix, pos, lineWidth.toFloat())
                    else -> pointsAdd(pos, fix, lineWidth.toFloat())
                }
            }
        }
    }

    //data class StrokeRenderCacheKey(
    //    val lineWidth: Double,
    //    val path: VectorPath,
    //    // @TODO: we shouldn't require this matrix. We should be able to compute everything without the matrix, and apply it at the shader level
    //    val matrix: Matrix
    //)

    //private val strokeCache = HashMap<StrokeRenderCacheKey, StrokeRenderData>()

    private fun renderStroke(
        stateTransform: Matrix,
        strokePath: VectorPath,
        paint: Paint,
        globalAlpha: Double,
        lineWidth: Double,
        scaleMode: LineScaleMode,
        startCap: LineCap,
        endCap: LineCap,
        join: LineJoin,
        miterLimit: Double,
        forceClosed: Boolean? = null,
    ) {
        //val m0 = root.globalMatrix
        //val mt0 = m0.toTransform()
        val m = globalMatrix
        val mt = m.toTransform()

        val scaleWidth = scaleMode.anyScale
        //val lineScale = mt0.scaleAvg.absoluteValue / mt.scaleAvg.absoluteValue
        val lineScale = mt.scaleAvg.absoluteValue
        //println("lineScale=$lineScale")
        val flineWidth = if (scaleWidth) lineWidth * lineScale else lineWidth
        val lineWidth = if (antialiased) (flineWidth * 0.5) + 0.25 else flineWidth * 0.5

        //val lineWidth = 0.2
        //val lineWidth = 20.0
        val fLineWidth = max((lineWidth).toFloat(), 1.5f)

        // @TODO: Curve points aren't joints and shouldn't require extra computations! Let's handle paths manually

        /*
        var startX = 0.0
        var startY = 0.0
        var lastX = 0.0
        var lastY = 0.0

        strokePath.visitCmds(
            moveTo = { x, y ->
                startX = x
                startY = y
                lastX = x
                lastY = y
            },
            lineTo = { x, y ->
                lastX = x
                lastY = y
            },
            quadTo = { x1, y1, x2, y2 ->
                lastX = x2
                lastY = y2
            },
            cubicTo = { x1, y1, x2, y2, x3, y3 ->
                lastX = x3
                lastY = y3
            },
            close = {

            }
        )
        */

        //val cacheKey = StrokeRenderCacheKey(lineWidth, strokePath, m)

        //val data = strokeCache.getOrPut(cacheKey) {
        val data = run {
            //val pathList = strokePath.toPathPointList(m, emitClosePoint = false)
            val pathList = strokePath.toPathPointList(Matrix(), emitClosePoint = false)
            //println(pathList.size)
            for (ppath in pathList) {
                gpuShapeViewCommands.verticesStart()
                val loop = forceClosed ?: ppath.closed
                //println("Points: " + ppath.toList())
                val end = if (loop) ppath.size + 1 else ppath.size
                //val end = if (loop) ppath.size else ppath.size

                for (n in 0 until end) pointsScope {
                    val isFirst = n == 0
                    val isLast = n == ppath.size - 1
                    val isFirstOrLast = isFirst || isLast
                    val a = ppath.getCyclic(n - 1)
                    val b = ppath.getCyclic(n) // Current point
                    val c = ppath.getCyclic(n + 1)
                    val orientation = Point.orientation(a, b, c).sign.toInt()
                    //val angle = Angle.between(b - a, c - a)
                    //println("angle = $angle")

                    ab.setTo(a, b, lineWidth, this)
                    bc.setTo(b, c, lineWidth, this)

                    when {
                        // Start/End caps
                        !loop && isFirstOrLast -> {
                            val start = n == 0

                            val cap = if (start) startCap else endCap
                            val index = if (start) 0 else 1
                            val segment = if (start) bc else ab
                            val p1 = segment.p1(index)
                            val p0 = segment.p0(index)
                            val iangle = if (start) segment.angleSE - 180.degrees else segment.angleSE

                            when (cap) {
                                LineCap.BUTT -> pointsAdd(p1, p0, fLineWidth)
                                LineCap.SQUARE -> {
                                    val p1s = Point(p1, iangle, lineWidth)
                                    val p0s = Point(p0, iangle, lineWidth)
                                    pointsAdd(p1s, p0s, fLineWidth)
                                }
                                LineCap.ROUND -> {
                                    val p1s = Point(p1, iangle, lineWidth * 1.5)
                                    val p0s = Point(p0, iangle, lineWidth * 1.5)
                                    pointsAddCubicOrLine(
                                        this,
                                        p0,
                                        p0,
                                        p0s,
                                        p1s,
                                        p1,
                                        lineWidth,
                                        reverse = false,
                                        start = start
                                    )
                                }
                            }
                        }
                        // Joins
                        else -> {
                            val m0 = Line.getIntersectXY(ab.s0, ab.e0, bc.s0, bc.e0, MPoint()) // Outer (CW)
                            val m1 = Line.getIntersectXY(ab.s1, ab.e1, bc.s1, bc.e1, MPoint()) // Inner (CW)
                            val e1 = m1 ?: ab.e1
                            val e0 = m0 ?: ab.e0
                            val round = join == LineJoin.ROUND
                            val dorientation = when {
                                (join == LineJoin.MITER && e1.distanceTo(b) <= (miterLimit * lineWidth)) -> 0
                                else -> orientation
                            }

                            if (loop && isFirst) {
                                //println("forientation=$forientation")
                                when (dorientation) {
                                    -1 -> pointsAdd(e1, bc.s0, fLineWidth)
                                    0 -> pointsAdd(e1, e0, fLineWidth)
                                    +1 -> pointsAdd(bc.s1, e0, fLineWidth)
                                }
                            } else {
                                //println("dorientation=$dorientation")
                                when (dorientation) {
                                    // Turn right
                                    -1 -> {
                                        val fp = m1 ?: ab.e1
                                        //points.addCubicOrLine(this, true, fp, p0, p0, p1, p1, lineWidth, cubic = false)
                                        if (round) {
                                            pointsAddCubicOrLine(
                                                this, fp,
                                                ab.e0, ab.e0s, bc.s0s, bc.s0,
                                                lineWidth, reverse = true
                                            )
                                        } else {
                                            pointsAdd(fp, ab.e0, fLineWidth)
                                            pointsAdd(fp, bc.s0, fLineWidth)
                                        }
                                    }
                                    // Miter
                                    0 -> pointsAdd(e1, e0, fLineWidth)
                                    // Turn left
                                    1 -> {
                                        val fp = m0 ?: ab.e0
                                        if (round) {
                                            pointsAddCubicOrLine(
                                                this, fp,
                                                ab.e1, ab.e1s, bc.s1s, bc.s1,
                                                lineWidth, reverse = false
                                            )
                                        } else {
                                            pointsAdd(ab.e1, fp, fLineWidth)
                                            pointsAdd(bc.s1, fp, fLineWidth)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                val info = gpuShapeViewPaintShader.paintToShaderInfo(
                    stateTransform = stateTransform,
                    matrix = null,
                    paint = paint,
                    globalAlpha = globalAlpha,
                    lineWidth = lineWidth,
                    out = gpuShapeViewPaintShader.tempPaintShader
                )

                gpuShapeViewCommands.draw(AG.DrawType.TRIANGLE_STRIP, info)
            }
        }

        //println("vertexCount=$vertexCount")
    }
    //private var lastPointsString = ""

    class PointsResult(val bounds: Rectangle, val data: FloatArray, val vertexCount: Int)

    private fun getPointsForPath(path: VectorPath, m: Matrix? = null, gpuShapeViewCommands: GpuShapeViewCommands? = null): PointsResult {
        val points: PointArrayList = pointCache.getOrPut(path) {
            val points = PointArrayList()
            path.emitPoints2 { x, y, move ->
                points.add(x, y)
            }
            points
        }

        val bb = BoundsBuilder()
        bb.reset()

        val data = FloatArray(points.size * 2 + 4)
        for (n in 0 until points.size + 1) {
            val x = points.getX(n % points.size).toFloat()
            val y = points.getY(n % points.size).toFloat()
            val tx = m?.transformXf(x, y) ?: x
            val ty = m?.transformYf(x, y) ?: y
            data[(n + 1) * 2 + 0] = tx
            data[(n + 1) * 2 + 1] = ty
            bb.add(tx, ty)
        }
        data[0] = ((bb.xmax + bb.xmin) / 2).toFloat()
        data[1] = ((bb.ymax + bb.ymin) / 2).toFloat()
        val bounds = bb.getBounds()
        if (gpuShapeViewCommands != null) {
            for (n in 0 until data.size step 2) {
                gpuShapeViewCommands.addVertex(data[n + 0], data[n + 1], data[n + 0], data[n + 1], 0f)
            }
        }
        return PointsResult(bounds, data, points.size + 2)
    }

    private fun renderFill(shape: FillShape) {
        //val m = localMatrix
        //val stage = stage!!
        //val stageMatrix = stage.localMatrix
        //println("stage=$stage, globalMatrix=$stageMatrix")

        if (shape.path.winding != Winding.EVEN_ODD) {
            if (!notifyAboutEvenOdd) {
                notifyAboutEvenOdd = true
                Console.error("ERROR: Currently only supported EVEN_ODD winding, but used ${shape.path.winding}")
            }
        }

        val pathDataStart = gpuShapeViewCommands.verticesStart()
        val pathData = getPointsForPath(shape.path, globalMatrix, gpuShapeViewCommands)
        val pathDataEnd = gpuShapeViewCommands.verticesEnd()

        val pathBounds = pathData.bounds

        val clipDataStart = gpuShapeViewCommands.verticesStart()
        val clipData = shape.clip?.let { getPointsForPath(it, globalMatrix, gpuShapeViewCommands) }
        val clipDataEnd = gpuShapeViewCommands.verticesEnd()
        val clipBounds = clipData?.bounds

        val scissorBounds: Rectangle = when {
            clipBounds != null -> Rectangle().also { it.setToIntersection(pathBounds, clipBounds) }
            else -> pathBounds
        }

        // @TODO: Scissor should be the intersection between the path bounds and the clipping bounds

        val rscissor: AG.Scissor = AG.Scissor().setTo(scissorBounds)
        //val scissor = if (applyScissor) AG.Scissor.combine(ctx.batch.scissor, rscissor) else rscissor
        val scissor: AG.Scissor? = null

        gpuShapeViewCommands.clearStencil(0, scissor = scissor)

        var stencilEqualsValue = 0b00000001
        writeStencil(pathDataStart, pathDataEnd, scissor, AG.StencilState(
            enabled = true,
            compareMode = AG.CompareMode.ALWAYS,
            writeMask = 0b00000001,
            actionOnBothPass = AG.StencilOp.INVERT,
        ))

        // @TODO: Should we do clipping other way?
        if (clipData != null) {
            writeStencil(clipDataStart, clipDataEnd, scissor, AG.StencilState(
                enabled = true,
                compareMode = AG.CompareMode.ALWAYS,
                writeMask = 0b00000010,
                actionOnBothPass = AG.StencilOp.INVERT,
            ))
            stencilEqualsValue = 0b00000011
        }

        // Antialias when we don't have clipping
        // @TODO: How should we handle clipping antialiasing? Should we render the mask into a buffer first, and then do the masking?
        /*
        if (antialiased && shape.clip == null) {
            renderStroke(
                ctx = ctx,
                stateTransform = shape.transform,
                //stateTransform = Matrix(),
                strokePath = shape.path,
                paint = shape.paint,
                globalAlpha = shape.globalAlpha,
                lineWidth = 2.0,
                scaleMode = LineScaleMode.NONE,
                startCap = LineCap.BUTT,
                endCap = LineCap.BUTT,
                join = LineJoin.MITER,
                miterLimit = 0.5,
                forceClosed = true,
                scissor = scissor,
                stencil = AG.StencilState(
                    enabled = true,
                    compareMode = AG.CompareMode.NOT_EQUAL,
                    referenceValue = stencilEqualsValue,
                    writeMask = 0,
                )
            )
        }
        */

        // renderFill
        run {
            val paint = shape.paint
            val stateTransform = shape.transform
            val globalAlpha = shape.globalAlpha
            val paintShader = gpuShapeViewPaintShader.paintToShaderInfo(
                stateTransform, null, paint, globalAlpha,
                lineWidth = 10000000.0,
                out = gpuShapeViewPaintShader.tempPaintShader
            ) ?: return

            val x0 = 0f
            val y0 = 0f
            val x1 = bufferWidth.toFloat()
            val y1 = bufferHeight.toFloat()

            val vstart = gpuShapeViewCommands.verticesStart()
            gpuShapeViewCommands.addVertex(x0, y0, x0, y0, 10000000f)
            gpuShapeViewCommands.addVertex(x1, y0, x1, y0, 10000000f)
            gpuShapeViewCommands.addVertex(x1, y1, x1, y1, 10000000f)
            gpuShapeViewCommands.addVertex(x0, y1, x0, y1, 10000000f)
            val vend = gpuShapeViewCommands.verticesEnd()

            //println("[($lx0,$ly0)-($lx1,$ly1)]")

            paintShader.uniforms.put(GpuShapeViewPrograms.u_GlobalAlpha, globalAlpha.toFloat())

            gpuShapeViewCommands.draw(
                AG.DrawType.TRIANGLE_FAN,
                paintShader = paintShader,
                scissor = scissor,
                colorMask = AG.ColorMaskState(true, true, true, true),
                stencil = AG.StencilState(
                    //enabled = true,
                    enabled = false,
                    compareMode = AG.CompareMode.EQUAL,
                    referenceValue = stencilEqualsValue,
                    writeMask = 0,
                ),
                startIndex = vstart,
                endIndex = vend,
            )
        }
    }

    private fun writeStencil(pathDataStart: Int, pathDataEnd: Int, scissor: AG.Scissor?, stencil: AG.StencilState) {
        gpuShapeViewCommands.draw(
            AG.DrawType.TRIANGLE_FAN,
            startIndex = pathDataStart,
            endIndex = pathDataEnd,
            paintShader = gpuShapeViewPaintShader.stencilPaintShader,
            scissor = scissor,
            colorMask = AG.ColorMaskState(false, false, false, false),
            blendMode = BlendMode.NONE.factors,
            stencil = stencil
        )
    }

    init {
        invalidateShape()
    }
}

