import com.soywiz.kds.*
import com.soywiz.klock.*
import com.soywiz.kmem.*
import com.soywiz.korag.*
import com.soywiz.korag.shader.*
import com.soywiz.korge.*
import com.soywiz.korge.render.*
//import com.soywiz.korge.component.length.bindLength
import com.soywiz.korge.resources.*
import com.soywiz.korge.tween.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korio.resources.*
import com.soywiz.korio.util.*
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
    private val sprites = FSpriteContainer(800_000)
    private val random = Random(0)

    init {
        val anchorRaw = FSprite.packAnchor(.2f, .2f)
        println(anchorRaw.toStringUnsigned(16))
        sprites.apply {
            for (n in 0 until sprites.maxSize) {
            //for (n in 0 until 1_000) {
            //for (n in 0 until 200_000) {
                val sprite = alloc()
                //println(sprite.offset)
                sprite.x = 0f
                sprite.y = 0f
                sprite.width = 1f
                sprite.height = 1f
                sprite.angle = 0.radians
                sprite.anchorRaw = anchorRaw
                sprite.uv0Raw = 0
                sprite.uv1Raw = 0
            }
        }

        addUpdater {
            sprites.fastForEach {
                it.x = random.nextInt(512).toFloat()
                it.y = random.nextInt(512).toFloat()
            }
        }
    }

    override fun renderInternal(ctx: RenderContext) {
        FSprite.run {
            ctx.xyBuffer.buffer.upload(
                floatArrayOf(
                    0f, 0f,
                    1f, 0f,
                    1f, 1f,
                    0f, 1f,
                )
            )
        }
        sprites.uploadVertices(ctx)
        ctx.flush()
        ctx.ag.drawV2(
            vertexData = FSprite.run { ctx.buffers },
            program = FSprite.vprogram,
            type = AG.DrawType.TRIANGLE_FAN,
            vertexCount = 4,
            instances = sprites.size,
            uniforms = ctx.batch.uniforms
        )
        ctx.batch.onInstanceCount(sprites.size)
    }
}

open class FSpriteContainer(val maxSize: Int) {
    var size = 0
    val data = FBuffer(maxSize * FSprite.STRIDE * 4)
    private val i32 = data.i32
    private val f32 = data.f32
    fun uploadVertices(ctx: RenderContext) {
        FSprite.apply {
            ctx.fastSpriteBuffer.buffer.upload(data, 0, size * STRIDE * 4)
        }
    }

    fun alloc() = FSprite(size++ * FSprite.STRIDE)

    var FSprite.x: Float get() = f32[offset + 0] ; set(value) { f32[offset + 0] = value }
    var FSprite.y: Float get() = f32[offset + 1] ; set(value) { f32[offset + 1] = value }
    var FSprite.width: Float get() = f32[offset + 2] ; set(value) { f32[offset + 2] = value }
    var FSprite.height: Float get() = f32[offset + 3] ; set(value) { f32[offset + 3] = value }
    var FSprite.radiansf: Float get() = f32[offset + 4] ; set(value) { f32[offset + 4] = value }
    var FSprite.anchorRaw: Int get() = i32[offset + 5] ; set(value) { i32[offset + 5] = value }
    var FSprite.uv0Raw: Int get() = i32[offset + 6] ; set(value) { i32[offset + 6] = value }
    var FSprite.uv1Raw: Int get() = i32[offset + 7] ; set(value) { i32[offset + 7] = value }

    var FSprite.angle: Angle get() = radiansf.radians ; set(value) { radiansf = value.radians.toFloat() }

    fun FSprite.setAnchor(x: Float, y: Float) {
        anchorRaw = FSprite.packAnchor(x, y)
    }

    inline fun fastForEach(callback: FSpriteContainer.(sprite: FSprite) -> Unit) {
        var m = 0
        for (n in 0 until size) {
            callback(FSprite(m))
            m += FSprite.STRIDE
        }
    }
}

inline class FSprite(val offset: Int) {
    companion object {
        const val STRIDE = 8

        val v_color = Varying("v_color", VarType.Float4)
        val a_x = Attribute("a_x", VarType.Float1, false)
        val a_y = Attribute("a_y", VarType.Float1, false)

        val a_pos = Attribute("a_rxy", VarType.Float2, false).withDivisor(1)
        val a_anchor = Attribute("a_axy", VarType.SShort2, true).withDivisor(1)
        val a_size = Attribute("a_size", VarType.Float2, true).withDivisor(1)
        val a_angle = Attribute("a_rangle", VarType.Float1, false).withDivisor(1)
        val a_uv0 = Attribute("a_uv0", VarType.UShort2, true).withDivisor(1)
        val a_uv1 = Attribute("a_uv1", VarType.UShort2, true).withDivisor(1)

        val RenderContext.xyBuffer by Extra.PropertyThis<RenderContext, AG.VertexData> {
            ag.createVertexData(a_x, a_y)
        }
        val RenderContext.fastSpriteBuffer by Extra.PropertyThis<RenderContext, AG.VertexData> {
            ag.createVertexData(a_pos, a_size, a_angle, a_anchor, a_uv0, a_uv1)
        }
        val RenderContext.buffers by Extra.PropertyThis<RenderContext, List<AG.VertexData>> {
            listOf(xyBuffer, fastSpriteBuffer)
        }

        val vprogram = Program(VertexShader {
            DefaultShaders.apply {
                FSprite.apply {
                    //SET(out, (u_ProjMat * u_ViewMat) * vec4(vec2(a_x, a_y), 0f.lit, 1f.lit))
                    //SET(v_color, texture2D(u_Tex, vec2(vec1(id) / 4f.lit, 0f.lit)))
                    SET(v_color, vec4(1f.lit, 0f.lit, 0f.lit, 1f.lit))
                    val cos = t_Temp0["y"]
                    val sin = t_Temp0["z"]
                    SET(cos, cos(a_angle))
                    SET(sin, sin(a_angle))
                    SET(t_TempMat2, mat2(
                        cos, -sin,
                        sin, cos,
                    ))
                    SET(t_Temp0["xy"], t_TempMat2 * vec2(((a_x - a_anchor.x) * a_size.x), ((a_y - a_anchor.y) * a_size.y)))
                    SET(out, (u_ProjMat * u_ViewMat) * vec4(t_Temp0["xy"] + vec2(a_pos.x, a_pos.y), 0f.lit, 1f.lit))
                }
            }
        }, FragmentShader {
            DefaultShaders.apply {
                FSprite.apply {
                    //SET(out, texture2D(DefaultShaders.u_Tex, DefaultShaders.v_Tex["xy"]))
                    SET(out, v_color)
                    //SET(out, vec4(1f.lit, 0f.lit, 1f.lit, 1f.lit))
                }
            }
        })

        private fun packAnchorComponent(v: Float): Int {
            return (((v + 1f) * .5f) * 0xFFFF).toInt() and 0xFFFF
        }

        fun packAnchor(x: Float, y: Float): Int {
            return (packAnchorComponent(x) and 0xFFFF) or (packAnchorComponent(y) shl 16)
        }
    }
    val index get() = offset / STRIDE
    //val offset get() = index * STRIDE
}

/*
class FSprite {
    var x: Float = 0f
    var y: Float = 0f
    var width: Float = .5f
    var height: Float = .5f
    var radiansf: Float = 0f
    var anchor: Int = 0x7fff7fff
    var uv0: Int = 0
    var uv1: Int = 0
}
*/
