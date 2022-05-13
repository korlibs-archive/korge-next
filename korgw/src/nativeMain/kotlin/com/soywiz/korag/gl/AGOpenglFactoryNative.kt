package com.soywiz.korag.gl

import com.soywiz.kgl.KmlGl
import com.soywiz.korag.AG
import com.soywiz.korag.AGConfig
import com.soywiz.korag.AGFactory
import com.soywiz.korag.AGWindow

actual object AGOpenglFactory {
	actual fun create(nativeComponent: Any?): AGFactory = AGFactoryNative
	actual val isTouchDevice: Boolean = false
}

object AGFactoryNative : AGFactory {
	override val supportsNativeFrame: Boolean = false
	override fun create(nativeControl: Any?, config: AGConfig): AG = AGNative()
	override fun createFastWindow(title: String, width: Int, height: Int): AGWindow = TODO()
}

open class AGNative() : AGOpengl() {
	override val nativeComponent = Any()
	override val gl: KmlGl = com.soywiz.kgl.KmlGlNative()
}
