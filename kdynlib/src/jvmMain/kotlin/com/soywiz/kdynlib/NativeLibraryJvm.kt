package com.soywiz.kdynlib

import com.sun.jna.*
import kotlin.reflect.*

actual typealias NativeInt = Pointer
actual fun Long.toNativeInt(): NativeInt = Pointer(this)
actual fun NativeInt.toLongValue(): Long = Pointer.nativeValue(this)

actual typealias VoidPtr = Pointer
actual fun Long.toVoidPtr(): VoidPtr = Pointer(this)
actual fun VoidPtr.toLongPtr(): Long = Pointer.nativeValue(this)

actual val NativeIntSize: Int = Native.POINTER_SIZE
actual val NativeLibrarySupported: Boolean = true

actual typealias Library = com.sun.jna.Library
actual typealias StdCallLibrary = com.sun.jna.win32.StdCallLibrary

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
actual fun NArena.alloc(size: Int): VoidPtr = Memory(size.toLong()).also {
    it.clear()
    this.add(it)
}
actual fun VoidPtr.transferBytes(bytes: ByteArray, index: Int, size: Int, write: Boolean, offset: Int) {
    if (write) {
        this.write(offset.toLong(), bytes, index, size)
    } else {
        this.read(offset.toLong(), bytes, index, size)
    }
}
actual fun VoidPtr.getByte(offset: Int): Byte = this.getByte(offset.toLong())
actual fun VoidPtr.setByte(offset: Int, value: Byte) = this.setByte(offset.toLong(), value)

actual fun VoidPtr.getShort(offset: Int): Short = this.getShort(offset.toLong())
actual fun VoidPtr.setShort(offset: Int, value: Short) = this.setShort(offset.toLong(), value)

actual fun VoidPtr.getInt(offset: Int): Int = this.getInt(offset.toLong())
actual fun VoidPtr.setInt(offset: Int, value: Int) {
    //println("setInt")
    this.setInt(offset.toLong(), value)
}

actual fun VoidPtr.getLong(offset: Int): Long = this.getLong(offset.toLong())
actual fun VoidPtr.setLong(offset: Int, value: Long) {
    //println("setLong")
    this.setLong(offset.toLong(), value)
}

object NativeLibraryJvm {
    @JvmStatic fun register(clazz: KClass<*>, name: String) {
        //println("clazz=$clazz, ${clazz::class.java}")
        Native.register(clazz.java, name)
    }
}
