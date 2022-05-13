package com.soywiz.korio.internal

import com.soywiz.korio.async.await
import com.soywiz.korio.async.suspendTest
import com.soywiz.korio.compression.compress
import com.soywiz.korio.compression.deflate.ZLib
import com.soywiz.korio.compression.uncompress
import com.soywiz.korio.stream.MemorySyncStream
import com.soywiz.korio.stream.readS16LE
import com.soywiz.korio.stream.toAsync
import com.soywiz.korio.stream.write16LE
import kotlinx.coroutines.runBlocking
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze
import kotlin.test.Test
import kotlin.test.assertEquals

class TempBytesNativeTest {
    @Test
    fun test() = suspendTest {
        val worker = Worker.start()
        try {
            val result = worker.execute(TransferMode.SAFE, { 0.freeze() }) {
                runBlocking {
                    val memory = MemorySyncStream().toAsync()
                    memory.write16LE(11)
                    memory.position = 0L

                    val mem = ByteArray(1024) { (it % 16).toByte() }
                    val mem2 = mem.compress(ZLib).uncompress(ZLib)

                    Triple(memory.readS16LE(), memory.size(), mem.contentEquals(mem2)).freeze()
                }
            }.await()
            assertEquals(Triple(11, 2L, true), result)
        } finally {
            worker.requestTermination()
        }
    }
}
