package com.soywiz.korge.view.filter

import com.soywiz.kds.iterators.*
import com.soywiz.kmem.*
import com.soywiz.korge.render.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korma.geom.*
import com.soywiz.korui.*

/**
 * Allows to create a single [Filter] that will render several [filters] in order.
 */
open class ComposedFilter private constructor(val filters: MutableList<Filter>, unit: Unit = Unit) : Filter {
    constructor() : this(mutableListOf())
    constructor(filters: List<Filter>) : this(if (filters is MutableList<Filter>) filters else filters.toMutableList())
	constructor(vararg filters: Filter) : this(filters.toList())

    override val allFilters: List<Filter> get() = filters.flatMap { it.allFilters }

    override val recommendedFilterScale: Double get() {
        var out = 1.0
        filters.fastForEach { out *= it.recommendedFilterScale  }
        return out
    }

    override fun computeBorder(out: MutableMarginInt, texWidth: Int, texHeight: Int) {
        var sumLeft = 0
        var sumTop = 0
        var sumRight = 0
        var sumBottom = 0
        filters.fastForEach {
            it.computeBorder(out, texWidth, texHeight)
            sumLeft += out.left
            sumRight += out.right
            sumTop += out.top
            sumBottom += out.bottom
        }
        out.setTo(sumTop, sumRight, sumBottom, sumLeft)
        //println(out)
    }

    open val isIdentity: Boolean get() = false

    final override fun render(
        ctx: RenderContext,
        matrix: Matrix,
        texture: Texture,
        texWidth: Int,
        texHeight: Int,
        renderColorAdd: ColorAdd,
        renderColorMul: RGBA,
        blendMode: BlendMode,
        filterScale: Double,
	) {
        if (isIdentity) return IdentityFilter.render(ctx, matrix, texture, texWidth, texHeight, renderColorAdd, renderColorMul, blendMode, filterScale)
        renderIndex(ctx, matrix, texture, texWidth, texHeight, renderColorAdd, renderColorMul, blendMode, filterScale, filters.size - 1)
	}

	fun renderIndex(
		ctx: RenderContext,
		matrix: Matrix,
		texture: Texture,
		texWidth: Int,
		texHeight: Int,
		renderColorAdd: ColorAdd,
		renderColorMul: RGBA,
		blendMode: BlendMode,
        filterScale: Double,
		level: Int,
	) {
        if (level < 0 || filters.isEmpty()) {
            return IdentityFilter.render(ctx, matrix, texture, texWidth, texHeight, renderColorAdd, renderColorMul, blendMode, filterScale)
        }
        //println("ComposedFilter.renderIndex: $level")
		val filter = filters[filters.size - level - 1]

        filter.renderToTextureWithBorder(ctx, matrix, texture, texWidth, texHeight, filterScale) { newtex, newmatrix ->
            renderIndex(ctx, newmatrix, newtex, newtex.width, newtex.height, renderColorAdd, renderColorMul, blendMode, filterScale, level - 1)
        }
	}

    override fun buildDebugComponent(views: Views, container: UiContainer) {
        for (filter in filters) {
            filter.buildDebugComponent(views, container)
        }
    }
}
