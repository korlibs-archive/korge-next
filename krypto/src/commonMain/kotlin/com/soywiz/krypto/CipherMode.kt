package com.soywiz.krypto

import com.soywiz.krypto.internal.arraycopy

interface CipherMode {
    companion object {
        val ECB: CipherMode get() = CipherModeECB
        val CBC: CipherMode get() = CipherModeECB
    }

    fun encrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray
    fun decrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray
}

private abstract class BaseCipherMode : CipherMode {
    protected fun getIV(srcIV: ByteArray?): ByteArray {
        val dstIV = ByteArray(16)
        srcIV?.apply {
            val min = if (size < dstIV.size) size else dstIV.size
            arraycopy(srcIV, 0, dstIV, 0, min)
        }
        return dstIV
    }

    protected fun ByteArray.toIntArray(): IntArray {
        val out = IntArray(size / 4)
        var m = 0
        for (n in 0 until out.size) {
            val v3 = this[m++].toInt() and 0xFF
            val v2 = this[m++].toInt() and 0xFF
            val v1 = this[m++].toInt() and 0xFF
            val v0 = this[m++].toInt() and 0xFF
            out[n] = (v0 shl 0) or (v1 shl 8) or (v2 shl 16) or (v3 shl 24)
        }
        return out
    }

    protected fun IntArray.toByteArray(): ByteArray {
        val out = ByteArray(size * 4)
        var m = 0
        for (n in 0 until size) {
            val v = this[n]
            out[m++] = ((v shr 24) and 0xFF).toByte()
            out[m++] = ((v shr 16) and 0xFF).toByte()
            out[m++] = ((v shr 8) and 0xFF).toByte()
            out[m++] = ((v shr 0) and 0xFF).toByte()
        }
        return out
    }

    protected fun Cipher.encryptBlock(data: IntArray, offset: Int) {
        val bdata = data.copyOfRange(offset, offset + 4).toByteArray()
        encrypt(bdata)
        arraycopy(bdata.toIntArray(), 0, data, offset, 4)
    }

    protected fun Cipher.decryptBlock(data: IntArray, offset: Int) {
        val bdata = data.copyOfRange(offset, offset + 4).toByteArray()
        decrypt(bdata)
        arraycopy(bdata.toIntArray(), 0, data, offset, 4)
    }
}

private object CipherModeECB : BaseCipherMode() {
    override fun encrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray {
        val pData = Padding.padding(data, cipher.blockSize, padding)
        for (n in pData.indices step 16) cipher.encrypt(pData, n)
        return pData
    }

    override fun decrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray {
        for (n in data.indices step 16) cipher.decrypt(data, n)
        return Padding.removePadding(data, padding)
    }
}

private object CipherModeCBC : BaseCipherMode() {
    override fun encrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray {
        val pData = Padding.padding(data, cipher.blockSize, padding)
        val words = pData.toIntArray()
        val wordsLength = words.size
        val ivWords = getIV(iv).toIntArray()

        if (words.size % 4 != 0) {
            throw IllegalArgumentException("Data is not multiple of ${cipher.blockSize}, and padding was set to ${CipherPadding.NoPadding}")
        }

        var s0 = ivWords[0]
        var s1 = ivWords[1]
        var s2 = ivWords[2]
        var s3 = ivWords[3]

        for (n in 0 until wordsLength step 4) {
            words[n + 0] = words[n + 0] xor s0
            words[n + 1] = words[n + 1] xor s1
            words[n + 2] = words[n + 2] xor s2
            words[n + 3] = words[n + 3] xor s3

            cipher.encryptBlock(words, n)

            s0 = words[n + 0]
            s1 = words[n + 1]
            s2 = words[n + 2]
            s3 = words[n + 3]
        }
        return words.toByteArray()
    }

    override fun decrypt(data: ByteArray, cipher: Cipher, padding: Padding, iv: ByteArray?): ByteArray {
        val dataWords = data.toIntArray()
        val wordsLength = dataWords.size
        val ivWords = getIV(iv).toIntArray()

        var s0 = ivWords[0]
        var s1 = ivWords[1]
        var s2 = ivWords[2]
        var s3 = ivWords[3]

        for (n in 0 until wordsLength step 4) {
            val t0 = dataWords[n + 0]
            val t1 = dataWords[n + 1]
            val t2 = dataWords[n + 2]
            val t3 = dataWords[n + 3]

            cipher.decryptBlock(dataWords, n)

            dataWords[n + 0] = dataWords[n + 0] xor s0
            dataWords[n + 1] = dataWords[n + 1] xor s1
            dataWords[n + 2] = dataWords[n + 2] xor s2
            dataWords[n + 3] = dataWords[n + 3] xor s3

            s0 = t0
            s1 = t1
            s2 = t2
            s3 = t3
        }
        return Padding.removePadding(dataWords.toByteArray(), padding)
    }

}
