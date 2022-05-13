package com.soywiz.korgw

import com.soywiz.kgl.KmlGlProxyLogToString
import com.soywiz.korag.gl.SimpleAGOpengl
import com.soywiz.korio.test.assertEqualsJvmFileReference
import kotlin.test.Test

class AGOpenglTest {
    @Test
    fun testClear() {
        val proxy = KmlGlProxyLogToString()
        val ag = SimpleAGOpengl(proxy)
        ag.clear()
        assertEqualsJvmFileReference("SimpleAGOpengl.clear.ref", ag.gl.log.joinToString("\n"))
    }
}
