package com.soywiz.kmem

expect class FastByteTransfer() {
    inline operator fun get(index: Int): Byte
    inline operator fun set(index: Int, value: Byte)
    inline fun use(array: ByteArray, block: (FastByteTransfer) -> Unit)
}

expect class FastShortTransfer() {
    inline operator fun get(index: Int): Short
    inline operator fun set(index: Int, value: Short)
    inline fun use(array: ShortArray, block: (FastShortTransfer) -> Unit)
}

expect class FastIntTransfer() {
    inline operator fun get(index: Int): Int
    inline operator fun set(index: Int, value: Int)
    inline fun use(array: IntArray, block: (FastIntTransfer) -> Unit)
}

expect class FastFloatTransfer() {
    inline operator fun get(index: Int): Float
    inline operator fun set(index: Int, value: Float)
    inline fun use(array: FloatArray, block: (FastFloatTransfer) -> Unit)
}

expect class FastFBufferTransfer() {
    inline fun getAlignedInt32(index: Int): Int
    inline fun setAlignedInt32(index: Int, value: Int)

    inline fun getAlignedFloat32(index: Int): Float
    inline fun setAlignedFloat32(index: Int, value: Float)

    inline fun use(array: FBuffer)
    inline fun unuse()
}

inline fun FastFBufferTransfer.use(array: FBuffer, block: (FastFBufferTransfer) -> Unit) {
    use(array)
    try {
        block(this)
    } finally {
        unuse()
    }
}
