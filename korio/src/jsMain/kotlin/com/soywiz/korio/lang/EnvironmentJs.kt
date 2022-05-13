package com.soywiz.korio.lang

import com.soywiz.korio.jsRuntime

internal actual object EnvironmentInternal {
	actual operator fun get(key: String): String? = jsRuntime.env(key)
	actual fun getAll(): Map<String, String> = jsRuntime.envs()
}
