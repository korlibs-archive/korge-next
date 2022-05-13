package com.soywiz.korge.view

import com.soywiz.korge.test.assertEqualsFileReference
import com.soywiz.korge.tests.ViewsForTesting
import com.soywiz.korge.view.filter.ColorMatrixFilter
import com.soywiz.korge.view.filter.SwizzleColorsFilter
import com.soywiz.korim.bitmap.Bitmap32
import com.soywiz.korim.color.Colors
import kotlin.test.Test
import kotlin.test.assertEquals

class ViewsRetinaFilterTest : ViewsForTesting(
    defaultDevicePixelRatio = 2.0,
    log = true,
) {
    @Test
    fun test() = viewsTest {
        stage.scale = 2.0 // @TODO: Should this be like that already by being defaultDevicePixelRatio = 2.0
        assertEquals(2.0, stage.scale, absoluteTolerance = 0.01)
        //println("stage.scale=${stage.scale}")
        //stage.scale = 2.0
        val container = container {
            image(Bitmap32(512, 512, Colors.RED))
            //solidRect(512, 512, Colors.RED)
                .filters(SwizzleColorsFilter("rrra"))
                .filters(ColorMatrixFilter(ColorMatrixFilter.GRAYSCALE_MATRIX))
        }
        delayFrame()
        container.renderToBitmap(views)
        logAg.getLogAsString()
        assertEqualsFileReference(
            "korge/render/ViewFilterRetina.log",
            listOf(
                logAg.getLogAsString(),
            ).joinToString("\n")
        )
    }
}
