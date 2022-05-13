import com.soywiz.klock.seconds
import com.soywiz.korge.tween.get
import com.soywiz.korge.tween.tween
import com.soywiz.korge.view.Stage
import com.soywiz.korge.view.image
import com.soywiz.korim.bitmap.mipmaps
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs

suspend fun Stage.mainMipmaps() {
    val image = image(resourcesVfs["korge.png"].readBitmap().mipmaps())
    while (true) {
        tween(image::scale[0.01], time = 3.seconds)
        tween(image::scale[0.2], time = 1.seconds)
    }
}
