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

const val scaleFactor = 3

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

            // TODO build a views container for handling layers for the ImageAnimationSystem of Fleks ECS
            val layer0 = container()
            val layer1 = container()

            // This is the world object of the entity component system (ECS)
            // It contains all ECS related system and component configuration
            val world = World {
                entityCapacity = 512

                // Register all needed systems
                system(::MoveSystem)
                system(::SpawnerSystem)
                system(::SpriteSystem)
                system(::CollisionSystem)
                system(::DestructSystem)

                // Register all needed components and its listeners (if needed)
                component(::Position)
                component(::Sprite, ::SpriteListener)
                component(::Spawner)
                component(::Destruct)
                component(::Rigidbody)
                component(::Impulse)

                // Register external objects which are used by systems and component listeners
                inject("layer0", layer0)
//              inject("layer1", layer1)  TODO add more layers for explosion objects to be on top
            }

            // This is the config for the spawner entity which sits on top of the screen and which
            // spawns the meteorite objects.
            // - The spawner get a "Position" component which set the position of it 10 pixels
            //   above the visible area.
            // - Secondly it gets a "Spawner" component. That tells the system that the spawned
            //   meteorite objects itself are spawning objects. These are the visible fire trails.
            world.entity {
                add<Position> {  // Position of spawner
                    x = 100.0
                    y = -10.0
                }
                add<Spawner> {  // Config for spawner object
                    numberOfObjects = 1  // The spawner will generate one object per second
                    interval = 60        // every 60 frames which means once per second
                    timeVariation = 0
                    // Spawner details for spawned objects (spawned objects do also spawn objects itself)
                    spawnerNumberOfObjects = 5 // Enable spawning feature for spawned object
                    spawnerInterval = 1
                    spawnerPositionVariationX = 5.0
                    spawnerPositionVariationY = 5.0
                    spawnerPositionAccelerationX = -30.0
                    spawnerPositionAccelerationY = -100.0
                    spawnerPositionAccelerationVariation = 15.0
                    spawnerSpriteImageData = "sprite"  // "" - Disable sprite graphic for spawned object
                    spawnerSpriteAnimation = "FireTrail"  // "FireTrail" - "TestNum"
                    spawnerSpriteIsPlaying = true
                    // Set position details for spawned objects
                    positionVariationX = 100.0
                    positionVariationY = 0.0
                    positionAccelerationX = 90.0
                    positionAccelerationY = 200.0
                    positionAccelerationVariation = 10.0
                    // Destruct info for spawned objects
                    destruct = true
                }
            }

            addUpdater { dt ->
                world.update(dt.seconds.toFloat())
            }
        }
    }
}
