package com.soywiz.korag.gl

import com.soywiz.korag.AGFactory
import com.soywiz.korgw.JvmAGFactory

actual object AGOpenglFactory {
    actual fun create(nativeComponent: Any?): AGFactory = JvmAGFactory
    actual val isTouchDevice: Boolean = false
}
