package com.soywiz.krypto

import com.soywiz.krypto.internal.*

// @TODO: This file could be simplified a lot.
// @TODO: Instead of using words in some, we can use bytes and vector operations
// @TODO: Then we can abstract base functionality so the core function of each mode keeps simple and DRY

/**
 * Symmetric Cipher Mode
 */
interface CipherMode {
    companion object {
        val ECB: CipherMode get() = CipherModeECB
        val CBC: CipherMode get() = CipherModeCBC
        val PCBC: CipherMode get() = CipherModePCBC
        val CFB: CipherMode get() = CipherModeCFB
        val OFB: CipherMode get() = CipherModeOFB
        val CTR: CipherMode get() = CipherModeCTR
    }

    val name: String
    fun encrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray
    fun decrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray
}

private abstract class CipherModeBase(override val name: String) : CipherMode {
    override fun toString(): String = name
}

private abstract class CipherModeCore(name: String) : CipherModeBase(name) {
    final override fun encrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray {
        val ivb = getIV(iv, cipher.blockSize)
        val pData = padding.add(data, cipher.blockSize)
        coreEncrypt(pData, cipher, ivb)
        return pData
    }

    final override fun decrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray {
        val ivb = getIV(iv, cipher.blockSize)
        val pData = data.copyOf()
        coreDecrypt(pData, cipher, ivb)
        return padding.remove(pData)
    }

    protected abstract fun coreEncrypt(pData: ByteArray, cipher: Cipher, ivb: ByteArray)
    protected abstract fun coreDecrypt(pData: ByteArray, cipher: Cipher, ivb: ByteArray)
}

private abstract class CipherModeCoreDE(name: String) : CipherModeCore(name) {
    final override fun coreEncrypt(pData: ByteArray, cipher: Cipher, ivb: ByteArray) {
        core(pData, cipher, ivb)
    }

    final override fun coreDecrypt(pData: ByteArray, cipher: Cipher, ivb: ByteArray) {
        core(pData, cipher, ivb)
    }

    protected abstract fun core(pData: ByteArray, cipher: Cipher, ivb: ByteArray)
}

private object CipherModeECB : CipherModeBase("ECB") {
    override fun encrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray {
        val pData = padding.add(data, cipher.blockSize)
        cipher.encrypt(pData, 0, pData.size)
        return pData
    }

    override fun decrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray {
        cipher.decrypt(data, 0, data.size)
        return padding.remove(data)
    }
}

private object CipherModeCBC : CipherModeCore("CBC") {
    override fun coreEncrypt(pData: ByteArray, cipher: Cipher, ivb: ByteArray) {
        for (n in pData.indices step cipher.blockSize) {
            arrayxor(pData, n, ivb)
            cipher.encrypt(pData, n, cipher.blockSize)
            arraycopy(pData, n, ivb, 0, cipher.blockSize)
        }
    }

    override fun coreDecrypt(pData: ByteArray, cipher: Cipher, ivb: ByteArray) {
        val blockSize = cipher.blockSize
        val tempBytes = ByteArray(blockSize)

        for (n in pData.indices step blockSize) {
            arraycopy(pData, n, tempBytes, 0, blockSize)
            cipher.decrypt(pData, n, blockSize)
            arrayxor(pData, n, ivb)
            arraycopy(tempBytes, 0, ivb, 0, blockSize)
        }
    }
}

private object CipherModePCBC : CipherModeBase("PCBC") {
    override fun encrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray {
        val pData = padding.add(data, cipher.blockSize)
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
        return padding.remove(cdata)
    }
}

private object CipherModeCFB : CipherModeBase("CFB") {
    override fun encrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray {
        var pData = padding.add(data, cipher.blockSize)
        val dataSize = pData.size
        if (dataSize % cipher.blockSize != 0) {
            pData = Padding.padding(pData, cipher.blockSize, CipherPadding.ZeroPadding)
        }

        val blockSizeD4 = cipher.blockSizeD4
        val words = pData.toIntArray()
        val wordsLength = words.size
        val ivWords = getIV(iv, cipher.blockSize).toIntArray()
        val cipherText = IntArray(blockSizeD4)

        cipher.encryptBlock(ivWords, 0)
        arraycopy(ivWords, 0, cipherText, 0, blockSizeD4)
        for (n in 0 until wordsLength step blockSizeD4) {
            cipherText[0] = cipherText[0] xor words[n + 0]
            cipherText[1] = cipherText[1] xor words[n + 1]
            cipherText[2] = cipherText[2] xor words[n + 2]
            cipherText[3] = cipherText[3] xor words[n + 3]

            arraycopy(cipherText, 0, words, n, blockSizeD4)
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

        // @TODO: Is this correct?
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
                // @TODO: Is this correct?
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

private object CipherModeOFB : CipherModeCoreDE("OFB") {
    override fun core(pData: ByteArray, cipher: Cipher, ivb: ByteArray) {
        val blockSize = cipher.blockSize
        val cipherText = ByteArray(blockSize)
        cipher.encrypt(ivb)
        for (n in pData.indices step blockSize) {
            arraycopy(pData, n, cipherText, 0, blockSize)
            arrayxor(cipherText, 0, ivb)
            arraycopy(cipherText, 0, pData, n, blockSize)
            if (n + blockSize < pData.size) {
                cipher.encrypt(ivb)
            }
        }
    }
}

// https://github.com/Jens-G/haxe-crypto/blob/dcf6d994773abba80b0720b2f5e9d5b26de0dbe3/src/com/hurlant/crypto/symmetric/mode/CTRMode.hx
private object CipherModeCTR : CipherModeCoreDE("CTR") {
    override fun core(pData: ByteArray, cipher: Cipher, ivb: ByteArray) {
        val blockSize = cipher.blockSize
        val temp = ByteArray(ivb.size)
        for (n in pData.indices step blockSize) {
            arraycopy(ivb, 0, temp, 0, temp.size)
            cipher.encrypt(temp, 0, blockSize)
            arrayxor(pData, n, temp)
            updateIV(ivb, blockSize)
        }
    }

    private fun updateIV(ivb: ByteArray, blockSize: Int) {
        for (j in blockSize - 1 downTo 0) {
            ivb[j]++
            if (ivb[j].toInt() != 0) break
        }
    }
}
