package com.soywiz.krypto

interface Cipher {
    val blockSize: Int
    fun encrypt(data: ByteArray, offset: Int = 0, len: Int = data.size - offset)
    fun decrypt(data: ByteArray, offset: Int = 0, len: Int = data.size - offset)
}

class CipherWithModeAndPadding(val cipher: Cipher, val mode: CipherMode, val padding: CipherPadding) {
    fun encrypt(data: ByteArray, offset: Int = 0, len: Int = data.size - offset): ByteArray =
        mode.encrypt(data.copyOfRange(offset, offset + len), cipher, padding)

    fun decrypt(data: ByteArray, offset: Int = 0, len: Int = data.size - offset): ByteArray =
        mode.decrypt(data.copyOfRange(offset, offset + len), cipher, padding)
}

fun Cipher.with(mode: CipherMode, padding: CipherPadding): CipherWithModeAndPadding = CipherWithModeAndPadding(this, mode, padding)
operator fun Cipher.get(mode: CipherMode, padding: CipherPadding): CipherWithModeAndPadding = with(mode, padding)
