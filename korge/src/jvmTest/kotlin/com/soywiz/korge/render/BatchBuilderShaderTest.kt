package com.soywiz.korge.render

import com.soywiz.korag.shader.ProgramConfig
import com.soywiz.korag.shader.gl.GlslConfig
import com.soywiz.korag.shader.gl.toNewGlslString
import com.soywiz.korge.test.assertEqualsFileReference
import kotlin.test.Test

class BatchBuilderShaderTest {
    @Test
    fun testDefault() {
        val program = BatchBuilder2D.getTextureLookupProgram(true, BatchBuilder2D.AddType.POST_ADD)
        val fragmentText = program.fragment.toNewGlslString(GlslConfig(programConfig = ProgramConfig.DEFAULT))
        assertEqualsFileReference("korge/render/Default.frag.log", fragmentText)
    }

    @Test
    fun testExternalTextureSampler() {
        val program = BatchBuilder2D.getTextureLookupProgram(true, BatchBuilder2D.AddType.POST_ADD)
        val fragmentText = program.fragment.toNewGlslString(GlslConfig(programConfig = ProgramConfig.EXTERNAL_TEXTURE_SAMPLER))
        assertEqualsFileReference("korge/render/ExternalTextureSampler.frag.log", fragmentText)
    }
}
