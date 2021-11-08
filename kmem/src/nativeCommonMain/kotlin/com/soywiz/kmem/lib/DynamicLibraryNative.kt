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

actual val NativeIntSize: Int = sizeOf<platform.posix.ssize_tVar>().toInt()

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

private fun invalidPointerError(): Nothing = error("Invalid Pointer")

actual fun VoidPtr.getByte(offset: Int): Byte = ((this.toLong() + offset).toCPointer<ByteVar>() ?: invalidPointerError())[0]
actual fun VoidPtr.setByte(offset: Int, value: Byte) { ((this.toLong() + offset).toCPointer<ByteVar>() ?: invalidPointerError())[0] = value }

actual fun VoidPtr.getShort(offset: Int): Short = ((this.toLong() + offset).toCPointer<ShortVar>() ?: invalidPointerError())[0]
actual fun VoidPtr.setShort(offset: Int, value: Short) { ((this.toLong() + offset).toCPointer<ShortVar>() ?: invalidPointerError())[0] = value }

actual fun VoidPtr.getInt(offset: Int): Int = ((this.toLong() + offset).toCPointer<IntVar>() ?: invalidPointerError())[0]
actual fun VoidPtr.setInt(offset: Int, value: Int) { ((this.toLong() + offset).toCPointer<IntVar>() ?: invalidPointerError())[0] = value }

actual fun VoidPtr.getLong(offset: Int): Long = ((this.toLong() + offset).toCPointer<LongVar>() ?: invalidPointerError())[0]
actual fun VoidPtr.setLong(offset: Int, value: Long) { ((this.toLong() + offset).toCPointer<LongVar>() ?: invalidPointerError())[0] = value }
