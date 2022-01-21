import com.soywiz.klock.*
import com.soywiz.korge.Korge
import com.soywiz.korge.scene.MaskTransition
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.scene.sceneContainer
import com.soywiz.korge.view.*
import com.soywiz.korge.view.filter.TransitionFilter
import com.soywiz.korim.atlas.MutableAtlasUnit
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.*
import com.soywiz.korio.file.std.resourcesVfs
import ecs.entitysystem.EntitySystem
import ecs.entitytypes.SpawnerEntity
import ecs.subsystems.ImageAnimationSystem
import ecs.subsystems.MovingSystem
import ecs.subsystems.SpawningSystem

import com.github.quillraven.fleks.World

const val scaleFactor = 1

suspend fun main() = Korge(width = 384 * scaleFactor, height = 216 * scaleFactor, bgcolor = Colors["#000000"]) {

    injector.mapPrototype { ExampleScene() }

    val rootSceneContainer = sceneContainer()
    views.debugViews = true

    rootSceneContainer.changeTo<ExampleScene>(
        transition = MaskTransition(transition = TransitionFilter.Transition.CIRCULAR, reversed = false, smooth = true),
        time = 0.5.seconds
    )
}

var aseImage: ImageData? = null

class ExampleScene : Scene() {

    private val atlas = MutableAtlasUnit(1024, 1024)
    private val entitySystem: EntitySystem = EntitySystem()

    override suspend fun Container.sceneInit() {
        val sw = Stopwatch().start()
        aseImage = resourcesVfs["sprites2.ase"].readImageData(ASE, atlas = atlas)
        println("loaded resources in ${sw.elapsed}")
    }

    override suspend fun Container.sceneMain() {
        container {
            scale(scaleFactor)


        }
    }
}
