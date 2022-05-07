package com.soywiz.krypto

import com.soywiz.krypto.internal.getInt
import com.soywiz.krypto.internal.toByteArray

interface Cipher {
    val blockSize: Int
    fun encrypt(data: ByteArray, offset: Int = 0, len: Int = data.size - offset)
    fun decrypt(data: ByteArray, offset: Int = 0, len: Int = data.size - offset)
}

val Cipher.blockSizeD4: Int get() = blockSize / 4
fun Cipher.encryptBlock(data: IntArray, offset: Int) {
    val tdata = data.toByteArray()
    encrypt(tdata, offset * 4, blockSize)
    for (n in 0 until blockSize / 4) data[n] = tdata.getInt(n * 4)
}
fun Cipher.decryptBlock(data: IntArray, offset: Int) {
    val tdata = data.toByteArray()
    decrypt(tdata, offset * 4, blockSize)
    for (n in 0 until blockSize / 4) data[n] = tdata.getInt(n * 4)
}

class CipherWithModeAndPadding(val cipher: Cipher, val mode: CipherMode, val padding: CipherPadding, val iv: ByteArray? = null) {
    fun encrypt(data: ByteArray, offset: Int = 0, len: Int = data.size - offset): ByteArray =
        mode.encrypt(data.copyOfRange(offset, offset + len), cipher, padding, iv)

    fun decrypt(data: ByteArray, offset: Int = 0, len: Int = data.size - offset): ByteArray =
        mode.decrypt(data.copyOfRange(offset, offset + len), cipher, padding, iv)
}

fun Cipher.with(mode: CipherMode, padding: CipherPadding, iv: ByteArray? = null): CipherWithModeAndPadding = CipherWithModeAndPadding(this, mode, padding, iv)
operator fun Cipher.get(mode: CipherMode, padding: CipherPadding, iv: ByteArray? = null): CipherWithModeAndPadding = with(mode, padding, iv)
