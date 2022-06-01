package systems

import com.github.quillraven.fleks.EachFrame
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Injections
import com.github.quillraven.fleks.IteratingSystem
import components.Position
import components.Rigidbody

/**
 * A system which moves entities. It either takes the rididbody of an entity into account or if not
 * it moves the entity linear without caring about gravity.
 */
class MoveSystem(injections: Injections) : IteratingSystem(
    injections,
    allOfComponents = arrayOf(Position::class),  // Position component absolutely needed for movement of entity objects
    anyOfComponents = arrayOf(Position::class, Rigidbody::class),  // Rigidbody not necessarily needed for movement
    interval = EachFrame
) {

    private val positions = injections.componentMapper<Position>()
    private val rigidbodies = injections.componentMapper<Rigidbody>()

    override fun onInit() {}

    override fun onTickEntity(entity: Entity) {
        val pos = positions[entity]

        if (rigidbodies.contains(entity)) {
            // Entity has a rigidbody - that means the movement will be calculated depending on it
            val rigidbody = rigidbodies[entity]
            // Currently we just add gravity to the entity
            pos.yAcceleration += rigidbody.mass * 9.81
            // TODO implement more sophisticated movement with rigidbody taking damping and friction into account
        }

        pos.x += pos.xAcceleration * deltaTime
        pos.y += pos.yAcceleration * deltaTime
    }
}
