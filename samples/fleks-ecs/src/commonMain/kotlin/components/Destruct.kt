package components

/**
 * This component contains details on destruction of the entity like if other entities should be spawned
 * or if other systems should be fed with data (score, player health or damage, enemy damage,
 * collectable rewards, etc.)
 *
 */
data class Destruct(
    // Setting this to true triggers the DestructSystem to execute destruction of the entity
    var triggerDestruction: Boolean = false,
    // details about what explosion animation should be spawned, etc.
    var spawnExplosion: Boolean = false,
    var explosionParticleRange: Double = 0.0,
    var explosionParticleAcceleration: Double = 0.0,
)
