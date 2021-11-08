package com.soywiz.kmem.lib

import com.soywiz.kmem.*
import kotlinx.cinterop.*
import platform.posix.*

//actual typealias NativeInt = platform.posix.ssize_t // Can't typealias another typealias, and we would need this for 32-bit and 64-bit targets

actual typealias NativeInt = kotlin.native.internal.NativePtr
actual fun Long.toNativeInt(): NativeInt = this.toCPointer<ByteVar>().rawValue
actual fun NativeInt.toLongValue(): Long = this.toLong()

actual typealias VoidPtr = kotlin.native.internal.NativePtr
actual fun Long.toVoidPtr(): VoidPtr = this.toCPointer<ByteVar>().rawValue
actual fun VoidPtr.toLongPtr(): Long = this.toLong()

actual typealias NPointed = CPointed
actual interface Library
actual interface StdCallLibrary : Library

actual typealias NArena = kotlinx.cinterop.Arena
actual fun NArenaAlloc(): NArena = Arena()
actual fun NArena.close() {
    this.clear()
}
actual fun NArena.alloc(size: Int): VoidPtr = this.allocArray<ByteVar>(size).rawValue
actual fun VoidPtr.transferBytes(bytes: ByteArray, index: Int, size: Int, write: Boolean, offset: Int) {
    val src = this.toLong().toCPointer<ByteVar>() ?: return
    bytes.usePinned { bytesPin ->
        val dst = bytesPin.startAddressOf
        if (write) {
            platform.posix.memcpy(src + offset, dst + index, size.convert())
        } else {
            platform.posix.memcpy(dst + index, src + offset, size.convert())
        }
    }
}
actual fun VoidPtr.getByte(offset: Int): Byte {
    val pointer = this.toLong().toCPointer<ByteVar>() ?: return 0
    return pointer[offset]
}
actual fun VoidPtr.setByte(value: Byte, offset: Int) {
    val pointer = this.toLong().toCPointer<ByteVar>() ?: return
    pointer[offset] = value
}
