import com.soywiz.klock.seconds
import com.soywiz.korge.tween.get
import com.soywiz.korge.tween.tween
import com.soywiz.korge.view.Stage
import com.soywiz.korge.view.addUpdater
import com.soywiz.korge.view.vector.gpuShapeView
import com.soywiz.korim.color.Colors
import com.soywiz.korim.vector.EmptyShape
import com.soywiz.korio.async.launch
import com.soywiz.korma.geom.Point
import com.soywiz.korma.geom.bezier.Bezier
import com.soywiz.korma.geom.vector.circle
import com.soywiz.korma.geom.vector.curve
import com.soywiz.korma.geom.vector.moveTo
import com.soywiz.korma.geom.vector.rect
import com.soywiz.korma.random.get
import kotlin.random.Random

suspend fun Stage.mainBezier() {
    val shape = gpuShapeView(EmptyShape)
    fun getRandomPoint() = Point(Random[100..500], Random[100..500])
    class Bez {
        var p1 = getRandomPoint()
        var p2 = getRandomPoint()
        var p3 = getRandomPoint()
        var p4 = getRandomPoint()
    }
    val bez = Bez()

    addUpdater {
        shape.updateShape {
            //val curve = Bezier.Quad(bez.p1, bez.p2, bez.p3)
            val curve = Bezier.Cubic(bez.p1, bez.p2, bez.p3, bez.p4)
            stroke(Colors.YELLOW, lineWidth = 2.0) {
                this.circle(bez.p1, 4.0)
                this.circle(bez.p2, 4.0)
                this.circle(bez.p3, 4.0)
                this.circle(bez.p4, 4.0)
            }
            stroke(Colors.WHITE, lineWidth = 4.0) {
                beginPath()
                curve(curve)
            }
            stroke(Colors.RED, lineWidth = 2.0) {
                rect(curve.getBounds())
            }
        }
    }

    launch {
        while (true) {
            tween(
                bez::p1[getRandomPoint()],
                bez::p2[getRandomPoint()],
                bez::p3[getRandomPoint()],
                bez::p4[getRandomPoint()],
                time = 1.seconds
            )
        }
    }
}
