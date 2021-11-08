package com.soywiz.kdynlib

import com.soywiz.kmem.*
import kotlin.math.*
import kotlin.reflect.*

interface NativeStruct {
    val ptr: VoidPtr
}

abstract class NativeStructDesc<T : NativeStruct>(val gen: (VoidPtr) -> T) {
    inline class ByteDef(val offset: Int) {
        inline operator fun getValue(struct: NativeStruct, property: KProperty<*>): Byte = struct.ptr.getByte(offset)
        inline operator fun setValue(struct: NativeStruct, property: KProperty<*>, value: Byte) = struct.ptr.setByte(offset, value)
    }
    inline class ShortDef(val offset: Int) {
        inline operator fun getValue(struct: NativeStruct, property: KProperty<*>): Short = struct.ptr.getShort(offset)
        inline operator fun setValue(struct: NativeStruct, property: KProperty<*>, value: Short) = struct.ptr.setShort(offset, value)
    }
    inline class IntDef(val offset: Int) {
        inline operator fun getValue(struct: NativeStruct, property: KProperty<*>): Int = struct.ptr.getInt(offset)
        inline operator fun setValue(struct: NativeStruct, property: KProperty<*>, value: Int) = struct.ptr.setInt(offset, value)
    }
    inline class LongDef(val offset: Int) {
        inline operator fun getValue(struct: NativeStruct, property: KProperty<*>): Long = struct.ptr.getLong(offset)
        inline operator fun setValue(struct: NativeStruct, property: KProperty<*>, value: Long) = struct.ptr.setLong(offset, value)
    }
    inline class FloatDef(val offset: Int) {
        inline operator fun getValue(struct: NativeStruct, property: KProperty<*>): Float = struct.ptr.getFloat(offset)
        inline operator fun setValue(struct: NativeStruct, property: KProperty<*>, value: Float) = struct.ptr.setFloat(offset, value)
    }
    inline class DoubleDef(val offset: Int) {
        inline operator fun getValue(struct: NativeStruct, property: KProperty<*>): Double = struct.ptr.getDouble(offset)
        inline operator fun setValue(struct: NativeStruct, property: KProperty<*>, value: Double) = struct.ptr.setDouble(offset, value)
    }
    inline class VoidPtrDef(val offset: Int) {
        inline operator fun getValue(struct: NativeStruct, property: KProperty<*>): VoidPtr = struct.ptr.getVoidPtr(offset)
        inline operator fun setValue(struct: NativeStruct, property: KProperty<*>, value: VoidPtr) = struct.ptr.setVoidPtr(offset, value)
    }
    inline class NativeIntDef(val offset: Int) {
        inline operator fun getValue(struct: NativeStruct, property: KProperty<*>): NativeInt = struct.ptr.getNativeInt(offset)
        inline operator fun setValue(struct: NativeStruct, property: KProperty<*>, value: NativeInt) {
            struct.ptr.setNativeInt(offset, value)
        }
    }
    class BytesFixedDef(val offset: Int, val size: Int) {
        inline operator fun getValue(struct: NativeStruct, property: KProperty<*>): ByteArray = struct.ptr.readBytes(size, offset = offset)
        inline operator fun setValue(struct: NativeStruct, property: KProperty<*>, value: ByteArray) = struct.ptr.writeBytes(value, 0, size, offset = offset)
    }
    class StructRefDef<T : NativeStruct>(val desc: NativeStructDesc<T>, val offset: Int) {
        inline operator fun getValue(struct: NativeStruct, property: KProperty<*>): T = desc.gen((struct.ptr.toLongPtr() + offset).toVoidPtr())
        inline operator fun setValue(struct: NativeStruct, property: KProperty<*>, value: T) {
            Unit
        }
    }
    protected var offset = 0
    var maxAlign = 1; protected set
    protected open fun offset(size: Int, align: Int = size): Int {
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
    fun <T : NativeStruct> structRef(type: NativeStructDesc<T>) = StructRefDef(type, offset(type.size, type.maxAlign))
    val size get() = offset.nextAlignedTo(maxAlign)
}

abstract class NativeUnionDesc<T : NativeStruct>(gen: (VoidPtr) -> T) : NativeStructDesc<T>(gen) {
    override fun offset(size: Int, align: Int): Int {
        offset = max(offset, size)
        maxAlign = max(maxAlign, align)
        return 0
    }
}
