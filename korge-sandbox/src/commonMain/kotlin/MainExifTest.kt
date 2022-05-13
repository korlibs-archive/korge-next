import com.soywiz.korge.view.Stage
import com.soywiz.korge.view.filter.OldBlurFilter
import com.soywiz.korge.view.filters
import com.soywiz.korge.view.image
import com.soywiz.korge.view.scale
import com.soywiz.korim.format.readBitmapInfo
import com.soywiz.korim.format.readBitmapSliceWithOrientation
import com.soywiz.korio.file.std.resourcesVfs

suspend fun Stage.mainExifTest() {
    //val file = localVfs("/tmp/Exif5-2x.avif")
    val file = resourcesVfs["Portrait_3.jpg"]
    //val info = resourcesVfs["IMG_5455.HEIC"].readImageInfo(HEICInfo)
    //val info = localVfs("/tmp/IMG_5455.HEIC").readImageInfo(HEICInfo, ImageDecodingProps(debug = true))
    //val info = localVfs("/tmp/Exif5-2x.avif").readImageInfo(HEICInfo, ImageDecodingProps(debug = true))
    val info = file.readBitmapInfo()
    image(file.readBitmapSliceWithOrientation())
        .scale(0.2)
        .filters(OldBlurFilter())
    //println(info)
}

