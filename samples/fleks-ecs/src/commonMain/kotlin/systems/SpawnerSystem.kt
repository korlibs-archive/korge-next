package systems

import com.github.quillraven.fleks.*
import components.*
import utils.random

class SpawnerSystem : IteratingSystem(
    allOfComponents = arrayOf(Spawner::class),
    interval = EachFrame
//    interval = Fixed(500f)  // for testing every 500 millisecond
) {

    private val positions = Inject.componentMapper<Position>()
    private val spawners = Inject.componentMapper<Spawner>()

    override fun onInit() {
    }

    override fun onTickEntity(entity: Entity) {
        val spawner = spawners[entity]
        if (spawner.interval > 0) {
            if (spawner.nextSpawnIn <= 0) {
//                println("[Entity: ${entity.id}] SpawnerSystem onTickEntity - create new entity")
                spawn(entity)
                spawner.nextSpawnIn = spawner.interval
                if (spawner.timeVariation != 0) spawner.nextSpawnIn += (-spawner.timeVariation..spawner.timeVariation).random()
            } else {
                spawner.nextSpawnIn--
            }
        }
    }

    private fun spawn(entity: Entity) {
        val spawnerPosition = positions[entity]
        val spawner = spawners[entity]
        for (i in 0 until spawner.numberOfObjects) {
            world.entity {
                add<Position> {  // Position of spawner
                    x = spawnerPosition.x + spawner.positionX
                    if (spawner.positionVariationX != 0.0) x += (-spawner.positionVariationX..spawner.positionVariationX).random()
                    y = spawnerPosition.y + spawner.positionY
                    if (spawner.positionVariationY != 0.0) y += (-spawner.positionVariationY..spawner.positionVariationY).random()
                    xAcceleration = spawner.positionAccelerationX
                    yAcceleration = spawner.positionAccelerationY
                    if (spawner.positionAccelerationVariation != 0.0) {
                        val variation = (-spawner.positionAccelerationVariation..spawner.positionAccelerationVariation).random()
                        xAcceleration += variation
                        xAcceleration += variation
                    }
                }
                // Add spawner feature
                if (spawner.spawnerNumberOfObjects != 0) {
                    add<Spawner> {
                        numberOfObjects = spawner.spawnerNumberOfObjects
                        interval = spawner.spawnerInterval
                        timeVariation = spawner.spawnerTimeVariation
                        // Position details for spawned objects
                        positionX = spawner.spawnerPositionX
                        positionY = spawner.spawnerPositionY
                        positionVariationX = spawner.spawnerPositionVariationX
                        positionVariationY = spawner.spawnerPositionVariationY
                        positionAccelerationX = spawner.spawnerPositionAccelerationX
                        positionAccelerationY = spawner.spawnerPositionAccelerationY
                        positionAccelerationVariation = spawner.spawnerPositionAccelerationVariation
                        // Sprite animation details for spawned objects
                        spriteImageData = spawner.spawnerSpriteImageData
                        spriteAnimation = spawner.spawnerSpriteAnimation
                        spriteIsPlaying = spawner.spawnerSpriteIsPlaying
                        spriteForwardDirection = spawner.spawnerSpriteForwardDirection
                        spriteLoop = spawner.spawnerSpriteLoop
                    }
                }
                // Add sprite animations
                if (spawner.spriteImageData.isNotEmpty()) {
                    add<Sprite> {  // Config for spawned object
                        imageData = spawner.spriteImageData
                        animation = spawner.spriteAnimation
                        isPlaying = spawner.spriteIsPlaying
                        forwardDirection = spawner.spriteForwardDirection
                        loop = spawner.spriteLoop
                    }
                }
                // Add destruct details
                if (spawner.destruct) {
                    add<Destruct> {
                        spawnExplosion = true
                        explosionParticleRange = 10.0
                        explosionParticleAcceleration = 200.0
                    }
                }
            }
        }
    }
}
