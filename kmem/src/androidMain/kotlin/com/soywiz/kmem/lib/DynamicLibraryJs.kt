package com.soywiz.kmem.lib

actual typealias NativeInt = Int
actual fun Long.toNativeInt(): NativeInt = this.toInt()
actual fun NativeInt.toLongValue(): Long = this.toLong()

actual typealias VoidPtr = Int
actual fun Long.toVoidPtr(): VoidPtr = this.toInt()
actual fun VoidPtr.toLongPtr(): Long = this.toLong()

actual abstract class NPointed
actual interface Library
actual interface StdCallLibrary : Library
actual class NArena
actual fun NArenaAlloc(): NArena = NArena()
actual fun NArena.close(): Unit = Unit
actual fun NArena.alloc(size: Int): VoidPtr = TODO()
actual fun VoidPtr.transferBytes(bytes: ByteArray, index: Int, size: Int, write: Boolean, offset: Int): Unit = TODO()
actual fun VoidPtr.getByte(offset: Int): Byte = TODO()
actual fun VoidPtr.setByte(value: Byte, offset: Int): Unit = TODO()
