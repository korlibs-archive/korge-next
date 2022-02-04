package components

import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity

data class Position(
    var x: Double = 0.0,
    var y: Double = 0.0,
    var xAcceleration: Double = 0.0,
    var yAcceleration: Double = 0.0,
)

class PositionListener : ComponentListener<Position> {
    override fun onComponentAdded(entity: Entity, component: Position) {
        println("Component $component added to $entity!")
    }

    override fun onComponentRemoved(entity: Entity, component: Position) {

        // Reset details for reusing in another entity
        component.x = 0.0
        component.y = 0.0
        component.xAcceleration = 0.0
        component.yAcceleration = 0.0

        println("Component $component removed from $entity!")
    }
}
