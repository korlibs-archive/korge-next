package com.soywiz.krypto

import com.soywiz.krypto.internal.*
import com.soywiz.krypto.internal.getIV
import com.soywiz.krypto.internal.getInt
import com.soywiz.krypto.internal.setInt

interface CipherMode {
    companion object {
        val ECB: CipherMode get() = CipherModeECB
        val CBC: CipherMode get() = CipherModeCBC
    }

    fun encrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray
    fun decrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray
}

private abstract class BaseCipherMode : CipherMode {
}

private object CipherModeECB : BaseCipherMode() {
    override fun encrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray {
        val pData = Padding.padding(data, cipher.blockSize, padding)
        cipher.encrypt(pData, 0, pData.size)
        return pData
    }

    override fun decrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray {
        cipher.decrypt(data, 0, data.size)
        return Padding.removePadding(data, padding)
    }
}

private object CipherModeCBC : BaseCipherMode() {
    override fun encrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray {
        val pData = padding.add(data, cipher.blockSize)
        val ivWords = getIV(iv)

        if (pData.size % cipher.blockSize != 0) {
            throw IllegalArgumentException("Data is not multiple of ${cipher.blockSize}, and padding was set to ${CipherPadding.NoPadding}")
        }

        var s0 = ivWords.getInt(0 * 4)
        var s1 = ivWords.getInt(1 * 4)
        var s2 = ivWords.getInt(2 * 4)
        var s3 = ivWords.getInt(3 * 4)

        for (n in pData.indices step cipher.blockSize) {
            pData.setInt(n + 0 * 4, pData.getInt(n + 0 * 4) xor s0)
            pData.setInt(n + 1 * 4, pData.getInt(n + 1 * 4) xor s1)
            pData.setInt(n + 2 * 4, pData.getInt(n + 2 * 4) xor s2)
            pData.setInt(n + 3 * 4, pData.getInt(n + 3 * 4) xor s3)

            cipher.encrypt(pData, n, cipher.blockSize)

            s0 = pData.getInt(n + 0 * 4)
            s1 = pData.getInt(n + 1 * 4)
            s2 = pData.getInt(n + 2 * 4)
            s3 = pData.getInt(n + 3 * 4)
        }
        return pData
    }

    override fun decrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray {
        val cdata = data.copyOf()
        val ivWords = getIV(iv).toIntArray()

        var s0 = ivWords[0]
        var s1 = ivWords[1]
        var s2 = ivWords[2]
        var s3 = ivWords[3]

        for (n in cdata.indices step cipher.blockSize) {
            val t0 = cdata.getInt(n + 0 * 4)
            val t1 = cdata.getInt(n + 1 * 4)
            val t2 = cdata.getInt(n + 2 * 4)
            val t3 = cdata.getInt(n + 3 * 4)

            cipher.decrypt(cdata, n, cipher.blockSize)

            cdata.setInt(n + 0 * 4, cdata.getInt(n + 0 * 4) xor s0)
            cdata.setInt(n + 1 * 4, cdata.getInt(n + 1 * 4) xor s1)
            cdata.setInt(n + 2 * 4, cdata.getInt(n + 2 * 4) xor s2)
            cdata.setInt(n + 3 * 4, cdata.getInt(n + 3 * 4) xor s3)

            s0 = t0
            s1 = t1
            s2 = t2
            s3 = t3
        }
        return Padding.removePadding(cdata, padding)
    }
}
