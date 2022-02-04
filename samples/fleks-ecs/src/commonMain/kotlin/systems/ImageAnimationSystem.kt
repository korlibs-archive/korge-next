package systems

//import aseImage
import com.github.quillraven.fleks.*
import com.soywiz.kds.Pool
import com.soywiz.kds.iterators.fastForEach
import com.soywiz.klock.TimeSpan
import com.soywiz.korge.view.*
import com.soywiz.korge.view.animation.ImageAnimationView
import com.soywiz.korim.bitmap.Bitmaps
import components.*
import components.ImageAnimation.LifeCycle

/**
 * This System takes care of displaying sprites (image-animation objects) on the screen. It takes the configuration from
 * [ImageAnimationComponent] to setup graphics from Assets and create an ImageAnimationView object for displaying in the Container.
 *
 */
class ImageAnimationSystem : IteratingSystem(
    allOfComponents = arrayOf(ImageAnimation::class, Position::class),
    interval = Fixed(500f)  // every 500 millisecond
) {

    private val position: ComponentMapper<Position> = Inject.componentMapper()
    private val imageAnimation: ComponentMapper<ImageAnimation> = Inject.componentMapper()

    override fun onInit() {
    }

    override fun onTickEntity(entity: Entity) {
        println("[Entity: ${entity.id}] image animation on tick")
//        println("pos id: ${position.id} x: ${position[entity].x} y: ${position[entity].y}")
    }
}

/*
class ImageAnimationSystem(
    private val container: Container
) : SubSystemBase() {

    private val doSmoothing = false

    private val imageAnimViewMap: MutableMap<Int, ImageAnimationView<Image>> = mutableMapOf()

    override fun registerEntity(id: Int, type: EntityAspects) {
        if (type.imageAnimation) {
            activeEntities.add(id)
            getEntityComponents(id).imageAnimationComponent.lifeCycle = LifeCycle.INIT
        }
    }

    // Special implementation for this sub-system
    override fun unregisterEntity(id: Int) {
        val imageAnimView = imageAnimViewMap.remove(id)!!
        imageAnimationViewPool.free(imageAnimView)
        imageAnimView.removeFromParent()
        activeEntities.remove(id)
        getEntityComponents(id).imageAnimationComponent.lifeCycle = LifeCycle.INACTIVE
    }

    override fun fixedUpdate() {
    }

    override fun update(dt: TimeSpan, tmod: Double) {
        activeEntities.fastForEach { id ->
            val entry = getEntityComponents(id)
            when (entry.imageAnimationComponent.lifeCycle) {
                LifeCycle.INIT -> createImageAnimationView(entry, id)
                LifeCycle.ACTIVE -> {
                    imageAnimViewMap[id]?.updateAnimation(dt)
                }
                else -> {}
            }
        }
    }

    override fun postUpdate(dt: TimeSpan, tmod: Double) {
        activeEntities.fastForEach { id ->
            val imageAnimation = getEntityComponents(id)
            when (imageAnimation.imageAnimationComponent.lifeCycle) {
                LifeCycle.INIT -> {}
                LifeCycle.ACTIVE -> {
                    // sync view position
                    val container = imageAnimViewMap[id]
                    container?.x = imageAnimation.positionComponent.x
                    container?.y = imageAnimation.positionComponent.y
                }
                LifeCycle.DESTROY -> {
                    // Object is going to be recycled
                    destroyEntity(id)
                }
                else -> {}
            }
        }
    }

    private fun createImageAnimationView(entry: EntityComponents, id: Int) {
        // initialize component data and config
        entry.let {
            val imageAnimView = imageAnimationViewPool.alloc()
            // If Asset is not available destroy the object again
            it.imageAnimationComponent.lifeCycle = if (aseImage == null) LifeCycle.DESTROY else LifeCycle.ACTIVE

            // Set animation object
            imageAnimView.animation =
                // TODO get this from Assets object with "imageData" string
                aseImage?.animationsByName?.getOrElse(it.imageAnimationComponent.animation) { aseImage?.defaultAnimation }
            imageAnimView.onDestroyLayer = { image -> imageBitmapTransparentPool.free(image) }
            imageAnimView.onPlayFinished = { it.imageAnimationComponent.lifeCycle = LifeCycle.DESTROY }
            imageAnimView.addTo(container)
            // Set play status
            imageAnimView.direction = when {
                it.imageAnimationComponent.forwardDirection && !it.imageAnimationComponent.loop -> ImageAnimation.Direction.ONCE_FORWARD
                !it.imageAnimationComponent.forwardDirection && it.imageAnimationComponent.loop -> ImageAnimation.Direction.REVERSE
                !it.imageAnimationComponent.forwardDirection && !it.imageAnimationComponent.loop -> ImageAnimation.Direction.ONCE_REVERSE
                else -> ImageAnimation.Direction.FORWARD
            }
            if (it.imageAnimationComponent.isPlaying) imageAnimView.play()

            imageAnimViewMap[id] = imageAnimView
        }
    }

    private val imageAnimationViewPool = Pool(reset = { it.rewind() }) {
        ImageAnimationView(enableUpdater = false) { imageBitmapTransparentPool.alloc() }.apply { smoothing = doSmoothing }
    }
    private val imageBitmapTransparentPool = Pool(reset = { it.bitmap = Bitmaps.transparent }, preallocate = 20) {
        Image(Bitmaps.transparent)
    }
}
*/
