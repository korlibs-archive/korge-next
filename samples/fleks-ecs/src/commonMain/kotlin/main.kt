import com.soywiz.korge.Korge
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.scene.sceneContainer
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.container
import com.soywiz.korge.view.addUpdater
import com.soywiz.korim.atlas.MutableAtlasUnit
import com.soywiz.korim.color.Colors
import com.soywiz.klock.Stopwatch
import com.soywiz.korim.format.ASE
import com.soywiz.korim.format.ImageData
import com.soywiz.korim.format.readImageData
import com.soywiz.korio.file.std.resourcesVfs

import com.github.quillraven.fleks.*
import systems.*
import systems.SpriteSystem.SpriteListener
import components.*

const val scaleFactor = 2

suspend fun main() = Korge(width = 384 * scaleFactor, height = 216 * scaleFactor, bgcolor = Colors["#000000"]) {

    injector.mapPrototype { ExampleScene() }

    val rootSceneContainer = sceneContainer()
    views.debugViews = true

    rootSceneContainer.changeTo<ExampleScene>()
}

var aseImage: ImageData? = null

class ExampleScene : Scene() {

    private val atlas = MutableAtlasUnit(1024, 1024)

    override suspend fun Container.sceneInit() {
        val sw = Stopwatch().start()
        aseImage = resourcesVfs["sprites.ase"].readImageData(ASE, atlas = atlas)
        println("loaded resources in ${sw.elapsed}")
    }

    override suspend fun Container.sceneMain() {
        container {
            scale = scaleFactor.toDouble()


//        val dummyInMoveSystem = MoveSystem.MyClass(text = "Hello injector!")

            // TODO build a views container for handling layers for the ImageAnimationSystem of Fleks ECS
            val layerContainer = container()

            // This is the world object of the entity component system (ECS)
            // It contains all ECS related configuration
            val world = World {
                entityCapacity = 512

                // Register all needed systems
                system(::MoveSystem)
                system(::SpawnerSystem)
                system(::SpriteSystem)

                // Register all needed components and its listeners (if needed)
                component(::Position)
                component(::Sprite, ::SpriteListener)
                component(::Spawner)

                // Register external objects which are used by systems and component listeners
                inject(layerContainer)
            }

            val spawner = world.entity {
                add<Position> {  // Position of spawner
                    x = 130.0
                    y = 100.0
                }
                add<Spawner> {  // Config for spawner object
                    numberOfObjects = 1  // which will be created at once
                    interval = 30  // every 30 frames
                    timeVariation = 0
                    positionVariationX = 50.0
                    positionVariationY = 0.0
                    positionAccelerationX = 40.0
                    positionAccelerationY = 50.0
//                    spriteImageData = "sprite"
//                    spriteAnimation = "FireTrail"  // "FireTrail" - "TestNum"
//                    spriteIsPlaying = true
                }
            }

            addUpdater { dt ->
                world.update(dt.seconds.toFloat())
            }
        }
    }
}
