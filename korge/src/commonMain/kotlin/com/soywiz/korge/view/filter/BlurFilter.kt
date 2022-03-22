package com.soywiz.korge.view.filter

import com.soywiz.korge.debug.*
import com.soywiz.korge.view.*
import com.soywiz.korma.geom.*
import com.soywiz.korui.*

class BlurFilter(radius: Double, expandBorder: Boolean = true) : ComposedFilter() {
    companion object {
        @Deprecated("", ReplaceWith("BlurFilter(radius = initialRadius)"))
        operator fun invoke(initialRadius: Double = 4.0, dummy: Unit = Unit): BlurFilter = BlurFilter(radius = initialRadius)
    }
    private val horizontal = DirectionalBlurFilter(angle = 0.degrees, radius, expandBorder).also { filters.add(it) }
    private val vertical = DirectionalBlurFilter(angle = 90.degrees, radius, expandBorder).also { filters.add(it) }
    var expandBorder: Boolean
        get() = horizontal.expandBorder
        set(value) {
            horizontal.expandBorder = value
            vertical.expandBorder = value
        }
    var radius: Double = radius
        set(value) {
            field = value
            horizontal.radius = radius
            vertical.radius = radius
        }

    override val isIdentity: Boolean get() = radius == 0.0

    override fun buildDebugComponent(views: Views, container: UiContainer) {
        container.uiEditableValue(::radius)
    }
}
