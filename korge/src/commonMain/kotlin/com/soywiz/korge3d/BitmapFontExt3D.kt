package com.soywiz.korge3d

import com.soywiz.korge.html.*
import com.soywiz.korge.render.*
import com.soywiz.korge.view.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korma.geom.*
import kotlin.math.*

fun BitmapFont.drawText3D(
	ctx: RenderContext3D,
	textSize: Double,
	str: String,
	m: Matrix = Matrix(),
	colMul: RGBA = Colors.WHITE,
	colAdd: Int = 0x7f7f7f7f,
	blendMode: BlendMode = BlendMode.INHERIT,
	filtering: Boolean = true
) {
	val m2 = m.clone()
	val scale = textSize / fontSize.toDouble()
	m2.prescale(scale, scale)
	var dx = 0.0
	var dy = 0.0
	for (n in str.indices) {
		val c1 = str[n].toInt()
		if (c1 == '\n'.toInt()) {
			dx = 0.0
			dy += fontSize
			continue
		}
		val c2 = str.getOrElse(n + 1) { ' ' }.toInt()
		val glyph = this[c1]
		val tex = glyph.texture
		ctx.batch.drawQuad(
			ctx.rctx.getTex(tex),
			(dx + glyph.xoffset).toFloat(),
			(dy + glyph.yoffset).toFloat(),
			m = m2,
			colorMul = colMul,
			colorAdd = colAdd,
			blendFactors = blendMode.factors,
			filtering = filtering
		)
		val kerningOffset = kernings[BitmapFont.Kerning.buildKey(c1, c2)]?.amount ?: 0
		dx += glyph.xadvance + kerningOffset
	}
}

fun RenderContext3D.drawText(
	font: BitmapFont,
	textSize: Double,
	str: String,
	m: Matrix = Matrix(),
	colMul: RGBA = Colors.WHITE,
	colAdd: Int = 0x7f7f7f7f,
	blendMode: BlendMode = BlendMode.INHERIT,
	filtering: Boolean = true
) {
	font.drawText3D(this, textSize, str, m, colMul, colAdd, blendMode, filtering)
}
