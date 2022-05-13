package com.soywiz.korge.view

import com.soywiz.kgl.KmlGlProxyLogToString
import com.soywiz.korag.AG
import com.soywiz.korag.gl.SimpleAGOpengl
import com.soywiz.korge.test.assertEqualsFileReference
import com.soywiz.korge.tests.ViewsForTesting
import com.soywiz.korge.view.vector.gpuShapeView
import com.soywiz.korim.bitmap.mipmaps
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmapOptimized
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.vector.LineCap
import com.soywiz.korma.geom.vector.LineJoin
import com.soywiz.korma.geom.vector.rect
import org.junit.Test

class ReferenceOpenglTest : ViewsForTesting() {
    val gl = KmlGlProxyLogToString()
    override fun createAg(): AG = SimpleAGOpengl(gl)

    @Test
    fun testOpengl() = viewsTest {
        image(resourcesVfs["texture.png"].readBitmapOptimized().mipmaps())
        gl.clearLog()
        render(views.renderContext)
        assertEqualsFileReference("korge/render/OpenGL.log", gl.getLogAsString())
    }

    @Test
    fun testOpenglShapeView() = viewsTest {
        container {
            xy(300, 300)
            val shape = gpuShapeView({
                //val lineWidth = 6.12123231 * 2
                val lineWidth = 12.0
                val width = 300.0
                val height = 300.0
                //rotation = 180.degrees
                this.stroke(Colors.WHITE.withAd(0.5), lineWidth = lineWidth, lineJoin = LineJoin.MITER, lineCap = LineCap.BUTT) {
                    this.rect(
                        lineWidth / 2, lineWidth / 2,
                        width,
                        height
                    )
                }
            }) {
                xy(-150, -150)
            }
        }
        gl.clearLog()
        render(views.renderContext)
        assertEqualsFileReference("korge/render/OpenGLShapeView.log", gl.getLogAsString())
    }
}
