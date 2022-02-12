package systems

import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.github.quillraven.fleks.Inject
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.addTo
import com.soywiz.korim.format.ImageAnimation

import com.github.quillraven.fleks.*
import components.*
import components.Sprite
import components.Sprite.LifeCycle
import aseImage

/**
 * This System takes care of displaying sprites (image-animation objects) on the screen. It takes the configuration from
 * [Sprite] component to setup graphics from Assets and create an ImageAnimationView object for displaying in the Container.
 *
 */
class SpriteSystem : IteratingSystem(
    allOf = AllOf(arrayOf(Sprite::class, Position::class)),
    interval = EachFrame
//    interval = Fixed(500f)  // for testing every 500 millisecond
) {

    private val positions: ComponentMapper<Position> = Inject.componentMapper()
    private val sprites: ComponentMapper<Sprite> = Inject.componentMapper()

    override fun onInit() {
    }

    override fun onTickEntity(entity: Entity) {
//        println("[Entity: ${entity.id}] SpriteSystem onTickEntity")

        val sprite = sprites[entity]
        val pos = positions[entity]
        when (sprite.lifeCycle) {
            LifeCycle.INIT -> {
                sprite.lifeCycle = LifeCycle.ACTIVE
            }
            LifeCycle.ACTIVE -> {
                // sync view position
                sprite.imageAnimView.x = pos.x
                sprite.imageAnimView.y = pos.y
            }
            LifeCycle.DESTROY -> {
                // Object is going to be recycled
                world.remove(entity)
            }
            else -> {}
        }
    }

    class SpriteListener : ComponentListener<Sprite> {

        private val layerContainer: Container = Inject.dependency()

        override fun onComponentAdded(entity: Entity, component: Sprite) {
            // Set animation object
            component.imageAnimView.animation =
                // TODO get this from Assets object with "imageData" string
                aseImage?.animationsByName?.getOrElse(component.animation) { aseImage?.defaultAnimation }
//        component.imageAnimView.onDestroyLayer = { image -> imageBitmapTransparentPool.free(image) }
            component.imageAnimView.onPlayFinished = { component.lifeCycle = Sprite.LifeCycle.DESTROY }
            component.imageAnimView.addTo(layerContainer)
            // Set play status
            component.imageAnimView.direction = when {
                component.forwardDirection && !component.loop -> ImageAnimation.Direction.ONCE_FORWARD
                !component.forwardDirection && component.loop -> ImageAnimation.Direction.REVERSE
                !component.forwardDirection && !component.loop -> ImageAnimation.Direction.ONCE_REVERSE
                else -> ImageAnimation.Direction.FORWARD
            }
            if (component.isPlaying) { component.imageAnimView.play() }
            component.lifeCycle = Sprite.LifeCycle.ACTIVE

//        println("Component $component")
//        println("  added to Entity '${entity.id}'!")
        }

        override fun onComponentRemoved(entity: Entity, component: Sprite) {
//        println("Component $component")
//        println("  removed from Entity '${entity.id}'!")

            component.imageAnimView.removeFromParent()
        }
    }
}
