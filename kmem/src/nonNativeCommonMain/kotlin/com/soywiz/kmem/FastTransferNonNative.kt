package com.soywiz.kmem

@PublishedApi internal val EmptyByteArray = ByteArray(1)
@PublishedApi internal val EmptyShortArray = ShortArray(1)
@PublishedApi internal val EmptyIntArray = IntArray(1)
@PublishedApi internal val EmptyFloatArray = FloatArray(1)
@PublishedApi internal val EmptyFBuffer = FBuffer(8)

actual class FastByteTransfer actual constructor() {
    @PublishedApi internal var ptr: ByteArray = EmptyByteArray

    actual inline operator fun get(index: Int): Byte = ptr[index]
    actual inline operator fun set(index: Int, value: Byte) { ptr[index] = value }
    actual inline fun use(array: ByteArray, block: (FastByteTransfer) -> Unit) {
        try {
            ptr = array
            block(this)
        } finally {
            ptr = EmptyByteArray
        }
    }
}

actual class FastShortTransfer actual constructor() {
    @PublishedApi internal var ptr: ShortArray = EmptyShortArray

    actual inline operator fun get(index: Int): Short = ptr[index]
    actual inline operator fun set(index: Int, value: Short) { ptr[index] = value }
    actual inline fun use(array: ShortArray, block: (FastShortTransfer) -> Unit) {
        try {
            ptr = array
            block(this)
        } finally {
            ptr = EmptyShortArray
        }
    }
}

actual class FastIntTransfer actual constructor() {
    @PublishedApi internal var ptr: IntArray = EmptyIntArray

    actual inline operator fun get(index: Int): Int = ptr[index]
    actual inline operator fun set(index: Int, value: Int) { ptr[index] = value }
    actual inline fun use(array: IntArray, block: (FastIntTransfer) -> Unit) {
        try {
            ptr = array
            block(this)
        } finally {
            ptr = EmptyIntArray
        }
    }
}

actual class FastFloatTransfer actual constructor() {
    @PublishedApi internal var ptr: FloatArray = EmptyFloatArray

    actual inline operator fun get(index: Int): Float = ptr[index]
    actual inline operator fun set(index: Int, value: Float) { ptr[index] = value }
    actual inline fun use(array: FloatArray, block: (FastFloatTransfer) -> Unit) {
        try {
            ptr = array
            block(this)
        } finally {
            ptr = EmptyFloatArray
        }
    }
}

actual class FastFBufferTransfer actual constructor() {
    @PublishedApi internal var ptr: FBuffer = EmptyFBuffer
    @PublishedApi internal var i32: Int32Buffer = EmptyFBuffer.i32
    @PublishedApi internal var f32: Float32Buffer = EmptyFBuffer.f32

    actual inline fun getAlignedInt32(index: Int): Int = i32[index]
    actual inline fun setAlignedInt32(index: Int, value: Int) { i32[index] = value }

    actual inline fun getAlignedFloat32(index: Int): Float = f32[index]
    actual inline fun setAlignedFloat32(index: Int, value: Float) { f32[index] = value }

    actual inline fun use(array: FBuffer) {
        ptr = array
        i32 = array.i32
        f32 = array.f32
    }

    actual inline fun unuse() {
        ptr = EmptyFBuffer
        i32 = EmptyFBuffer.i32
        f32 = EmptyFBuffer.f32
    }
}
