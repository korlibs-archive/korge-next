package com.soywiz.korio

import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString

fun getExecutablePath(): String = kotlinx.cinterop.memScoped {
	val maxSize = 4096
	val data = allocArray<kotlinx.cinterop.UShortVar>(maxSize + 1)
	platform.windows.GetModuleFileNameW(null, data.reinterpret(), maxSize.convert())
	data.toKString()
}.replace('\\', '/')

fun getExecutableDirectory(): String = getExecutablePath().substringBeforeLast('/')

actual fun nativeCwd(): String = getExecutableDirectory()
