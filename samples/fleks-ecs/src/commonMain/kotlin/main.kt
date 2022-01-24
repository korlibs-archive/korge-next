import com.github.quillraven.fleks.*
import com.soywiz.klock.*
import com.soywiz.korge.Korge
import com.soywiz.korge.scene.MaskTransition
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.scene.sceneContainer
import com.soywiz.korge.view.*
import com.soywiz.korge.view.filter.TransitionFilter
import com.soywiz.korim.atlas.MutableAtlasUnit
import com.soywiz.korim.color.Colors

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

class ExampleScene : Scene() {

    private val atlas = MutableAtlasUnit(1024, 1024)

    override suspend fun Container.sceneInit() {
    }

    override suspend fun Container.sceneMain() {

        val world = World {
            entityCapacity = 20

            system(::MoveSystem)
            system(::PositionSystem)
        }

        addUpdater() { dt ->
            world.update(dt.milliseconds.toFloat())
        }
    }
}

class MoveSystem : IntervalSystem(
    interval = Fixed(1000f)  // every second
) {

    override fun onTick() {
        println("MoveSystem: onTick")
    }
}

class PositionSystem : IteratingSystem(
    interval = Fixed(500f)  // every 500 millisecond
) {

    override fun onTickEntity(entity: Entity) {
        println("PositionSystem: onTickEntity")
    }

}
