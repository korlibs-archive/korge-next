package components

import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity

data class ImageAnimation(
    var lifeCycle: LifeCycle = LifeCycle.INACTIVE,
    var imageData: String = "",
    var animation: String = "",
    var isPlaying: Boolean = false,
    var forwardDirection: Boolean = true,
    var loop: Boolean = false
) {
    enum class LifeCycle {
        INACTIVE, INIT, ACTIVE, DESTROY
    }
}

class ImageAnimationListener : ComponentListener<ImageAnimation> {
    override fun onComponentAdded(entity: Entity, component: ImageAnimation) {

        // Init component view

        println("Component $component added to $entity!")
    }

    override fun onComponentRemoved(entity: Entity, component: ImageAnimation) {

        // Reset details for reusing in another entity

        println("Component $component removed from $entity!")
    }
}
