package com.soywiz.kmem.lib

import com.soywiz.kmem.*
import com.sun.jna.*

actual typealias NativeInt = NativeLong
actual fun Long.toNativeInt(): NativeInt = NativeLong(this)
actual fun NativeInt.toLongValue(): Long = this.toLong()

actual typealias VoidPtr = Pointer
actual fun Long.toVoidPtr(): VoidPtr = Pointer.createConstant(this)
actual fun VoidPtr.toLongPtr(): Long = Pointer.nativeValue(this)

actual typealias Library = com.sun.jna.Library
actual typealias StdCallLibrary = com.sun.jna.win32.StdCallLibrary
actual abstract class NPointed

actual class NArena {
    val ptrs = arrayListOf<Memory>()

    fun add(ptr: Memory) {
        ptrs.add(ptr)
    }

    fun clear() {
        ptrs.forEach { it.clear() }
        ptrs.clear()
    }
}
actual fun NArenaAlloc() = NArena()
actual fun NArena.close() = this.clear()
actual fun NArena.alloc(size: Int): VoidPtr = Memory(size.toLong()).also { this.add(it) }
actual fun VoidPtr.transferBytes(bytes: ByteArray, index: Int, size: Int, write: Boolean, offset: Int) {
    if (write) {
        this.write(offset.toLong(), bytes, index, size)
    } else {
        this.read(offset.toLong(), bytes, index, size)
    }
}
actual fun VoidPtr.getByte(offset: Int): Byte = this.getByte(offset.toLong())
actual fun VoidPtr.setByte(value: Byte, offset: Int) = this.setByte(offset.toLong(), value)
