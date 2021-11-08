package com.soywiz.korlibs.kmem.nativelib

import com.google.devtools.ksp.processing.*
import com.soywiz.korlibs.ksp.*

class NativeLibProcessorProviderJvm : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor =
        NativeLibProcessorCommon(environment, NativeLibProcessorTarget.JVM)
}
