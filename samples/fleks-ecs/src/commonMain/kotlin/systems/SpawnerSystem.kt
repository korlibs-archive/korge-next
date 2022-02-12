package systems

import kotlin.random.Random
import com.github.quillraven.fleks.*
import components.*

class SpawnerSystem : IteratingSystem(
    allOf = AllOf(arrayOf(Spawner::class)),
    interval = EachFrame
//    interval = Fixed(500f)  // for testing every 500 millisecond
) {

    private val sprites: ComponentMapper<Sprite> = Inject.componentMapper()
    private val positions: ComponentMapper<Position> = Inject.componentMapper()
    private val spawners: ComponentMapper<Spawner> = Inject.componentMapper()

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
        val pos = positions[entity]
        val spawner = spawners[entity]
        for (i in 0 until spawner.numberOfObjects) {
            world.entity {
                add<Position> {  // Position of spawner
                    x = pos.x
                    if (spawner.positionVariationX != 0.0) x += (-spawner.positionVariationX..spawner.positionVariationX).random()
                    y = pos.y
                    if (spawner.positionVariationY != 0.0) y += (-spawner.positionVariationY..spawner.positionVariationY).random()
                    xAcceleration = spawner.positionAccelerationX
                    yAcceleration = spawner.positionAccelerationY
                }
                add<Sprite> {  // Config for spawned object
                    imageData = spawner.spriteImageData
                    animation = spawner.spriteAnimation
                    isPlaying = spawner.spriteIsPlaying
                    forwardDirection = spawner.spriteForwardDirection
                    loop = spawner.spriteLoop
                }
            }
        }
    }

    private fun ClosedFloatingPointRange<Double>.random() = Random.nextDouble(start, endInclusive)
    private fun ClosedFloatingPointRange<Float>.random() = Random.nextDouble(start.toDouble(), endInclusive.toDouble()).toFloat()
}
