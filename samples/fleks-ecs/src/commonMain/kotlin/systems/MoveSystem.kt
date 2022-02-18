package systems

import com.github.quillraven.fleks.*
import components.*

/**
 * A system which moves entities. It either takes the rididbody of an entity into account or if not
 * it moves the entity linear without caring about gravity.
 */
class MoveSystem : IteratingSystem(
    allOfComponents = arrayOf(Position::class),  // Position component absolutely needed for movement of entity objects
    anyOfComponents = arrayOf(Position::class, Rigidbody::class),  // Rigidbody not necessarily needed for movement
    interval = EachFrame
//    interval = Fixed(500f)  // for testing every 500 millisecond
) {

    private val positions = Inject.componentMapper<Position>()
    private val rigidbodies = Inject.componentMapper<Rigidbody>()

    override fun onInit() {
    }

    override fun onTickEntity(entity: Entity) {
//        println("[Entity: ${entity.id}] MoveSystem onTickEntity")
        val pos = positions[entity]

        if (rigidbodies.contains(entity)) {
            // Entity has a rigidbody - that means the movement will be calculated depending on it
            val rigidbody = rigidbodies[entity]
//            pos.x += pos.a
            // TODO implement movement with rigidbody

        } else {
            // Do movement without rigidbody which means that the object will not react to gravity, friction and damping
            pos.x += pos.xAcceleration * deltaTime
            pos.y += pos.yAcceleration * deltaTime
        }
    }
}
