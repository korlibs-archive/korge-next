import com.soywiz.kds.forEachRatio01
import com.soywiz.korge.view.Stage
import com.soywiz.korge.view.debug.DebugVertexView
import com.soywiz.korge.view.graphics
import com.soywiz.korim.color.Colors
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korma.geom.bezier.BezierCurve
import com.soywiz.korma.geom.bezier.StrokePointsMode
import com.soywiz.korma.geom.bezier.toStrokePoints
import com.soywiz.korma.geom.shape.buildVectorPath
import com.soywiz.korma.geom.vector.getCurves
import com.soywiz.korma.geom.vector.line
import com.soywiz.korma.geom.vector.lineTo
import com.soywiz.korma.geom.vector.moveTo
import com.soywiz.korma.geom.vector.quadTo

suspend fun Stage.mainStrokesExperiment() {
    val path = buildVectorPath {
        moveTo(100, 300)

        //lineTo(500, 500)

        quadTo(100, 500, 500, 500)
        lineTo(500, 200)
        lineTo(800, 200)
        quadTo(600, 300, 800, 500)
    }
    val curves = path.getCurves()

    println(curves.curves.joinToString("\n"))

    val points = curves.toStrokePoints(10.0, mode = StrokePointsMode.SCALABLE_POS_NORMAL_WIDTH)
    BezierCurve(10.0, 10.0).inflections()
    //points.scale(2.0)

    println("path=$path")

    addChild(DebugVertexView(points.vector))

    if (true) {
    //if (false) {
        graphics {
            stroke(Colors.RED, StrokeInfo(thickness = 3.0)) {
                forEachRatio01(200) { ratio ->
                    val p = curves.calc(ratio)
                    if (ratio == 0.0) moveTo(p) else lineTo(p)
                }
            }
            stroke(Colors.GREEN, StrokeInfo(thickness = 2.0)) {
                forEachRatio01(200) { ratio ->
                    val t = curves.ratioFromLength(ratio * curves.length)
                    //println("t=$t")
                    val p = curves.calc(t)
                    val n = curves.normal(t)
                    line(p, p + n * 10)
                }
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
