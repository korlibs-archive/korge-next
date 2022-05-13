import com.soywiz.klock.seconds
import com.soywiz.korge.view.FixedSizeContainer
import com.soywiz.korge.view.Stage
import com.soywiz.korge.view.addFilter
import com.soywiz.korge.view.filter.BlurFilter
import com.soywiz.korge.view.filter.Convolute3Filter
import com.soywiz.korge.view.filter.PageFilter
import com.soywiz.korge.view.filter.SwizzleColorsFilter
import com.soywiz.korge.view.filter.TransitionFilter
import com.soywiz.korge.view.filter.WaveFilter
import com.soywiz.korge.view.image
import com.soywiz.korge.view.position
import com.soywiz.korge.view.renderToBitmap
import com.soywiz.korge.view.scale
import com.soywiz.korge.view.xy
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs

suspend fun Stage.mainFiltersRenderToBitmap() {
    println("LOADING IMAGE...")
    val bitmap = resourcesVfs["korge.png"].readBitmap()
    val container = FixedSizeContainer(width, height).apply {
        //scale(2.0, 2.0)
        println("PREPARING VIEWS...")
        image(bitmap).scale(.5).position(0, 0).addFilter(WaveFilter(time = 0.5.seconds))
        //image(bitmap).scale(.5).position(256, 0).addFilter(DirectionalBlurFilter(radius = 32.0))
        image(bitmap).scale(.5).position(256, 0).addFilter(BlurFilter(radius = 32.0))
        image(bitmap).scale(.5).position(512, 0).addFilter(TransitionFilter(TransitionFilter.Transition.SWEEP, reversed = false, smooth = true, ratio = 0.5))
        image(bitmap).scale(.5).position(0, 256).addFilter(PageFilter(hratio = 0.5, hamplitude1 = 20.0))
        image(bitmap).scale(.5).position(256, 256).addFilter(Convolute3Filter(Convolute3Filter.KERNEL_SHARPEN))
        image(bitmap).scale(.5).position(512, 256).addFilter(SwizzleColorsFilter("bgga"))
        println("VIEWS PREPARED")
    }

    //image(stage.renderToBitmap(views)).scale(0.4).xy(800, 50)
    addChild(container)
    image(container.renderToBitmap(views)).scale(1.0).xy(50, 50)
    //container.visible = false
}
