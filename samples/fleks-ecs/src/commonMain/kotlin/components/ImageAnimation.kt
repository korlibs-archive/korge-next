package components

import com.github.quillraven.fleks.ComponentListener
import com.github.quillraven.fleks.Entity
import com.soywiz.kds.Pool
import com.soywiz.korge.view.Image
import com.soywiz.korge.view.animation.ImageAnimationView
import com.soywiz.korim.bitmap.Bitmaps

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

    private val imageAnimationViewPool = Pool(reset = { it.rewind() }) {
        ImageAnimationView(/* TODO enableUpdater = false*/) { imageBitmapTransparentPool.alloc() }.apply { smoothing = true }
    }
    private val imageBitmapTransparentPool = Pool(reset = { it.bitmap = Bitmaps.transparent }, preallocate = 20) {
        Image(Bitmaps.transparent)
    }


    override fun onComponentAdded(entity: Entity, component: ImageAnimation) {

        // Init component view

        println("Component $component added to $entity!")
    }

    override fun onComponentRemoved(entity: Entity, component: ImageAnimation) {

        // Reset details for reusing in another entity

        println("Component $component removed from $entity!")
    }
}
