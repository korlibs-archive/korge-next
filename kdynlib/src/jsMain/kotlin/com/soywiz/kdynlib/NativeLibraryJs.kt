package com.soywiz.kdynlib

actual typealias NativeInt = Int
actual fun Long.toNativeInt(): NativeInt = this.toInt()
actual fun NativeInt.toLongValue(): Long = this.toLong()

actual typealias VoidPtr = Int
actual fun Long.toVoidPtr(): VoidPtr = this.toInt()
actual fun VoidPtr.toLongPtr(): Long = this.toLong()

actual val NativeIntSize: Int = Int.SIZE_BYTES
actual val NativeLibrarySupported: Boolean = false

actual interface Library
actual interface StdCallLibrary : Library
actual class NArena
actual fun NArenaAlloc(): NArena = NArena()
actual fun NArena.close(): Unit = Unit
actual fun NArena.alloc(size: Int): VoidPtr = TODO()
actual fun VoidPtr.transferBytes(bytes: ByteArray, index: Int, size: Int, write: Boolean, offset: Int): Unit = TODO()
actual fun VoidPtr.getByte(offset: Int): Byte = TODO()
actual fun VoidPtr.setByte(offset: Int, value: Byte): Unit = TODO()
actual fun VoidPtr.getShort(offset: Int): Short = TODO()
actual fun VoidPtr.setShort(offset: Int, value: Short): Unit = TODO()
actual fun VoidPtr.getInt(offset: Int): Int = TODO()
actual fun VoidPtr.setInt(offset: Int, value: Int): Unit = TODO()
actual fun VoidPtr.getLong(offset: Int): Long = TODO()
actual fun VoidPtr.setLong(offset: Int, value: Long): Unit = TODO()
