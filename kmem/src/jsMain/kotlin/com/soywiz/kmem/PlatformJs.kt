package com.soywiz.kmem

internal actual val CurrentNativePlatform: NativePlatform by lazy {
    // @TODO: Parse the useragent on the browser, and other stuff in nodejs
    NativePlatform.UNKNOWN
}
internal actual val CurrentNativeRuntime: NativeRuntime = NativeRuntime.JS
internal actual val CurrentNativeArch: NativeArch by lazy {
    // @TODO: Parse the useragent on the browser, and other stuff in nodejs
    NativeArch.UNKNOWN
}
internal actual val CurrentNativeRuntimeVariant: NativeRuntimeVariant by lazy {
    // @TODO: Determine Node, browser, worker, etc.
    NativeRuntimeVariant("JavaScript", mapOf())
}
