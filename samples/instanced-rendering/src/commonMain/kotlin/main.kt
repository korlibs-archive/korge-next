import com.soywiz.kds.*
import com.soywiz.klock.*
import com.soywiz.korag.*
import com.soywiz.korag.shader.*
import com.soywiz.korge.*
import com.soywiz.korge.render.*
//import com.soywiz.korge.component.length.bindLength
import com.soywiz.korge.resources.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.*
import com.soywiz.korio.resources.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.interpolation.*
import kotlin.random.*

// @TODO: We could autogenerate this via gradle
val ResourcesContainer.korge_png by resourceBitmap("korge.png")

suspend fun main() {
    //GLOBAL_CHECK_GL = true
    Korge(width = 512, height = 512, bgcolor = Colors["#2b2b2b"], clipBorders = false) {
        gameWindow.icon = korge_png.get().bmp.toBMP32().scaled(32, 32)

        val minDegrees = (-16).degrees
        val maxDegrees = (+16).degrees

        val image = image(korge_png) {
            //val image = image(resourcesVfs["korge.png"].readbitmapslice) {
            rotation = maxDegrees
            anchor(.5, .5)
            scale(.8)
            position(256, 256)
        }

        addChild(MyView())

        //bindLength(image::scaledWidth) { 100.vw }
        //bindLength(image::scaledHeight) { 100.vh }

        while (true) {
            image.tween(image::rotation[minDegrees], time = 1.seconds, easing = Easing.EASE_IN_OUT)
            image.tween(image::rotation[maxDegrees], time = 1.seconds, easing = Easing.EASE_IN_OUT)
        }
    }
}

class MyView : View() {
    var indexBuffer: AG.Buffer? = null
    private val textureUnit = AG.TextureUnit(null, linear = false)

    companion object {
        val v_color = Varying("v_color", VarType.Float4)
        val a_x = Attribute("a_x", VarType.Float1, false)
        val a_y = Attribute("a_y", VarType.Float1, false)

        val a_rx = Attribute("a_rx", VarType.Float1, false).withDivisor(1)
        val a_ry = Attribute("a_ry", VarType.Float1, false).withDivisor(1)
        val a_ax = Attribute("a_ax", VarType.Float1, false).withDivisor(1)
        val a_ay = Attribute("a_ay", VarType.Float1, false).withDivisor(1)
        val a_w = Attribute("a_w", VarType.Float1, false).withDivisor(1)
        val a_h = Attribute("a_h", VarType.Float1, false).withDivisor(1)
        val a_rangle = Attribute("a_rangle", VarType.Float1, false).withDivisor(1)

        private val RenderContext.xyBuffer by Extra.PropertyThis<RenderContext, AG.VertexData> { ag.createVertexData(a_x, a_y) }
        private val RenderContext.fastSpriteBuffer by Extra.PropertyThis<RenderContext, AG.VertexData> { ag.createVertexData(a_rx, a_ry, a_ax, a_ay, a_w, a_h, a_rangle) }

        val vprogram = Program(VertexShader {
            DefaultShaders.apply {
                //SET(out, (u_ProjMat * u_ViewMat) * vec4(vec2(a_x, a_y), 0f.lit, 1f.lit))
                //SET(v_color, texture2D(u_Tex, vec2(vec1(id) / 4f.lit, 0f.lit)))
                SET(v_color, vec4(1f.lit, 0f.lit, 0f.lit, 1f.lit))
                val cos = t_Temp0["y"]
                val sin = t_Temp0["z"]
                SET(cos, cos(a_rangle))
                SET(sin, sin(a_rangle))
                SET(t_TempMat2, mat2(
                    cos, -sin,
                    sin, cos,
                ))
                SET(t_Temp0["xy"], t_TempMat2 * vec2(((a_x - a_ax) * a_w), ((a_y - a_ay) * a_h)))
                SET(out, (u_ProjMat * u_ViewMat) * vec4(t_Temp0["xy"] + vec2(a_rx, a_ry), 0f.lit, 1f.lit))
            }
        }, FragmentShader {
            //SET(out, texture2D(DefaultShaders.u_Tex, DefaultShaders.v_Tex["xy"]))
            SET(out, v_color)
            //SET(out, vec4(1f.lit, 0f.lit, 1f.lit, 1f.lit))
        })
    }

    private val numInstances = 500_000
    private val STRIDE = 7
    private val data = FloatArray(numInstances * STRIDE)
    private val random = Random

    override fun renderInternal(ctx: RenderContext) {
        if (indexBuffer == null) {
            indexBuffer = ctx.ag.createIndexBuffer()
            indexBuffer!!.upload(shortArrayOf(0, 1, 1, 2, 2, 3, 3, 0))
            //tex!!.upload(Bitmap32(1, 1) { x, y -> Colors.FUCHSIA })
        }
        ctx.xyBuffer.buffer.upload(floatArrayOf(
            0f, 0f,
            1f, 0f,
            1f, 1f,
            0f, 1f,
        ))
        for (n in 0 until numInstances) {
            data[n * STRIDE + 0] = random.nextInt(512).toFloat()
            data[n * STRIDE + 1] = random.nextInt(512).toFloat()
            data[n * STRIDE + 2] = .5f
            data[n * STRIDE + 3] = .5f
            data[n * STRIDE + 4] = .5f
            data[n * STRIDE + 5] = .5f
            data[n * STRIDE + 6] = 0f
            //data[n * STRIDE + 6] = 0.degrees.radians.toFloat()
        }

        ctx.fastSpriteBuffer.buffer.upload(data)
        ctx.flush()
        ctx.ag.drawV2(
            vertexData = listOf(ctx.xyBuffer, ctx.fastSpriteBuffer),
            program = vprogram,
            type = AG.DrawType.LINES,
            vertexCount = 8,
            indices = indexBuffer,
            instances = numInstances,
            uniforms = ctx.batch.uniforms
        )
        ctx.batch.onInstanceCount(numInstances)
    }
}

class FSprite {
    var x: Float = 0f
    var y: Float = 0f
    var rotation: Angle = 0.degrees
    val scaleX: Float = 0f
    val scaleY: Float = 0f

    val tx0: Float = 0f
    val ty0: Float = 0f
    val tx1: Float = 1f
    val ty1: Float = 1f
}
