package components

import com.soywiz.korge.view.Image
import com.soywiz.korge.view.animation.ImageAnimationView
import com.soywiz.korim.bitmap.Bitmaps

data class Sprite(
    var lifeCycle: LifeCycle = LifeCycle.INACTIVE,
    var imageData: String = "",
    var animation: String = "",
    var isPlaying: Boolean = false,
    var forwardDirection: Boolean = true,
    var loop: Boolean = false,
    // internal data
    var imageAnimView: ImageAnimationView<Image> = ImageAnimationView { Image(Bitmaps.transparent) }.apply { smoothing = false }
) {
    enum class LifeCycle {
        INACTIVE, INIT, ACTIVE, DESTROY
    }
}
