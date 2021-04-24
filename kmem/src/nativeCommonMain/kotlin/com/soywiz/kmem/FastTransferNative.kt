package com.soywiz.kmem

import kotlinx.cinterop.*

@PublishedApi internal val EmptyBytePtr = ByteArray(1).pin().addressOf(0)
@PublishedApi internal val EmptyShortPtr = ShortArray(1).pin().addressOf(0)
@PublishedApi internal val EmptyIntPtr = IntArray(1).pin().addressOf(0)
@PublishedApi internal val EmptyFloatPtr = FloatArray(1).pin().addressOf(0)

actual class FastByteTransfer actual constructor() {
    @PublishedApi internal var ptr: CPointer<ByteVar> = EmptyBytePtr

    actual inline operator fun get(index: Int): Byte = ptr[index]
    actual inline operator fun set(index: Int, value: Byte) { ptr[index] = value }
    actual inline fun use(array: ByteArray, block: (FastByteTransfer) -> Unit) {
        array.usePinned { pin ->
            ptr = pin.startAddressOf
            try {
                block(this)
            } finally {
                ptr = EmptyBytePtr
            }
        }
    }
}

actual class FastShortTransfer actual constructor() {
    @PublishedApi internal var ptr: CPointer<ShortVar> = EmptyShortPtr

    actual inline operator fun get(index: Int): Short = ptr[index]
    actual inline operator fun set(index: Int, value: Short) { ptr[index] = value }
    actual inline fun use(array: ShortArray, block: (FastShortTransfer) -> Unit) {
        array.usePinned { pin ->
            ptr = pin.startAddressOf
            try {
                block(this)
            } finally {
                ptr = EmptyShortPtr
            }
        }
    }
}

actual class FastIntTransfer actual constructor() {
    @PublishedApi internal var ptr: CPointer<IntVar> = EmptyIntPtr

    actual inline operator fun get(index: Int): Int = ptr[index]
    actual inline operator fun set(index: Int, value: Int) { ptr[index] = value }
    actual inline fun use(array: IntArray, block: (FastIntTransfer) -> Unit) {
        array.usePinned { pin ->
            ptr = pin.startAddressOf
            try {
                block(this)
            } finally {
                ptr = EmptyIntPtr
            }
        }
    }
}

actual class FastFloatTransfer actual constructor() {
    @PublishedApi internal var ptr: CPointer<FloatVar> = EmptyFloatPtr

    actual inline operator fun get(index: Int): Float = ptr[index]
    actual inline operator fun set(index: Int, value: Float) { ptr[index] = value }
    actual inline fun use(array: FloatArray, block: (FastFloatTransfer) -> Unit) {
        array.usePinned { pin ->
            ptr = pin.startAddressOf
            try {
                block(this)
            } finally {
                ptr = EmptyFloatPtr
            }
        }
    }
}

actual class FastFBufferTransfer actual constructor() {
    @PublishedApi internal var pin: Pinned<ByteArray>? = null
    @PublishedApi internal var f32: CPointer<FloatVar> = EmptyFloatPtr
    @PublishedApi internal var i32: CPointer<IntVar> = EmptyIntPtr

    actual inline fun getAlignedInt32(index: Int): Int = i32[index]
    actual inline fun setAlignedInt32(index: Int, value: Int) { i32[index] = value }

    actual inline fun getAlignedFloat32(index: Int): Float = f32[index]
    actual inline fun setAlignedFloat32(index: Int, value: Float) { f32[index] = value }

    actual inline fun use(array: FBuffer) {
        val pin = array.mem.data.pin()
        this.pin = pin
        f32 = pin.startAddressOf.reinterpret<FloatVar>()
        i32 = pin.startAddressOf.reinterpret<IntVar>()
    }

    actual inline fun unuse() {
        pin?.unpin()
        pin = null
        f32 = EmptyFloatPtr
        i32 = EmptyIntPtr
    }
}
