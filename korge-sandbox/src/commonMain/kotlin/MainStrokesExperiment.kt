import com.soywiz.kds.forEachRatio01
import com.soywiz.klock.seconds
import com.soywiz.korag.AG
import com.soywiz.korge.tween.get
import com.soywiz.korge.tween.tween
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.Stage
import com.soywiz.korge.view.addUpdater
import com.soywiz.korge.view.centered
import com.soywiz.korge.view.circle
import com.soywiz.korge.view.container
import com.soywiz.korge.view.debug.DebugVertexView
import com.soywiz.korge.view.debug.debugVertexView
import com.soywiz.korge.view.graphics
import com.soywiz.korge.view.vector.gpuShapeView
import com.soywiz.korim.color.Colors
import com.soywiz.korim.font.DefaultTtfFont
import com.soywiz.korim.text.DefaultStringTextRenderer
import com.soywiz.korim.text.aroundPath
import com.soywiz.korim.text.text
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korio.async.delay
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.PointArrayList
import com.soywiz.korma.geom.bezier.Bezier
import com.soywiz.korma.geom.bezier.BezierCurve
import com.soywiz.korma.geom.bezier.StrokePointsMode
import com.soywiz.korma.geom.bezier.toDashes
import com.soywiz.korma.geom.bezier.toStrokePoints
import com.soywiz.korma.geom.firstPoint
import com.soywiz.korma.geom.lastPoint
import com.soywiz.korma.geom.shape.buildVectorPath
import com.soywiz.korma.geom.vector.LineCap
import com.soywiz.korma.geom.vector.curve
import com.soywiz.korma.geom.vector.getCurves
import com.soywiz.korma.geom.vector.line
import com.soywiz.korma.geom.vector.lineTo
import com.soywiz.korma.geom.vector.moveTo
import com.soywiz.korma.geom.vector.star
import com.soywiz.korma.interpolation.Easing

suspend fun Stage.mainStrokesExperiment2() {
    //graphics {
    //graphics { // @TODO: This is not working!
    gpuShapeView {
        updateShape {
            fill(Colors.RED) {
                //this.circle(0, 0, 100)
                curve(Bezier.Cubic(
                    Point(0, 0) + Point(200, 200),
                    Point(0, -50) + Point(200, 200),
                    Point(50, -50) + Point(200, 200),
                    Point(50, 0) + Point(200, 200)
                ))
                close()
                //line(0, 0, 100, 100)
            }
        }
    }

    val path = buildVectorPath {
        //this.circle(400, 300, 200)
        moveTo(100, 300)
        lineTo(300, 400)
        //lineTo(500, 300)
        lineTo(200, 300)
        //moveTo(100, 300)
        //quadTo(100, 500, 500, 500)
        //lineTo(500, 200)
        //lineTo(800, 200)
        //quadTo(600, 300, 800, 500)
    }
    val curves = path.getCurves()
    val points = curves.toStrokePoints(10.0, mode = StrokePointsMode.SCALABLE_POS_NORMAL_WIDTH)
    //addChild(DebugVertexView(points.vector, type = AG.DrawType.LINE_STRIP).also { it.color = Colors.WHITE })
    val dbv = debugVertexView(points.vector, type = AG.DrawType.TRIANGLE_STRIP) { color = Colors.WHITE }
    debugVertexView(PointArrayList().also {
        for (c in curves.curves) {
            val bc = c as BezierCurve
            it.add(bc.points.firstPoint())
            it.add(bc.points.lastPoint())
        }
    }, type = AG.DrawType.POINTS) { color = Colors.RED }

    launchImmediately {
        while (true) {
            //dbv.points = curves.toStrokePoints(5.0, mode = StrokePointsMode.SCALABLE_POS_NORMAL_WIDTH).vector
            //delay(0.3.seconds)
            //dbv.points = curves.toStrokePoints(10.0, endCap = LineCap.SQUARE, startCap = LineCap.SQUARE, mode = StrokePointsMode.SCALABLE_POS_NORMAL_WIDTH).vector
            //delay(0.3.seconds)
            //dbv.points = curves.toStrokePoints(5.0, endCap = LineCap.ROUND, startCap = LineCap.ROUND, mode = StrokePointsMode.SCALABLE_POS_NORMAL_WIDTH).vector
            dbv.points = curves.toStrokePoints(5.0, endCap = LineCap.ROUND, startCap = LineCap.ROUND, mode = StrokePointsMode.SCALABLE_POS_NORMAL_WIDTH).vector
            delay(0.3.seconds)
        }
    }
}

