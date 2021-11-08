package com.soywiz.kmem.lib

import com.soywiz.kmem.*
import kotlin.math.*
import kotlin.reflect.*

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

expect val NativeIntSize: Int

expect abstract class NPointed
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


fun VoidPtr.readStringzUtf8(offset: Int = 0, estimatedSize: Int = 1024) = readBytesUpTo(offset = 0).decodeToString()

fun NArena.allocBytes(data: ByteArray): VoidPtr = alloc(data.size).also { it.writeBytes(data) }
fun NArena.allocStringz(str: String): VoidPtr = allocBytes(byteArrayOf(*str.encodeToByteArray(), 0))

annotation class NativeLibrary

interface LibraryCompanion<T : Library> {
    operator fun invoke(): T = TODO("Execute Interface(\"dllname\") instead (even if the symbol doesn't exist)")
}

@Suppress("unused")
inline fun <T> Library.memScoped(block: NArena.() -> T) = libMemScope(block)

interface LibStruct {
    val ptr: VoidPtr
}

abstract class LibStructDesc<T : LibStruct>(val gen: (VoidPtr) -> T) {
    inline class ByteDef(val offset: Int) {
        inline operator fun getValue(struct: LibStruct, property: KProperty<*>): Byte = struct.ptr.getByte(offset)
        inline operator fun setValue(struct: LibStruct, property: KProperty<*>, value: Byte) = struct.ptr.setByte(offset, value)
    }
    inline class ShortDef(val offset: Int) {
        inline operator fun getValue(struct: LibStruct, property: KProperty<*>): Short = struct.ptr.getShort(offset)
        inline operator fun setValue(struct: LibStruct, property: KProperty<*>, value: Short) = struct.ptr.setShort(offset, value)
    }
    inline class IntDef(val offset: Int) {
        inline operator fun getValue(struct: LibStruct, property: KProperty<*>): Int = struct.ptr.getInt(offset)
        inline operator fun setValue(struct: LibStruct, property: KProperty<*>, value: Int) = struct.ptr.setInt(offset, value)
    }
    inline class LongDef(val offset: Int) {
        inline operator fun getValue(struct: LibStruct, property: KProperty<*>): Long = struct.ptr.getLong(offset)
        inline operator fun setValue(struct: LibStruct, property: KProperty<*>, value: Long) = struct.ptr.setLong(offset, value)
    }
    inline class FloatDef(val offset: Int) {
        inline operator fun getValue(struct: LibStruct, property: KProperty<*>): Float = struct.ptr.getFloat(offset)
        inline operator fun setValue(struct: LibStruct, property: KProperty<*>, value: Float) = struct.ptr.setFloat(offset, value)
    }
    inline class DoubleDef(val offset: Int) {
        inline operator fun getValue(struct: LibStruct, property: KProperty<*>): Double = struct.ptr.getDouble(offset)
        inline operator fun setValue(struct: LibStruct, property: KProperty<*>, value: Double) = struct.ptr.setDouble(offset, value)
    }
    inline class VoidPtrDef(val offset: Int) {
        inline operator fun getValue(struct: LibStruct, property: KProperty<*>): VoidPtr = struct.ptr.getVoidPtr(offset)
        inline operator fun setValue(struct: LibStruct, property: KProperty<*>, value: VoidPtr) = struct.ptr.setVoidPtr(offset, value)
    }
    inline class NativeIntDef(val offset: Int) {
        inline operator fun getValue(struct: LibStruct, property: KProperty<*>): NativeInt = struct.ptr.getNativeInt(offset)
        inline operator fun setValue(struct: LibStruct, property: KProperty<*>, value: NativeInt) {
            struct.ptr.setNativeInt(offset, value)
        }
    }
    class BytesFixedDef(val offset: Int, val size: Int) {
        inline operator fun getValue(struct: LibStruct, property: KProperty<*>): ByteArray = struct.ptr.readBytes(size, offset = offset)
        inline operator fun setValue(struct: LibStruct, property: KProperty<*>, value: ByteArray) = struct.ptr.writeBytes(value, 0, size, offset = offset)
    }
    class StructRefDef<T : LibStruct>(val desc: LibStructDesc<T>, val offset: Int) {
        inline operator fun getValue(struct: LibStruct, property: KProperty<*>): T = desc.gen((struct.ptr.toLongPtr() + offset).toVoidPtr())
        inline operator fun setValue(struct: LibStruct, property: KProperty<*>, value: T) {
            Unit
        }
    }
    private var offset = 0
    var maxAlign = 1; private set
    private fun offset(size: Int, align: Int = size): Int {
        maxAlign = max(maxAlign, align)
        return offset.nextAlignedTo(align).also { offset += size }
    }
    fun byte(align: Int = 1) = ByteDef(offset(1, align))
    fun short(align: Int = 2) = ShortDef(offset(2, align))
    fun int(align: Int = 4) = IntDef(offset(4, align))
    fun long(align: Int = 8) = LongDef(offset(8, align))
    fun float(align: Int = 4) = FloatDef(offset(4, align))
    fun double(align: Int = 8) = DoubleDef(offset(8, align))
    fun voidPtr(align: Int = NativeIntSize) = VoidPtrDef(offset(NativeIntSize, align))
    fun nativeInt(align: Int = NativeIntSize) = NativeIntDef(offset(NativeIntSize, align))
    fun bytesFixed(size: Int, align: Int = 1) = BytesFixedDef(offset(size, align), size)
    fun <T : LibStruct> structRef(type: LibStructDesc<T>) = StructRefDef(type, offset(type.size, type.maxAlign))
    val size get() = offset.nextAlignedTo(maxAlign)
}

