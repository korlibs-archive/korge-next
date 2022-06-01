package systems

import com.github.quillraven.fleks.EachFrame
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Injections
import com.github.quillraven.fleks.IteratingSystem
import components.Destruct
import components.Impulse
import components.Position

class CollisionSystem(injections: Injections) : IteratingSystem(
    injections,
    allOfComponents = arrayOf(Position::class),
    interval = EachFrame
//    interval = Fixed(500f)  // for testing every 500 millisecond
) {

    private val positions  = injections.componentMapper<Position>()
    private val destructs = injections.componentMapper<Destruct>()
    private val impulses = injections.componentMapper<Impulse>()

    override fun onInit() {
    }

    override fun onTickEntity(entity: Entity) {
        val pos = positions[entity]

        // To make collision detection easy we check here just the Y position if it is below 200 which means
        // that the object is colliding - In real games here is a more sophisticated collision check necessary ;-)
        if (pos.y > 200.0) {
            pos.y = 200.0
            // Check if entity has a Destruct or Impulse component
            if (destructs.contains(entity)) {
                // Delegate "destruction" of the entity to the DestructSystem - it will destroy the entity after some other task are done
                destructs[entity].triggerDestruction = true
            } else if (impulses.contains(entity)) {
                // Do not destruct entity but let it bounce on the surface
                pos.xAcceleration = pos.xAcceleration * 0.7
                pos.yAcceleration = -pos.yAcceleration * 0.9
            } else {
                // Entity gets destroyed immediately
                world.remove(entity)
            }
        }
    }
}
