import com.soywiz.klock.seconds
import com.soywiz.korge.time.delayFrame
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.Stage
import com.soywiz.korge.view.addFilter
import com.soywiz.korge.view.container
import com.soywiz.korge.view.filter.BlurFilter
import com.soywiz.korge.view.filter.Convolute3Filter
import com.soywiz.korge.view.filter.PageFilter
import com.soywiz.korge.view.filter.TransitionFilter
import com.soywiz.korge.view.filter.WaveFilter
import com.soywiz.korge.view.image
import com.soywiz.korge.view.position
import com.soywiz.korge.view.renderToBitmap
import com.soywiz.korge.view.scale
import com.soywiz.korge.view.xy
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korio.lang.portableSimpleName

suspend fun Stage.mainFiltersSample() {
    /*
    val container = container {
        //solidRect(512, 512, Colors.RED)
        image(Bitmap32(512, 512, Colors.RED))
            .filters(SwizzleColorsFilter("rgba"))
            .filters(ColorMatrixFilter(ColorMatrixFilter.GRAYSCALE_MATRIX))
    }
    //delayFrame()
    val bitmap = container.renderToBitmap(views)
    bitmap.writeTo(localVfs("/tmp/image.png"), PNG)
    image(bitmap).xy(800, 0)
    */

    val cc = container {
        FiltersE2ETestCase.runBase(this)
    }
    delayFrame()
    val bitmap = cc.renderToBitmap(views)
    //removeChildren()
    image(bitmap).xy(768, 0)
    //image(bitmap).xy(768, 0).scale(0.5, 0.5)
    //image(bitmap)
}

open class E2ETestCase {
    val name get() = this::class.portableSimpleName

    open suspend fun runBase(stage: Container) {
        stage.run()
    }

    open suspend fun Container.run() {
    }
}

object FiltersE2ETestCase : E2ETestCase() {
    override suspend fun Container.run() {
        println("LOADING IMAGE...")
        val bitmap = resourcesVfs["korge.png"].readBitmap().toBMP32().premultiplied()
        println("PREPARING VIEWS...")
        image(bitmap).scale(.5).position(0, 0).addFilter(WaveFilter(time = 0.5.seconds))
        image(bitmap).scale(.5).position(256, 0).addFilter(BlurFilter(radius = 6.0))
        //image(bitmap).scale(.5).position(256, 0).addFilter(BlurFilter(initialRadius = 4.0))
        image(bitmap).scale(.5).position(512, 0).addFilter(TransitionFilter(TransitionFilter.Transition.SWEEP, reversed = false, smooth = true, ratio = 0.5))
        image(bitmap).scale(.5).position(0, 256).addFilter(PageFilter(hratio = 0.5, hamplitude1 = 20.0))
        image(bitmap).scale(.5).position(256, 256).addFilter(Convolute3Filter(Convolute3Filter.KERNEL_SHARPEN))
        //image(bitmap).scale(.5).position(512, 256).filters(SwizzleColorsFilter("bgga"), ColorMatrixFilter(ColorMatrixFilter.GRAYSCALE_MATRIX))
        //image(bitmap).scale(.5).position(512, 256).filters(SwizzleColorsFilter("bgga"), ColorMatrixFilter(ColorMatrixFilter.GRAYSCALE_MATRIX))
        /*
        image(bitmap)
            .filters(SwizzleColorsFilter("bgga"))
            .filters(ColorMatrixFilter(ColorMatrixFilter.GRAYSCALE_MATRIX))

         */

        //image(Image(bitmap).scale(.5).filters(SwizzleColorsFilter("bgga"), ColorMatrixFilter(ColorMatrixFilter.GRAYSCALE_MATRIX)).renderToBitmap(stage!!.views))
        println("VIEWS PREPARED")
    }
}

