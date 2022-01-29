import com.soywiz.korge.Korge
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.scene.sceneContainer
import com.soywiz.korge.view.*
import com.soywiz.korim.atlas.MutableAtlasUnit
import com.soywiz.korim.color.Colors
import com.github.quillraven.fleks.*
import kotlin.native.concurrent.ThreadLocal
import kotlin.reflect.KClass

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

        val dummy = MyClass(text = "Hello injector!")

        val world = World {
            entityCapacity = 20

            system(::MoveSystem)
            system(::PositionSystem)

            inject(dummy)

            // Register all needed components
            // TODO remove and create components directly on system creation time
            component(::Position)
        }

        val entity = world.entity {
            add<Position> {
                x = 50f
                y = 100f
            }
        }

        addUpdater { dt ->
            world.update(dt.milliseconds.toFloat())
        }
    }
}

data class MyClass(val text: String = "")
data class Position(var x: Float = 0f, var y: Float = 0f)

class MoveSystem : IntervalSystem(
    interval = Fixed(1000f)  // every second
) {

    private val dummy: MyClass = Inject.dependency()

    override fun onInit() {
    }

    override fun onTick() {
        println("MoveSystem: onTick (text: ${dummy.text})")
    }
}

class PositionSystem : IteratingSystem(
    allOf = AllOf(arrayOf(Position::class)),
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

