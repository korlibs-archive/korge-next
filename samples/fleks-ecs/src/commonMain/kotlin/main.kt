import com.soywiz.korge.Korge
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.scene.sceneContainer
import com.soywiz.korge.view.*
import com.soywiz.korim.atlas.MutableAtlasUnit
import com.soywiz.korim.color.Colors
import com.github.quillraven.fleks.*

const val scaleFactor = 1

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

        val dummyInMoveSystem = MoveSystem.MyClass(text = "Hello injector!")

        val world = World {
            entityCapacity = 20

            system(::MoveSystem)
            system(::PositionSystem)

            inject(dummyInMoveSystem)

            // Register all needed components and its listener if needed to the world
            component(::Position, ::PositionListener)
            component(::ImageAnimation)

        }

        val entity = world.entity {
            add<Position> {
                x = 50f
                y = 120f
            }
        }

        addUpdater { dt ->
            world.update(dt.milliseconds.toFloat())
        }
    }
}

class PositionListener : ComponentListener<Position> {
    override fun onComponentAdded(entity: Entity, component: Position) {
        println("Component $component added to $entity!")
    }

    override fun onComponentRemoved(entity: Entity, component: Position) {
        println("Component $component removed from $entity!")
    }
}

data class Position(var x: Float = 0f, var y: Float = 0f)
data class ImageAnimation(var imageData: String = "", var isPlaying: Boolean = false)

class MoveSystem : IntervalSystem(
    interval = Fixed(1000f)  // every second
) {

    class MyClass(val text: String = "")

    private val dummy: MyClass = Inject.dependency()

    override fun onInit() {
    }

    override fun onTick() {
        println("MoveSystem: onTick (text: ${dummy.text})")
    }
}

class PositionSystem : IteratingSystem(
    allOfComponents = arrayOf(Position::class),
    interval = Fixed(500f)  // every 500 millisecond
) {

    private val position: ComponentMapper<Position> = Inject.componentMapper()

    override fun onInit() {
    }

    override fun onTickEntity(entity: Entity) {
        println("PositionSystem: onTickEntity")
        println("pos id: ${position.id} x: ${position[entity].x} y: ${position[entity].y}")
    }
}

