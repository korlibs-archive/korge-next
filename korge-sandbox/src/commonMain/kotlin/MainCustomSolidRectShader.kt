import com.soywiz.klock.seconds
import com.soywiz.korag.DefaultShaders
import com.soywiz.korag.shader.Uniform
import com.soywiz.korag.shader.VarType
import com.soywiz.korag.shader.appendingVertex
import com.soywiz.korag.shader.replacingFragment
import com.soywiz.korge.view.Stage
import com.soywiz.korge.view.addUpdater
import com.soywiz.korge.view.getDefaultProgram
import com.soywiz.korge.view.solidRect
import com.soywiz.korge.view.xy
import com.soywiz.korim.color.Colors

suspend fun Stage.mainCustomSolidRectShader() {
    val solidRect = solidRect(200, 200, Colors.RED).xy(100, 100)
    val timeUniform = Uniform("u_time", VarType.Float1)

    var time = 0.seconds
    addUpdater {
        solidRect.programUniforms[timeUniform] = time.seconds.toFloat()
        time += it
    }

    solidRect.program = views.getDefaultProgram()
        .replacingFragment("color") {
            DefaultShaders {
                SET(out, vec4(1f.lit, v_Tex.x, v_Tex.y, 1f.lit))
            }
        }
        .appendingVertex("moving") {
            SET(out.x, out.x + (sin(timeUniform * 2f.lit) * .1f.lit))
        }
}
