package com.soywiz.korge.native

import com.soywiz.korio.posix.posixFclose
import com.soywiz.korio.posix.posixFopen
import com.soywiz.korio.posix.posixFread
import com.soywiz.korio.posix.posixFseek
import com.soywiz.korio.posix.posixFtell
import com.soywiz.korio.posix.posixFwrite
import com.soywiz.korio.posix.posixMkdir
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.usePinned
import platform.posix.SEEK_END
import platform.posix.SEEK_SET

object KorgeSimpleNativeSyncIO {
    fun mkdirs(file: String) {
        posixMkdir(file, "0777".toInt(8))
    }

    fun writeBytes(file: String, bytes: ByteArray) {
        val fd = posixFopen(file, "wb") ?: error("Can't open file '$file' for writing")
        try {
            if (bytes.isNotEmpty()) {
                memScoped {
                    bytes.usePinned { pin ->
                        posixFwrite(pin.addressOf(0), 1.convert(), bytes.size.convert(), fd)
                    }
                }
            }
        } finally {
            posixFclose(fd)
        }
    }

    fun readBytes(file: String): ByteArray {
        val fd = posixFopen(file, "rb") ?: error("Can't open file '$file' for reading")
        try {
            posixFseek(fd, 0L.convert(), SEEK_END)
            val fileSize = posixFtell(fd)
            posixFseek(fd, 0L.convert(), SEEK_SET)

            val out = ByteArray(fileSize.toInt())
            if (out.isNotEmpty()) {
                memScoped {
                    out.usePinned { pin ->
                        @Suppress("UNUSED_VARIABLE")
                        val readCount = posixFread(pin.addressOf(0), 1.convert(), out.size.convert(), fd)
                        //println("readCount: $readCount, out.size=${out.size}, fileSize=$fileSize")
                    }
                }
            }
            return out
        } finally {
            posixFclose(fd)
        }
    }
}
