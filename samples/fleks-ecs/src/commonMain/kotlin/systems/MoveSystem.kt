package systems

import com.github.quillraven.fleks.*
import components.*

class MoveSystem : IteratingSystem(
    allOfComponents = arrayOf(Position::class),
    interval = Fixed(500f)  // every 500 millisecond
) {

    private val position: ComponentMapper<Position> = Inject.componentMapper()

    override fun onInit() {
    }

    override fun onTickEntity(entity: Entity) {
        val pos = position[entity]
        pos.x += pos.xAcceleration * deltaTime
        pos.y += pos.yAcceleration * deltaTime

        println("[Entity: ${entity.id}] move on tick")
    }
}

/*
class MovingSystem : SubSystemBase() {

    override fun registerEntity(id: Int, type: EntityAspects) {
        if (type.position) {
            activeEntities.add(id)

            // initialize component data and config
            getEntityComponents(id).positionComponent.let {
                it.x = 0.0
                it.y = 0.0
            }
        }
    }

    override fun fixedUpdate() {
    }

    override fun update(dt: TimeSpan, tmod: Double) {

        // TODO for stesting only...
        activeEntities.fastForEach { id ->
            val positionComponent = getEntityComponents(id).positionComponent
            // TODO further implement dynamic moving
            positionComponent.x += positionComponent.xAcceleration * tmod
            positionComponent.y += positionComponent.yAcceleration * tmod
        }

    }

    override fun postUpdate(dt: TimeSpan, tmod: Double) {
    }

}
*/
