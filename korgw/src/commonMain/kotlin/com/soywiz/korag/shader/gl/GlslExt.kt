package com.soywiz.korag.shader.gl

import com.soywiz.korag.shader.*

fun Shader.toNewGlslStringResult(config: GlslGenerator.Config) =
    GlslGenerator(this.type, config).generateResult(this.stm)
