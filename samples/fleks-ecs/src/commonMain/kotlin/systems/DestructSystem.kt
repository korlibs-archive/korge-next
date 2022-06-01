package systems

import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Injections
import com.github.quillraven.fleks.IteratingSystem
import components.Destruct
import components.Position
import entities.createExplosionArtefact

/**
 * This system controls the "destruction" of an entity (game object).
 *
 */
class DestructSystem(injections: Injections) : IteratingSystem(
    injections,
    allOfComponents = arrayOf(Destruct::class)
) {

    private val positions = injections.componentMapper<Position>()
    private val destructs = injections.componentMapper<Destruct>()

    override fun onTickEntity(entity: Entity) {
        val destruct = destructs[entity]
        if (destruct.triggerDestruction) {
            val position = positions[entity]
            // The spawning of explosion objects is hardcoded here to 40 objects - TODO that should be put into some component config later
            for (i in 0 until 40) {
                world.createExplosionArtefact(position, destruct)
            }
            // now destroy entity
            world.remove(entity)
        }
    }
}
