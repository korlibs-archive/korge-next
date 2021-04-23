package com.soywiz.korag.shader.gl

import com.soywiz.korag.shader.*

fun Shader.toNewGlslStringResult(config: GlslConfig) =
    GlslGenerator(this.type, config).generateResult(this.stm)
