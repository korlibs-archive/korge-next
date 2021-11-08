package com.soywiz.kmem

internal actual val CurrentNativePlatform: NativePlatform = NativePlatform.MAC
internal actual val CurrentNativeRuntimeVariant: NativeRuntimeVariant by lazy { NativeRuntimeVariant("MAC") }
