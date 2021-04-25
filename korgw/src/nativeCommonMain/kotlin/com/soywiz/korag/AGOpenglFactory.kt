package com.soywiz.korag

import com.soywiz.kgl.*
import com.soywiz.korag.shader.gl.*

actual object AGOpenglFactory {
	actual fun create(nativeComponent: Any?): AGFactory = AGFactoryNative
	actual val isTouchDevice: Boolean = false
}

object AGFactoryNative : AGFactory {
	override val supportsNativeFrame: Boolean = false
	override fun create(nativeControl: Any?, config: AGConfig): AG = AGNative()
	override fun createFastWindow(title: String, width: Int, height: Int): AGWindow = TODO()
}

open class AGNative(override val glKind: GlslGenerator.Config.Kind = GlslGenerator.Config.Kind.CORE) : AGOpengl() {
	override val nativeComponent = Any()
	override val gl: KmlGl = com.soywiz.kgl.KmlGlNative()
}
