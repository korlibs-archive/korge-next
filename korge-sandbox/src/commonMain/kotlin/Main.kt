
import com.soywiz.korge.Korge
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.scene.sceneContainer
import com.soywiz.korge.ui.UIDropdownOption
import com.soywiz.korge.ui.UIDropdownSettings
import com.soywiz.korge.ui.uiDropdown
import com.soywiz.korge.view.Stage
import com.soywiz.korge.view.xy
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.mix
import com.soywiz.korio.lang.portableSimpleName
import samples.BezierSample
import samples.Bunnymark
import samples.MainArc
import samples.MainBVH
import samples.MainBezier
import samples.MainBlur
import samples.MainCircleColor
import samples.MainCircles
import samples.MainClipping
import samples.MainColorPicker
import samples.MainColorTransformFilter
import samples.MainCustomSolidRectShader
import samples.MainDpi
import samples.MainDraggable
import samples.MainDragonbones
import samples.MainEasing
import samples.MainEditor
import samples.MainEmoji
import samples.MainExifTest
import samples.MainFilterScale
import samples.MainFilterSwitch
import samples.MainFiltersRenderToBitmap
import samples.MainFiltersSample
import samples.MainGifAnimation
import samples.MainGpuVectorRendering
import samples.MainGpuVectorRendering2
import samples.MainGpuVectorRendering3
import samples.MainHaptic
import samples.MainImageTrace
import samples.MainKorviSample
import samples.MainMasks
import samples.MainMipmaps
import samples.MainMutableAtlasTest
import samples.MainRenderText
import samples.MainRotateCircle
import samples.MainRotatedAtlas
import samples.MainRotatedTexture
import samples.MainSWF
import samples.MainSkybox
import samples.MainSpine
import samples.MainStrokesExperiment
import samples.MainStrokesExperiment2
import samples.MainStrokesExperiment3
import samples.MainSvgAnimation
import samples.MainTextMetrics
import samples.MainTextureIssue
import samples.MainTilemapTest
import samples.MainTransition
import samples.MainTrimmedAtlas
import samples.MainTweenPoint
import samples.MainUIImageTester
import samples.MainUITreeView
import samples.MainVampire
import samples.MainVectorFill
import samples.MainVectorRendering
import samples.MainZIndex
import samples.ParticlesMain
import samples.TerminalEmulatorMain

suspend fun main() = Korge(
    bgcolor = Colors.DARKCYAN.mix(Colors.BLACK, 0.8),
    clipBorders = false,
    //scaleMode = ScaleMode.EXACT,
    //debug = true,
    multithreaded = true,
    //debugAg = true,
) {
    demoSelector(
        Demo(::MainTilemapTest),
        listOf(
            Demo(::MainRotatedAtlas),
            Demo(::MainSWF),
            Demo(::MainSpine),
            Demo(::MainDragonbones),
            Demo(::MainMutableAtlasTest),
            Demo(::TerminalEmulatorMain),
            Demo(::ParticlesMain),
            Demo(::BezierSample),
            Demo(::MainEditor),
            Demo(::Bunnymark),
            Demo(::MainKorviSample),
            Demo(::MainFiltersSample),
            Demo(::MainTextMetrics),
            Demo(::MainRenderText),
            Demo(::MainVectorRendering),
            Demo(::MainFilterScale),
            Demo(::MainExifTest),
            Demo(::MainColorTransformFilter),
            Demo(::MainMipmaps),
            Demo(::MainCustomSolidRectShader),
            Demo(::MainBlur),
            Demo(::MainFiltersRenderToBitmap),
            Demo(::MainColorPicker),
            Demo(::MainMasks),
            Demo(::MainHaptic),
            Demo(::MainSkybox),
            Demo(::MainDraggable),
            Demo(::MainGifAnimation),
            Demo(::MainTransition),
            Demo(::MainTextureIssue),
            Demo(::MainClipping),
            Demo(::MainTweenPoint),
            Demo(::MainEasing),
            Demo(::MainVectorFill),
            Demo(::MainFilterSwitch),
            Demo(::MainSvgAnimation),
            Demo(::MainArc),
            Demo(::MainStrokesExperiment),
            Demo(::MainStrokesExperiment2),
            Demo(::MainStrokesExperiment3),
            Demo(::MainZIndex),
            Demo(::MainDpi),
            Demo(::MainBezier),
            Demo(::MainUITreeView),
            Demo(::MainUIImageTester),
            Demo(::MainVampire),
            Demo(::MainCircles),
            Demo(::MainEmoji),
            Demo(::MainBVH),
            Demo(::MainImageTrace),
            Demo(::MainRotateCircle),
            Demo(::MainTrimmedAtlas),
            Demo(::MainRotatedTexture),
            Demo(::MainCircleColor),
            Demo(::MainGpuVectorRendering),
            Demo(::MainGpuVectorRendering2),
            Demo(::MainGpuVectorRendering3),
        )
    )
}

class Demo(val sceneBuilder: () -> Scene, val name: String = sceneBuilder()::class.portableSimpleName.removePrefix("Main")) {
    override fun toString(): String = name
}

suspend fun Stage.demoSelector(default: Demo, all: List<Demo>) {
    val container = sceneContainer(width = width, height = height - 32.0) { }.xy(0, 32)

    suspend fun setDemo(demo: Demo?) {
        //container.removeChildren()
        if (demo != null) {
            container.changeTo({ injector ->
                demo.sceneBuilder().also { it.init(injector) }
            })
        }
    }

    val toDropdownOptions = (listOf(default) + all).sortedBy { it.name }.map {
        UIDropdownOption.AnyOption(it.name, it)
    }

    val dropdown = uiDropdown(toDropdownOptions, UIDropdownSettings(
        dropdownWidth = 200.0,
        numEntriesVisible = 15
    )).apply {
        onDropdownChange {
            val op = it.newOption as UIDropdownOption.AnyOption
            setDemo(op.data as Demo)
        }
    }

    dropdown.setEntry(default.name)
}
