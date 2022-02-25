package systems

import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Inject
import com.github.quillraven.fleks.IteratingSystem
import components.*
import utils.random

/**
 * This system controls the "destruction" of an game object / entity.
 *
 */
class DestructSystem : IteratingSystem(
    allOfComponents = arrayOf(Destruct::class)
) {

    private val positions = Inject.componentMapper<Position>()
    private val destructs = Inject.componentMapper<Destruct>()

    override fun onTickEntity(entity: Entity) {
        val destruct = destructs[entity]
        if (destruct.triggerDestruction) {
            val pos = positions[entity]
            // The spawning of exposion objects is hardcoded here to 10 objects - that should be put into some component later
            for (i in 0 until 20) {
                world.entity {
                    add<Position> {  // Position of explosion object
                        // set initial position of explosion object to collision position
                        x = pos.x
                        y = pos.y - (destruct.explosionParticleRange * 0.5)
                        if (destruct.explosionParticleRange != 0.0) {
                            x += (-destruct.explosionParticleRange..destruct.explosionParticleRange).random()
                            y += (-destruct.explosionParticleRange..destruct.explosionParticleRange).random()
                        }
                        // make sure that all spawned objects are above 200 - this is hardcoded for now since we only have some basic collision detection at y > 200
                        // otherwise they will be destroyed immediately and false appear at position 0x0
                        if (y > 200.0) { y = 199.0 }
                        xAcceleration = pos.xAcceleration + random(destruct.explosionParticleAcceleration)
                        yAcceleration = -pos.yAcceleration + random(destruct.explosionParticleAcceleration)
                    }
                    add<Sprite> {
                        imageData = "sprite"  // "" - Disable sprite graphic for spawned object
                        animation = "FireTrail"  // "FireTrail" - "TestNum"
                        isPlaying = true
                    }
                    add<Rigidbody> {
                        mass = 2.0
                    }
                    add<Impulse> {}
                }
            }
            // now destroy entity
            world.remove(entity)
        }
    }
}
