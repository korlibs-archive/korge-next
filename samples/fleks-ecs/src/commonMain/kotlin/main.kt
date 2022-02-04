import com.soywiz.korge.Korge
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.scene.sceneContainer
import com.soywiz.korge.view.*
import com.soywiz.korim.atlas.MutableAtlasUnit
import com.soywiz.korim.color.Colors
import com.github.quillraven.fleks.*
import systems.*
import components.*

const val scaleFactor = 2

suspend fun main() = Korge(width = 384 * scaleFactor, height = 216 * scaleFactor, bgcolor = Colors["#000000"]) {

    injector.mapPrototype { ExampleScene() }

    val rootSceneContainer = sceneContainer()
    views.debugViews = true

    rootSceneContainer.changeTo<ExampleScene>()
}

class ExampleScene : Scene() {

    private val atlas = MutableAtlasUnit(1024, 1024)

    override suspend fun Container.sceneInit() {
    }

    override suspend fun Container.sceneMain() {

//        val dummyInMoveSystem = MoveSystem.MyClass(text = "Hello injector!")

        // This is the world object of the entity component system (ECS)
        // It contains all ECS related configuration
        val world = World {
            entityCapacity = 20

            // Register all needed systems
            system(::MoveSystem)
            system(::SpawnerSystem)
            system(::ImageAnimationSystem)

            // Register all needed components and its listeners (if needed)
            component(::Position, ::PositionListener)
            component(::ImageAnimation, ::ImageAnimationListener)
            component(::Spawner)

            // Register external objects which are used by systems and component listeners
//            inject(dummyInMoveSystem)
        }

        val spawner = world.entity {
            add<Position> {  // Position of spawner
                x = 130.0
                y = 100.0
            }
            add<Spawner> {  // Config for spawner object
                numberOfObjects = 7
                interval = 1
                timeVariation = 0
                xPosVariation = 50.0
                yPosVariation = 7.0
                xAccel = -0.8
                yAccel = -1.0
            }
            add<ImageAnimation> {  // Config for spawner object
                imageData = "sprite2"
                animation = "FireTrail"  // "FireTrail" - "TestNum"
                isPlaying = true
            }
        }
/*
                // Initialize entity data and config
                entity.spawnerComponent.numberOfObjects = 7
                entity.positionComponent.x = 130.0
                entity.positionComponent.y = 100.0
                entity.spawnerComponent.interval = 1
                entity.spawnerComponent.timeVariation = 0
                entity.spawnerComponent.xPosVariation = 50.0
                entity.spawnerComponent.yPosVariation = 7.0
                entity.spawnerComponent.xAccel = -0.8
                entity.spawnerComponent.yAccel = -1.0

                entity.imageAnimationComponent.imageData = "sprite2"
                entity.imageAnimationComponent.animation = "FireTrail"  // "FireTrail" - "TestNum"
                entity.imageAnimationComponent.isPlaying = true
*/

        addUpdater { dt ->
            world.update(dt.milliseconds.toFloat())
        }
    }
}
