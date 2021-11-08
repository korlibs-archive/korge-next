package com.soywiz.kdynlib

import com.soywiz.kmem.*
import kotlin.math.*
import kotlin.reflect.*

annotation class NativeLibrary {
    companion object {
        val SUPPORTED get() = NativeLibrarySupported
    }
}

typealias Int32 = Int
typealias Int64 = Long

expect class NativeInt
expect fun Long.toNativeInt(): NativeInt
expect fun NativeInt.toLongValue(): Long

fun Int.toNativeInt(): NativeInt = this.toLong().toNativeInt()
fun NativeInt.toIntValue(): Int = this.toLongValue().toInt()

expect class VoidPtr
expect fun Long.toVoidPtr(): VoidPtr
expect fun VoidPtr.toLongPtr(): Long

val VoidPtrSize: Int get() = NativeIntSize
expect val NativeIntSize: Int
expect val NativeLibrarySupported: Boolean

expect interface Library
expect interface StdCallLibrary : Library
expect class NArena
expect fun NArenaAlloc(): NArena
expect fun NArena.close(): Unit
expect fun NArena.alloc(size: Int): VoidPtr
expect fun VoidPtr.transferBytes(bytes: ByteArray, index: Int, size: Int, write: Boolean, offset: Int = 0)
expect fun VoidPtr.getByte(offset: Int): Byte
expect fun VoidPtr.setByte(offset: Int, value: Byte)
expect fun VoidPtr.getShort(offset: Int): Short
expect fun VoidPtr.setShort(offset: Int, value: Short)
expect fun VoidPtr.getInt(offset: Int): Int
expect fun VoidPtr.setInt(offset: Int, value: Int)
expect fun VoidPtr.getLong(offset: Int): Long
expect fun VoidPtr.setLong(offset: Int, value: Long)

fun VoidPtrNew(value: Long): VoidPtr = value.toVoidPtr()
fun VoidPtrNew(value: Int): VoidPtr = value.toLong().toVoidPtr()

fun VoidPtr.getChar(offset: Int): Char = getShort(offset).toInt().toChar()
fun VoidPtr.setChar(offset: Int, value: Char) = setShort(offset, value.code.toShort())

fun VoidPtr.getFloat(offset: Int): Float = Float.fromBits(getInt(offset))
fun VoidPtr.setFloat(offset: Int, value: Float) = setInt(offset, value.toRawBits())

fun VoidPtr.getDouble(offset: Int): Double = Double.fromBits(getLong(offset))
fun VoidPtr.setDouble(offset: Int, value: Double) = setLong(offset, value.toRawBits())

fun VoidPtr.getVoidPtr(offset: Int): VoidPtr = when (NativeIntSize) {
    4 -> getInt(offset).toLong().toVoidPtr()
    8 -> getLong(offset).toLong().toVoidPtr()
    else -> TODO()
}
fun VoidPtr.setVoidPtr(offset: Int, value: VoidPtr) = when (NativeIntSize) {
    4 -> setInt(offset, value.toLongPtr().toInt())
    8 -> setLong(offset, value.toLongPtr().toLong())
    else -> TODO()
}

fun VoidPtr.getNativeInt(offset: Int): NativeInt = when (NativeIntSize) {
    4 -> getInt(offset).toNativeInt()
    8 -> getLong(offset).toNativeInt()
    else -> TODO()
}
fun VoidPtr.setNativeInt(offset: Int, value: NativeInt) = when (NativeIntSize) {
    4 -> setInt(offset, value.toIntValue())
    8 -> setLong(offset, value.toLongValue())
    else -> TODO()
}

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


fun VoidPtr.readStringzUtf8(offset: Int = 0, estimatedSize: Int = 1024) = readBytesUpTo(offset = offset, estimatedSize = estimatedSize).decodeToString()
fun VoidPtr.readStringzUtf16(offset: Int = 0, estimatedSize: Int = 1024): String {
    val sb = StringBuilder(estimatedSize)
    var n = 0
    while (true) {
        val c = this.getChar(offset + n * 2)
        if (c == '\u0000') break
        sb.append(c)
        n++
    }
    return sb.toString()
}

fun NArena.allocBytes(data: ByteArray): VoidPtr = alloc(data.size).also { it.writeBytes(data) }
fun NArena.allocStringz(str: String): VoidPtr = allocBytes(byteArrayOf(*str.encodeToByteArray(), 0))
fun NArena.allocStringzUtf16(str: String): VoidPtr {
    val ptr = alloc((str.length + 1) * 2)
    for (n in 0 until str.length) ptr.setChar(n * 2, str[n])
    ptr.setChar(str.length * 2, '\u0000')
    return ptr
}

interface LibraryCompanion<T : Library> {
    operator fun invoke(): T = TODO("Execute Interface(\"dllname\") instead (even if the symbol doesn't exist)")
}

@Suppress("unused")
inline fun <T> Library.memScoped(block: NArena.() -> T) = libMemScope(block)

public fun interface DynamicSymbolResolver {
    public fun getSymbol(name: String): VoidPtr?
}
