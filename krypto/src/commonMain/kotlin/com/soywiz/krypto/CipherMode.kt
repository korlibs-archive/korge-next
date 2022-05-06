package com.soywiz.krypto

interface CipherMode {
    fun encrypt(data: ByteArray, cipher: Cipher, padding: Padding): ByteArray
    fun decrypt(data: ByteArray, cipher: Cipher, padding: Padding): ByteArray

    companion object
}

object ECB : CipherMode {
    override fun encrypt(data: ByteArray, cipher: Cipher, padding: Padding): ByteArray {
        val pData = Padding.padding(data, cipher.blockSize, padding)
        for (n in pData.indices step 16) cipher.encrypt(pData, n)
        return pData
    }

    override fun decrypt(data: ByteArray, cipher: Cipher, padding: Padding): ByteArray {
        for (n in data.indices step 16) {
            cipher.decrypt(data, n)
        }
        return Padding.removePadding(data, padding)
    }
}

val CipherMode.Companion.ECB: ECB get() = com.soywiz.krypto.ECB
