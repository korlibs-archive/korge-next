package com.soywiz.korio.compression.zip

import com.soywiz.korio.async.suspendTest
import com.soywiz.korio.async.useIt
import com.soywiz.korio.file.std.openAsZip
import com.soywiz.korio.file.std.resourcesVfs
import kotlin.test.Test
import kotlin.test.assertEquals

class ZipFileTest {
    @Test
    fun test() = suspendTest {
        resourcesVfs["krita1.kra"].open().useIt { stream ->
            val zip = ZipFile(stream)
            //println(zip.files)
            val vfs = stream.openAsZip()
            assertEquals(65859L, vfs["mergedimage.png"].size())
        }
    }
}
