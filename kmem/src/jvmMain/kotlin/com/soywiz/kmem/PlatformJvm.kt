package com.soywiz.kmem

internal actual val CurrentNativePlatform: NativePlatform by lazy {
    val os = System.getProperty("os.name").lowercase()
    when {
        os.contains("mac") || os.contains("osx") || os.contains("darwin") -> NativePlatform.MAC
        os.contains("lin") -> NativePlatform.LINUX
        os.contains("win") -> NativePlatform.WINDOWS
        else -> NativePlatform.UNKNOWN
    }
}
internal actual val CurrentNativeRuntime: NativeRuntime = NativeRuntime.JVM
internal actual val CurrentNativeArch: NativeArch by lazy {
    val arch = System.getProperty("os.arch")
    when {
        arch.contains("x86") -> if (arch.endsWith("64")) NativeArch.X64 else NativeArch.X86
        arch.contains("aarch64") -> NativeArch.ARM64
        arch.contains("arm") -> NativeArch.ARM32
        else -> NativeArch.UNKNOWN
    }
}
internal actual val CurrentNativeRuntimeVariant: NativeRuntimeVariant by lazy {
    val version = System.getProperty("java.version")
    val vendor = System.getProperty("java.vendor")
    val arch = System.getProperty("os.arch")
    val os = System.getProperty("os.name")
    NativeRuntimeVariant("$vendor :: $version :: $arch :: $os", mapOf("vendor" to vendor, "version" to version, "arch" to arch, "os" to os))
}
