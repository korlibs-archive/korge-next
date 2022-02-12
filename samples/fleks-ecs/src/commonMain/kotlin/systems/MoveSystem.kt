package systems

import com.github.quillraven.fleks.*
import components.*

class MoveSystem : IteratingSystem(
    allOf = AllOf(arrayOf(Position::class)),
    interval = EachFrame
//    interval = Fixed(500f)  // for testing every 500 millisecond
) {

    private val positions: ComponentMapper<Position> = Inject.componentMapper()

    override fun onInit() {
    }

    override fun onTickEntity(entity: Entity) {
//        println("[Entity: ${entity.id}] MoveSystem onTickEntity")

        val pos = positions[entity]
        pos.x += pos.xAcceleration * deltaTime
        pos.y += pos.yAcceleration * deltaTime
    }
}
