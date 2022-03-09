package components

/**
 * This is a very basic definition of a rigidbody which does not take rotation into account.
 */
data class Rigidbody(
    var mass: Double = 0.0,
    var velocityX: Double = 0.0,
    var velocityY: Double = 0.0,
    var damping: Double = 0.0,  // e.g. air resistance of the object when falling
    var friction: Double = 0.0,  // e.g. friction of the object when it moves over surfaces
)
