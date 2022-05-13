package com.soywiz.korio.lang

import kotlin.reflect.KClass

actual val <T : Any> KClass<T>.portableSimpleName: String get() = java.simpleName
