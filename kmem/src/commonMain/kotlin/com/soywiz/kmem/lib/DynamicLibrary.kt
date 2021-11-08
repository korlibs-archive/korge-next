package com.soywiz.kmem.lib

import com.soywiz.kmem.*

typealias Int32 = Int
typealias Int64 = Long

expect class NativeInt
expect fun Long.toNativeInt(): NativeInt
expect fun NativeInt.toLongValue(): Long

expect class VoidPtr
expect fun Long.toVoidPtr(): VoidPtr
expect fun VoidPtr.toLongPtr(): Long

expect abstract class NPointed
expect interface Library
expect interface StdCallLibrary : Library
expect class NArena
expect fun NArenaAlloc(): NArena
expect fun NArena.close(): Unit
expect fun NArena.alloc(size: Int): VoidPtr
expect fun VoidPtr.transferBytes(bytes: ByteArray, index: Int, size: Int, write: Boolean, offset: Int = 0)
expect fun VoidPtr.getByte(offset: Int = 0): Byte
expect fun VoidPtr.setByte(value: Byte, offset: Int = 0)

fun VoidPtr.readBytesUpTo(end: Byte = 0, offset: Int = 0, estimatedSize: Int = 1024): ByteArray {
    val bos = ByteArrayBuilder(estimatedSize)
    var n = offset
    while (true) {
        val byte = this.getByte(n)
        if (byte == end) break
        bos.append(byte)
        n++
    }
    return bos.toByteArray()
}
inline fun <T> libMemScope(block: NArena.() -> T): T {
    val arena = NArenaAlloc()
    try {
        return block(arena)
    } finally {
        arena.close()
    }
}

fun VoidPtr.writeBytes(bytes: ByteArray, index: Int = 0, size: Int = bytes.size, offset: Int = 0) {
    transferBytes(bytes, index, size, true, offset)
}

fun VoidPtr.readBytes(bytes: ByteArray, index: Int = 0, size: Int = bytes.size, offset: Int = 0) {
    transferBytes(bytes, index, size, false, offset)
}

fun VoidPtr.readBytes(count: Int, offset: Int = 0): ByteArray =
    ByteArray(count).also { readBytes(it, offset = offset) }


fun VoidPtr.readStringzUtf8(offset: Int = 0, estimatedSize: Int = 1024) = readBytesUpTo(offset = 0).decodeToString()

fun NArena.allocBytes(data: ByteArray): VoidPtr = alloc(data.size).also { it.writeBytes(data) }
fun NArena.allocStringz(str: String): VoidPtr = allocBytes(byteArrayOf(*str.encodeToByteArray(), 0))

annotation class NativeLibrary

interface LibraryCompanion<T : Library> {
    operator fun invoke(): T = TODO("Execute Interface(\"dllname\") instead (even if the symbol doesn't exist)")
}

@Suppress("unused")
inline fun <T> Library.memScoped(block: NArena.() -> T) = libMemScope(block)

@NativeLibrary
interface MyNativeLibrary : Library, StdCallLibrary {
    companion object : LibraryCompanion<MyNativeLibrary> {
        override fun invoke(): MyNativeLibrary = invoke("Kernel32.dll") // This will be resolved by the KSP plugin
    }
    fun Sleep(time: Int32): Unit
    fun GetModuleFileNameA(module: VoidPtr?, name: VoidPtr?, size: Int32): Int32
}
