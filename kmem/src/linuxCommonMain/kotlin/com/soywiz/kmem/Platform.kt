package com.soywiz.kmem

internal actual val CurrentNativePlatform: NativePlatform = NativePlatform.LINUX
internal actual val CurrentNativeRuntimeVariant: NativeRuntimeVariant by lazy { NativeRuntimeVariant("LINUX") }
