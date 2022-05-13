package com.soywiz.korge.view

import com.soywiz.kgl.KmlGl
import com.soywiz.kgl.KmlGlProxyLogToString
import com.soywiz.korag.AG
import com.soywiz.korag.gl.AGOpengl
import com.soywiz.korge.render.RenderContext
import com.soywiz.korge.test.assertEqualsFileReference
import com.soywiz.korge.tests.ViewsForTesting
import com.soywiz.korge.view.filter.IdentityFilter
import com.soywiz.korim.bitmap.NativeImage
import com.soywiz.korim.color.Colors
import org.junit.Test

class ViewsOpenglJvmTest : ViewsForTesting(log = true) {
    val logGl: KmlGlProxyLogToString = object : KmlGlProxyLogToString() {
        override fun getString(name: String, params: List<Any?>, result: Any?): String? = when (name) {
            "shaderSource" -> super.getString(name, params.dropLast(1) + listOf("..."), result)
            else -> super.getString(name, params, result)
        }
    }

    override fun createAg(): AG = object : AGOpengl() {
        override val gl: KmlGl = logGl
        override val nativeComponent: Any = Unit
    }

    // This checks that the texture is generated with the default size (dirty=true fix)
    @Test
    fun testIdentityFilterFor128x128() {
        views.stage += Image(NativeImage(AG.RenderBufferConsts.DEFAULT_INITIAL_WIDTH, AG.RenderBufferConsts.DEFAULT_INITIAL_HEIGHT)).also {
            it.filter = IdentityFilter
        }
        views.render()
        assertEqualsFileReference("korge/render/ViewsJvmTestOpenglFilterIdentityDefaultRenderBufferSize.log", logGl.getLogAsString())
    }

    @Test
    fun testRenderToTextureWithStencil() {
        views.stage += object : View() {
            override fun renderInternal(ctx: RenderContext) {
                ctx.renderToTexture(100, 100, render = {
                   ctx.useCtx2d { ctx2d ->
                       ctx2d.rect(0.0, 0.0, 100.0, 100.0, Colors.RED)
                   }
                }, hasStencil = true) { tex ->
                    ctx.useBatcher { batcher ->
                        batcher.drawQuad(tex)
                    }
                }
                ctx.renderToTexture(100, 100, render = {
                    ctx.useCtx2d { ctx2d ->
                        ctx2d.rect(0.0, 0.0, 100.0, 100.0, Colors.BLUE)
                    }
                }, hasDepth = true, hasStencil = true) { tex ->
                    ctx.useBatcher { batcher ->
                        batcher.drawQuad(tex, 100f, 0f)
                    }
                }
            }
        }
        views.render()
        assertEqualsFileReference("korge/render/ViewsJvmTestOpenglRenderToTextureWithStencil.log", logGl.getLogAsString())
    }

}
