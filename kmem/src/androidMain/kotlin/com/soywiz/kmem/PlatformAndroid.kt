package com.soywiz.kmem

internal actual val CurrentNativePlatform: NativePlatform by lazy {
    // @TODO: Determine this
    NativePlatform.ANDROID
}
internal actual val CurrentNativeRuntime: NativeRuntime = NativeRuntime.ANDROID
internal actual val CurrentNativeArch: NativeArch by lazy {
    // @TODO: Determine this
    NativeArch.UNKNOWN
}
internal actual val CurrentNativeRuntimeVariant: NativeRuntimeVariant by lazy {
    // @TODO: Determine this
    NativeRuntimeVariant("Android", mapOf())
}
