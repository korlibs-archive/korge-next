package systems

import com.github.quillraven.fleks.*
import components.Position

class CollisionSystem : IteratingSystem(
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

        if (pos.y > 200) {
            world.remove(entity)
        }
    }
}
