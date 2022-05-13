import com.soywiz.korge.view.Stage
import com.soywiz.korge.view.filter
import com.soywiz.korge.view.filter.ColorTransformFilter
import com.soywiz.korge.view.solidRect
import com.soywiz.korim.color.ColorAdd
import com.soywiz.korim.color.ColorTransform
import com.soywiz.korim.color.Colors

suspend fun Stage.mainColorTransformFilter() {
    //val rect = solidRect(100, 100, )
    val rect = solidRect(100, 100, Colors.DARKGRAY)
    //rect.colorAdd = ColorAdd(+100, 0, 0, 0)
    rect.filter = ColorTransformFilter(ColorTransform(add = ColorAdd(+127, 0, +127, +255)))
}
