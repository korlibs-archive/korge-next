package systems

import com.github.quillraven.fleks.*
import components.ImageAnimation
import components.Position
import components.Spawner
import kotlin.random.Random

fun ClosedFloatingPointRange<Double>.random() = Random.nextDouble(start, endInclusive)
fun ClosedFloatingPointRange<Float>.random() = Random.nextDouble(start.toDouble(), endInclusive.toDouble()).toFloat()
fun IntRange.random() = Random.nextInt(start, endInclusive)

class SpawnerSystem : IteratingSystem(
    allOfComponents = arrayOf(Spawner::class),
    interval = Fixed(500f)  // every 500 millisecond
) {

    private val imageAnimation: ComponentMapper<ImageAnimation> = Inject.componentMapper()
    private val position: ComponentMapper<Position> = Inject.componentMapper()
    private val spawner: ComponentMapper<Spawner> = Inject.componentMapper()

    override fun onInit() {
    }

    override fun onTickEntity(entity: Entity) {
        spawn(entity)
        println("[Entity: ${entity.id}] spawner on tick - create new entity")
    }

    private fun spawn(entity: Entity) {
        world.entity {
            add<Position> {  // Position of spawner
                x = position[entity].x
                if (spawner[entity].xPosVariation != 0.0) x += (-spawner[entity].xPosVariation..spawner[entity].xPosVariation).random()
                y = position[entity].y
                if (spawner[entity].yPosVariation != 0.0) y += (-spawner[entity].yPosVariation..spawner[entity].yPosVariation).random()
                xAcceleration = spawner[entity].xAccel
                yAcceleration = spawner[entity].yAccel
            }
            add<ImageAnimation> {  // Config for spawner object
                imageData = imageAnimation[entity].imageData
                animation = imageAnimation[entity].animation
                isPlaying = imageAnimation[entity].isPlaying
            }
        }
    }
}
/*
class SpawnerSystem : SubSystemBase() {

    override fun registerEntity(id: Int, type: EntityAspects) {
        if (type.spawner) {
            activeEntities.add(id)

            // initialize component data and config
            getEntityComponents(id).spawnerComponent.let {
                it.nextSpawnIn = 0
            }
        }
    }

    override fun fixedUpdate() {
        // Do nothing
    }

    override fun update(dt: TimeSpan, tmod: Double) {
        activeEntities.fastForEach { id ->
            val entity = getEntityComponents(id)
            entity.lock = true
            val spawner = entity.spawnerComponent
            if (spawner.interval > 0) {
                if (spawner.nextSpawnIn <= 0) {
                    spawn(id)
                    spawner.nextSpawnIn = spawner.interval
                    if (spawner.timeVariation != 0) spawner.nextSpawnIn += (-spawner.timeVariation..spawner.timeVariation).random()
                } else {
                    spawner.nextSpawnIn--
                }
            }
            entity.lock = false
        }
    }

    override fun postUpdate(dt: TimeSpan, tmod: Double) {
        // Do nothing
    }
}
*/
