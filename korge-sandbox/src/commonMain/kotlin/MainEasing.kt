import com.soywiz.korge.view.Graphics
import com.soywiz.korge.view.Stage
import com.soywiz.korge.view.View
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.graphics
import com.soywiz.korge.view.line
import com.soywiz.korge.view.vector.GpuShapeView
import com.soywiz.korge.view.vector.gpuShapeView
import com.soywiz.korge.view.xy
import com.soywiz.korim.color.Colors
import com.soywiz.korim.vector.StrokeInfo
import com.soywiz.korma.geom.vector.line
import com.soywiz.korma.interpolation.Easing

suspend fun Stage.mainEasing() {
    fun renderEasing(easing: Easing): View {
        return GpuShapeView().apply {
            updateShape {
                stroke(Colors.WHITE, lineWidth = 3.0) {
                    var first = true
                    val overflow = 0
                    for (n in (-overflow)..(64 + overflow)) {
                        val ratio = n.toDouble() / 64.0
                        val x = n.toDouble()
                        val y = easing(ratio) * 64
                        //println("x=$x, y=$y, ratio=$ratio")
                        if (first) {
                            first = false
                            moveTo(x, -y)
                        } else {
                            lineTo(x, -y)
                        }
                    }
                }
                stroke(Colors.RED, lineWidth = 2.0) {
                    this.line(0.0, 0.0, 0.0, -64.0)
                    this.line(0.0, 0.0, 64.0, 0.0)
                }

            }
        }
    }

    renderEasing(Easing.EASE_IN).xy(100, 200).addTo(this)
    renderEasing(Easing.cubic(.86,.13,.22,.84)).xy(200, 200).addTo(this)
    //renderEasing(Easing.cubic(.17, .67, .83, .67)).xy(200, 200).addTo(this)
    //renderEasing(Easing.cubic(.4, .6, .6, .4)).xy(200, 200).addTo(this)
    renderEasing(Easing.LINEAR).xy(300, 200).addTo(this)
    renderEasing(Easing.EASE_OUT).xy(400, 200).addTo(this)
}
