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

private val RenderContext.xBuffer by Extra.PropertyThis<RenderContext, AG.Buffer> { ag.createVertexBuffer() }
private val RenderContext.yBuffer by Extra.PropertyThis<RenderContext, AG.Buffer> { ag.createVertexBuffer() }
private val RenderContext.colBuffer by Extra.PropertyThis<RenderContext, AG.Buffer> { ag.createVertexBuffer() }
private val RenderContext.idBuffer by Extra.PropertyThis<RenderContext, AG.Buffer> { ag.createVertexBuffer() }

class MyView : View() {
    var indexBuffer: AG.Buffer? = null
    var tex: AG.Texture? = null
    private val textureUnit = AG.TextureUnit(null, linear = false)

    companion object {
        val v_color = Varying("v_color", VarType.Float4)
        val a_x = Attribute("a_x", VarType.Float1, false)
        val a_y = Attribute("a_y", VarType.Float1, false)
        val a_col = Attribute("aa_col", VarType.Float4, false).withDivisor(1)
        val a_id = Attribute("a_id", VarType.Float1, false).withDivisor(1)
        val xLayout = VertexLayout(listOf(a_x))
        val yLayout = VertexLayout(listOf(a_y))
        val colLayout = VertexLayout(listOf(a_col))
        val idLayout = VertexLayout(listOf(a_id))

        val vprogram = Program(VertexShader {
            val id = a_id
            //val id = 0.lit
            DefaultShaders.apply {
                //SET(out, (u_ProjMat * u_ViewMat) * vec4(vec2(a_x, a_y), 0f.lit, 1f.lit))
                //SET(v_color, texture2D(u_Tex, vec2(vec1(id) / 4f.lit, 0f.lit)))
                SET(v_color, a_col)
                SET(out, vec4(vec2(a_x + vec1(id) * 0.1f.lit, a_y), 0f.lit, 1f.lit))
            }
        }, FragmentShader {
            //SET(out, texture2D(DefaultShaders.u_Tex, DefaultShaders.v_Tex["xy"]))
            SET(out, v_color)
            //SET(out, vec4(1f.lit, 0f.lit, 1f.lit, 1f.lit))
        })
    }

    override fun renderInternal(ctx: RenderContext) {
        if (tex == null) {
            println("ctx.ag.isInstanceIDSupported=${ctx.ag.isInstanceIDSupported}")
            println("ctx.ag.isFloatTextureSupported=${ctx.ag.isFloatTextureSupported}")
            tex = ctx.ag.createTexture()
            indexBuffer = ctx.ag.createIndexBuffer()
            indexBuffer!!.upload(shortArrayOf(0, 1, 1, 2, 2, 3, 3, 0))
            tex!!.upload(FloatBitmap32(4, 1, floatArrayOf(
                1f, 0f, 1f, 1f,
                1f, 1f, 0f, 1f,
                1f, 0f, 0f, 1f,
                0f, 1f, 1f, 1f,
            )))
            //tex!!.upload(Bitmap32(1, 1) { x, y -> Colors.FUCHSIA })
        }
        ctx.xBuffer.upload(floatArrayOf(0f, .9f, 0f, .9f))
        ctx.yBuffer.upload(floatArrayOf(
            0f, .3f, .9f, .9f,
            0f, -.3f, -.9f, -.9f,
        ))
        ctx.colBuffer.upload(floatArrayOf(
            1f, 0f, 1f, 1f,
            1f, 1f, 0f, 1f,
            1f, 0f, 0f, 1f,
            0f, 1f, 1f, 1f,
        ))
        ctx.idBuffer.upload(listOf(0, 1, 2, 3).map { it.toFloat() }.toFloatArray())
        textureUnit.texture = tex
        ctx.flush()
        ctx.ag.drawV2(
            vertexData = listOf(
                AG.VertexData(ctx.xBuffer, xLayout),
                AG.VertexData(ctx.yBuffer, yLayout),
                AG.VertexData(ctx.colBuffer, colLayout),
                AG.VertexData(ctx.idBuffer, idLayout),
            ),
            program = vprogram,
            type = AG.DrawType.LINES,
            vertexCount = 8,
            indices = indexBuffer,
            //instances = 10,
            instances = 4,
            //instances = 3,
            uniforms = AG.UniformValues(
                DefaultShaders.u_Tex to textureUnit
            )
        )
    }
}
