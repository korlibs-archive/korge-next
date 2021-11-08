package com.soywiz.kmem

object Platform {
    val PLATFORM get() = NativePlatform.CURRENT
    val ARCH get() = NativeArch.CURRENT
    val RUNTIME get() = NativeRuntime.CURRENT
    val RUNTIME_VARIANT get() = NativeRuntimeVariant.CURRENT
}

enum class NativePlatform {
    WINDOWS, LINUX, MAC, IOS, ANDROID, UNKNOWN;

    val isWindows get() = this == WINDOWS
    val isLinux get() = this == LINUX
    val isMac get() = this == MAC
    val isIos get() = this == IOS
    val isApple get() = isMac || isIos
    val isPosix get() = !isWindows
    val isAndroid get() = this == ANDROID
    val isMobile get() = isIos || isAndroid
    val isDesktop get() = isWindows || isLinux || isMac

    companion object {
        val CURRENT get() = CurrentNativePlatform
    }
}
enum class NativeRuntime {
    JS, JVM, ANDROID, NATIVE, UNKNOWN;

    val isJs get() = this == JS
    val isJvm get() = this == JVM
    val isJvmOrAndroid get() = this == JVM || this == ANDROID
    val isNative get() = this == NATIVE

    companion object {
        val CURRENT get() = CurrentNativeRuntime
    }
}
enum class NativeArch(val bits: Int) {
    X86(32), X64(64), ARM32(32), ARM64(64), UNKNOWN(32);

    val is32Bits get() = bits == 32
    val is64Bits get() = bits == 64

    val isX86 get() = this == X86
    val isX64 get() = this == X64
    val isX86Compatible get() = isX86 || isX64

    val isArm32 get() = this == ARM32
    val isArm64 get() = this == ARM64
    val isArm get() = isArm32 || isArm64

    companion object {
        val CURRENT get() = CurrentNativeArch
    }
}

open class NativeRuntimeVariant(val variant: String, val extra: Map<String, String> = mapOf()) {
    companion object {
        val CURRENT get() = CurrentNativeRuntimeVariant
    }
}

internal expect val CurrentNativePlatform: NativePlatform
internal expect val CurrentNativeRuntime: NativeRuntime
internal expect val CurrentNativeArch: NativeArch
internal expect val CurrentNativeRuntimeVariant: NativeRuntimeVariant
