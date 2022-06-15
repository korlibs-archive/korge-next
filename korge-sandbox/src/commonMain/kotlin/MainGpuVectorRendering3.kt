import com.soywiz.korag.AG
import com.soywiz.korev.Key
import com.soywiz.korge.input.keys
import com.soywiz.korge.view.Stage
import com.soywiz.korge.view.debug.debugVertexView
import com.soywiz.korge.view.text
import com.soywiz.korge.view.vector.gpuShapeView
import com.soywiz.korge.view.xy
import com.soywiz.korim.color.Colors
import com.soywiz.korim.text.TextAlignment
import com.soywiz.korma.geom.Rectangle
import com.soywiz.korma.geom.bezier.StrokePointsMode
import com.soywiz.korma.geom.bezier.toStrokePointsList
import com.soywiz.korma.geom.shape.buildVectorPath
import com.soywiz.korma.geom.vector.LineJoin
import com.soywiz.korma.geom.vector.StrokeInfo
import com.soywiz.korma.geom.vector.VectorPath
import com.soywiz.korma.geom.vector.lineTo
import com.soywiz.korma.geom.vector.moveTo
import com.soywiz.korma.geom.vector.rect
import com.soywiz.korma.geom.vector.toCurves

suspend fun Stage.mainGpuVectorRendering3() {
    /*
    gpuShapeView({
        keep {
            translate(100, 100)
            fill(Colors.WHITE) {
                rect(-10, -10, 120, 120)
                rectHole(40, 40, 80, 80)
            }
        }
    }) {
        rotation = 5.degrees
        //debugDrawOnlyAntialiasedBorder = true
        keys {
            down(Key.N0) { antialiased = !antialiased }
            down(Key.N1) { debugDrawOnlyAntialiasedBorder = !debugDrawOnlyAntialiasedBorder }
        }
    }
    */

    fun Stage.debugPath(desc: String, x: Number, y: Number, strokeInfo: StrokeInfo, path: VectorPath) {
        val pointsList = path.toCurves().toStrokePointsList(strokeInfo, generateDebug = true, mode = StrokePointsMode.NON_SCALABLE_POS)

        gpuShapeView({
            stroke(Colors.RED, strokeInfo) {
                path(path)
            }
        }) {
            //rotation = 5.degrees
            keys {
                down(Key.N0) { antialiased = !antialiased }
            }
        }.xy(x.toDouble(), y.toDouble())

        //debugVertexView(pointsList.map { it.vector }, type = AG.DrawType.POINTS)
        text(desc, alignment = TextAlignment.BASELINE_LEFT).xy(x.toDouble(), y.toDouble() - 5.0)
        debugVertexView(pointsList.map { it.vector }, type = AG.DrawType.LINE_STRIP).xy(x.toDouble(), y.toDouble()).apply {
            keys {
                down(Key.N9) { visible = !visible }
            }
        }
    }

    class DemoInfo(val index: Int, val name: String, val info: StrokeInfo)

    //val strokeInfo = StrokeInfo(thickness = 10.0, join = LineJoin.MITER)
    for ((index, strokeInfo) in listOf(
        StrokeInfo(thickness = 10.0, join = LineJoin.BEVEL),
        StrokeInfo(thickness = 10.0, join = LineJoin.MITER),
        StrokeInfo(thickness = 10.0, join = LineJoin.ROUND),
    ).withIndex()) {
        val sx = index * 400
        text("${strokeInfo.join}", color = Colors.YELLOWGREEN).xy(sx + 50, 10)
        debugPath("Lines CW", sx + 50, 50, strokeInfo, buildVectorPath {
            //rect(10, 10, 100, 100)
            moveTo(10, 10)
            lineTo(100, 10)
            lineTo(100, 100)
        })

        debugPath("Lines CCW", sx + 50, 250, strokeInfo, buildVectorPath {
            //rect(10, 10, 100, 100)
            moveTo(10, 10)
            lineTo(10, 100)
            lineTo(100, 100)
        })

        debugPath("Rect closed", sx + 200, 50, strokeInfo, buildVectorPath {
            rect(Rectangle.fromBounds(10, 10, 100, 100))
        })

        debugPath("Rect not closed", sx + 200, 250, strokeInfo, buildVectorPath {
            moveTo(10, 10)
            lineTo(100, 10)
            lineTo(100, 100)
            lineTo(10, 100)
            lineTo(10, 10)
        })

        debugPath("Pointed CW", sx + 50, 450, strokeInfo, buildVectorPath {
            moveTo(10, 10)
            lineTo(100, 10)
            lineTo(10, 100)
        })

        debugPath("Pointed CW", sx + 250, 450, strokeInfo, buildVectorPath {
            moveTo(10, 10)
            lineTo(100, 10)
            lineTo(10, 30)
        })
    }
}