suspend fun Stage.mainStrokesExperiment() {
    val path = buildVectorPath {
        //this.circle(400, 300, 200)
        this.star(6, 200.0, 300.0, x = 400.0, y = 300.0)
        //moveTo(100, 300)
        //quadTo(100, 500, 500, 500)
        //lineTo(500, 200)
        //lineTo(800, 200)
        //quadTo(600, 300, 800, 500)
    }
        //.applyTransform(Matrix().translate(-100, -200))
    val curves = path.getCurves()

    println(curves.curves.joinToString("\n"))

    val points = curves.toStrokePoints(10.0, mode = StrokePointsMode.SCALABLE_POS_NORMAL_WIDTH)
    BezierCurve(10.0, 10.0).inflections()
    //points.scale(2.0)

    println("path=$path")



    addChild(DebugVertexView(points.vector).also { it.color = Colors.WHITE })

    fun generateDashes(offset: Double): Container {
        return Container().apply {
            val strokeSize = 180.0
            for (c in curves.toDashes(doubleArrayOf(180.0, 50.0), offset = offset)) {
                addChild(DebugVertexView(c.toStrokePoints(10.0).vector).also { it.color = Colors.BLUEVIOLET })
            }
        }
    }

    class OffsetInfo {
        var offset = 0.0
    }

    val container = container {
    }
    val offsetInfo = OffsetInfo()
    addUpdater {
        container.removeChildren()
        container.addChild(generateDashes(offsetInfo.offset))
    }

    launchImmediately {
        while (true) {
            tween(offsetInfo::offset[200.0], time = 5.seconds)
            tween(offsetInfo::offset[0.0], time = 5.seconds)
        }
    }

    val circle = circle(16.0, Colors.PURPLE).centered
    launchImmediately {
        while (true) {
            circle.tween(circle::pos.get(path, includeLastPoint = true, reversed = false), time = 5.seconds, easing = Easing.LINEAR)
            circle.tween(circle::pos.get(path, includeLastPoint = true, reversed = true), time = 5.seconds, easing = Easing.LINEAR)
        }
    }

    if (true) {
    //if (false) {
        graphics {
            //stroke(Colors.RED, StrokeInfo(thickness = 3.0)) {
            //    forEachRatio01(200) { ratio ->
            //        val p = curves.calc(ratio)
            //        if (ratio == 0.0) moveTo(p) else lineTo(p)
            //    }
            //}
            stroke(Colors.GREEN, StrokeInfo(thickness = 2.0)) {
                forEachRatio01(200) { ratio ->
                    val t = curves.ratioFromLength(ratio * curves.length)
                    //println("t=$t")
                    val p = curves.calc(t)
                    val n = curves.normal(t)
                    line(p, p + n * 10)
                }
            }
            fill(Colors.RED) {
                this.text("Hello, this is a test. Oh nice! Text following paths! How cool is that? Really cool? or not at all?\nCOOL, COOL, COOL, let's rock this path a bit more because it is cool, yeah!", DefaultTtfFont, textSize = 32.0, x = 0.0, y = 0.0, renderer = DefaultStringTextRenderer.aroundPath(path))
            }
        }
    }
}

/*
@Suppress("OPT_IN_USAGE")
class StrokeView(val points: IVectorArrayList) : View() {
    private val gpuShapeViewCommands = GpuShapeViewCommands()

    override fun renderInternal(ctx: RenderContext) {
        gpuShapeViewCommands.clear()
        gpuShapeViewCommands.verticesStart()
        println("--------- ${points.size}")
        for (n in 0 until points.size) {
            val x = points.get(n, 0).toFloat()
            val y = points.get(n, 1).toFloat()
            val u = points.get(n, 2).toFloat()
            val v = points.get(n, 3).toFloat()
            val len = points.get(n, 4).toFloat()
            val px = x + u * len
            val py = y + v * len
            //println("x=$x, y=$y, u=$u, v=$v, len=$len")
            println("px=$px, py=$py")
            gpuShapeViewCommands.addVertex(
                px, py, 0f, 0f,
                100f
            )
        }
        gpuShapeViewCommands.verticesEnd()
        gpuShapeViewCommands.draw(
            AG.DrawType.TRIANGLE_STRIP,
            GpuShapeViewPrograms.paintToShaderInfo(Matrix(), Colors.RED, 1.0, 6.0)
        )
        ctx.flush()
        gpuShapeViewCommands.render(ctx, globalMatrix, Matrix(), false, renderColorMul)
        ctx.flush()
    }
}
*/
