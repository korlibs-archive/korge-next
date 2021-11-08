package com.soywiz.kmem

internal actual val CurrentNativePlatform: NativePlatform = NativePlatform.WINDOWS
internal actual val CurrentNativeRuntimeVariant: NativeRuntimeVariant by lazy { NativeRuntimeVariant("WINDOWS") }
