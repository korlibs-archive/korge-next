import com.soywiz.korge.view.Stage
import com.soywiz.korge.view.image
import com.soywiz.korim.bitmap.NativeImage
import com.soywiz.korim.bitmap.context2d
import com.soywiz.korim.format.readBitmap
import com.soywiz.korim.paint.BitmapPaint
import com.soywiz.korim.vector.CycleMethod
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korma.geom.Matrix

suspend fun Stage.mainVectorRendering() {
    val korgeBitmap = resourcesVfs["korge.png"].readBitmap()
    //val bitmap = korgeBitmap.resized(100, 100, ScaleMode.FILL, Anchor.CENTER)
    //image(bitmap)
    val bitmap2 = NativeImage(512, 512).context2d {
        //val bitmap2 = Bitmap32(512, 512).context2d {
        translate(100, 100)
        scale(2.0)
        globalAlpha = 0.75
        fillStyle = BitmapPaint(korgeBitmap, Matrix().translate(50, 50), cycleX = CycleMethod.REPEAT, cycleY = CycleMethod.REPEAT)
        /*
        fillStyle = createLinearGradient(0.0, 0.0, 200.0, 200.0, transform = Matrix().scale(0.5).pretranslate(30, 30))
            .addColorStop(0.0, Colors.RED)
            .addColorStop(1.0, Colors.BLUE)

         */
        fillRect(0.0, 0.0, 100.0, 100.0)
    }
    image(bitmap2)
}
