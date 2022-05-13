package com.soywiz.korio.util.i18n

import com.soywiz.korio.jsRuntime

internal actual val systemLanguageStrings: List<String> by lazy { jsRuntime.langs() }
