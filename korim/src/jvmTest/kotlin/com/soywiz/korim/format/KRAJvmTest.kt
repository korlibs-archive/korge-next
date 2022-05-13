package com.soywiz.korim.format

import com.soywiz.korio.async.suspendTest
import com.soywiz.korio.file.std.resourcesVfs
import kotlin.test.Test
import kotlin.test.assertEquals

class KRAJvmTest {
    @Test
    fun test() = suspendTest {
        val output = resourcesVfs["krita.kra"].readImageData(KRA, ImageDecodingProps().also {
            //it.kritaPartialImageLayers = true
            it.kritaPartialImageLayers = false
            it.kritaLoadLayers = true
        })
        assertEquals(4, output.frames.size)
        //output.showImagesAndWait()
    }
}
