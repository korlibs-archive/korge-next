package systems

import com.github.quillraven.fleks.*
import components.Destruct
import components.Position

class CollisionSystem : IteratingSystem(
    allOfComponents = arrayOf(Position::class),
    interval = EachFrame
//    interval = Fixed(500f)  // for testing every 500 millisecond
) {

    private val positions  = Inject.componentMapper<Position>()
    private val destructs = Inject.componentMapper<Destruct>()

    override fun onInit() {
    }

    override fun onTickEntity(entity: Entity) {
//        println("[Entity: ${entity.id}] MoveSystem onTickEntity")
        val pos = positions[entity]

        // To make collision detection easy we check here just the Y position if it is below 200 which means
        // that the object is colliding - In real games here is a more sophisticated collision check necessary ;-)
        if (pos.y > 200.0) {
            // Check if entity has a destruct component
            if (destructs.contains(entity)) {
                // yes - then delegate "destruction" of the entity to the DestructSystem - it will destroy the entity after some other task are done
                destructs[entity].triggerDestruction = true
            } else {
                // no - else the entity gets destroyed immediately
                world.remove(entity)
            }
        }
    }
}
