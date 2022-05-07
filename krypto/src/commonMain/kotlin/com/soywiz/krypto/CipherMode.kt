package com.soywiz.krypto

import com.soywiz.krypto.internal.*
import com.soywiz.krypto.internal.getIV
import com.soywiz.krypto.internal.getInt
import com.soywiz.krypto.internal.setInt

interface CipherMode {
    companion object {
        val ECB: CipherMode get() = CipherModeECB
        val CBC: CipherMode get() = CipherModeCBC
        val PCBC: CipherMode get() = CipherModePCBC
        val CFB: CipherMode get() = CipherModeCFB
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
        val ivWords = getIV(iv, cipher.blockSize)

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
        val ivWords = getIV(iv, cipher.blockSize).toIntArray()

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

private object CipherModePCBC : BaseCipherMode() {
    override fun encrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray {
        val pData = Padding.padding(data, cipher.blockSize, padding)
        val ivWords = getIV(iv, cipher.blockSize).toIntArray()
        val plaintext = IntArray(4)

        var s0 = ivWords[0]
        var s1 = ivWords[1]
        var s2 = ivWords[2]
        var s3 = ivWords[3]

        for (n in pData.indices step cipher.blockSize) {
            plaintext[0] = pData.getInt(n + 0 * 4)
            plaintext[1] = pData.getInt(n + 1 * 4)
            plaintext[2] = pData.getInt(n + 2 * 4)
            plaintext[3] = pData.getInt(n + 3 * 4)

            pData.setInt(n + 0 * 4, plaintext[0] xor s0)
            pData.setInt(n + 1 * 4, plaintext[1] xor s1)
            pData.setInt(n + 2 * 4, plaintext[2] xor s2)
            pData.setInt(n + 3 * 4, plaintext[3] xor s3)

            cipher.encrypt(pData, n, cipher.blockSize)

            s0 = pData.getInt(n + 0 * 4) xor plaintext[0]
            s1 = pData.getInt(n + 1 * 4) xor plaintext[1]
            s2 = pData.getInt(n + 2 * 4) xor plaintext[2]
            s3 = pData.getInt(n + 3 * 4) xor plaintext[3]
        }
        return pData
    }

    override fun decrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray {
        val cdata = data.copyOf()
        val ivWords = getIV(iv, cipher.blockSize).toIntArray()
        val cipherText = IntArray(4)

        var s0 = ivWords[0]
        var s1 = ivWords[1]
        var s2 = ivWords[2]
        var s3 = ivWords[3]

        for (n in cdata.indices step cipher.blockSize) {
            cipherText[0] = cdata.getInt(n + 0 * 4)
            cipherText[1] = cdata.getInt(n + 1 * 4)
            cipherText[2] = cdata.getInt(n + 2 * 4)
            cipherText[3] = cdata.getInt(n + 3 * 4)

            cipher.decrypt(cdata, n, cipher.blockSize)

            cdata.setInt(n + 0 * 4, cdata.getInt(n + 0 * 4) xor s0)
            cdata.setInt(n + 1 * 4, cdata.getInt(n + 1 * 4) xor s1)
            cdata.setInt(n + 2 * 4, cdata.getInt(n + 2 * 4) xor s2)
            cdata.setInt(n + 3 * 4, cdata.getInt(n + 3 * 4) xor s3)

            s0 = cdata.getInt(n + 0 * 4) xor cipherText[0]
            s1 = cdata.getInt(n + 1 * 4) xor cipherText[1]
            s2 = cdata.getInt(n + 2 * 4) xor cipherText[2]
            s3 = cdata.getInt(n + 3 * 4) xor cipherText[3]
        }
        return Padding.removePadding(cdata, padding)
    }
}

private object CipherModeCFB : BaseCipherMode() {
    override fun encrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray {
        var pData = Padding.padding(data, cipher.blockSize, padding)
        val dataSize = pData.size
        if (dataSize % cipher.blockSize != 0) {
            pData = Padding.padding(pData, cipher.blockSize, CipherPadding.ZeroPadding)
        }

        val words = pData.toIntArray()
        val wordsLength = words.size
        val ivWords = getIV(iv, cipher.blockSize).toIntArray()
        val cipherText = IntArray(cipher.blockSizeD4)

        cipher.encryptBlock(ivWords, 0)
        arraycopy(ivWords, 0, cipherText, 0, cipher.blockSizeD4)
        for (n in 0 until wordsLength step cipher.blockSizeD4) {
            cipherText[0] = cipherText[0] xor words[n + 0]
            cipherText[1] = cipherText[1] xor words[n + 1]
            cipherText[2] = cipherText[2] xor words[n + 2]
            cipherText[3] = cipherText[3] xor words[n + 3]

            arraycopy(cipherText, 0, words, n, cipher.blockSizeD4)
            if (n + 4 < wordsLength) {
                cipher.encryptBlock(cipherText, 0)
            }
        }
        val wordsData = words.toByteArray()
        var result = wordsData
        if (dataSize < wordsData.size) {
            result = ByteArray(dataSize)
            arraycopy(wordsData, 0, result, 0, result.size)
        }
        return result
    }

    override fun decrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray {
        val dataSize = data.size
        var pData = data
        if (dataSize % cipher.blockSize != 0) {
            pData = Padding.padding(data, cipher.blockSize, CipherPadding.ZeroPadding)
        }

        val blockSizeD4 = cipher.blockSizeD4
        val words = pData.toIntArray()
        val wordsLength = words.size
        val ivWords = getIV(iv, cipher.blockSize).toIntArray()
        val plainText = IntArray(blockSizeD4)
        val cipherText = IntArray(blockSizeD4)

        cipher.encryptBlock(ivWords, 0)
        arraycopy(ivWords, 0, cipherText, 0, blockSizeD4)
        for (n in 0 until wordsLength step blockSizeD4) {
            plainText[0] = cipherText[0] xor words[n + 0]
            plainText[1] = cipherText[1] xor words[n + 1]
            plainText[2] = cipherText[2] xor words[n + 2]
            plainText[3] = cipherText[3] xor words[n + 3]

            arraycopy(words, n, cipherText, 0, blockSizeD4)
            arraycopy(plainText, 0, words, n, blockSizeD4)
            if (n + 4 < wordsLength) {
                cipher.encryptBlock(cipherText, 0)
            }
        }
        val wordsData = words.toByteArray()
        var result = wordsData
        if (dataSize < wordsData.size) {
            result = ByteArray(dataSize)
            arraycopy(wordsData, 0, result, 0, result.size)
        }
        return Padding.removePadding(result, padding)
    }
}
