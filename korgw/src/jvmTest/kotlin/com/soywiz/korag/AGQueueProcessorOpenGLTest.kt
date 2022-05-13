package com.soywiz.korag

import com.soywiz.kgl.KmlGlProxyLogToString
import com.soywiz.korag.gl.AGQueueProcessorOpenGL
import com.soywiz.korio.annotations.KorIncomplete
import com.soywiz.korio.annotations.KorInternal
import com.soywiz.korio.test.assertEqualsJvmFileReference
import kotlin.test.Test

@OptIn(KorIncomplete::class, KorInternal::class)
class AGQueueProcessorOpenGLTest {
    @Test
    fun test() {
        val gl = KmlGlProxyLogToString()
        val global = AGGlobalState()
        val processor = AGQueueProcessorOpenGL(gl, global)
        val list = global.createList()
        list.enable(AGEnable.BLEND)
        val program = list.createProgram(DefaultShaders.PROGRAM_DEBUG)
        list.useProgram(program)
        list.deleteProgram(program)
        list.disable(AGEnable.BLEND)
        list.finish()
        processor.processBlocking(list, -1)
        assertEqualsJvmFileReference("com/soywiz/korag/AGQueueProcessorOpenGLTest.ref", gl.getLogAsString())
    }
}
