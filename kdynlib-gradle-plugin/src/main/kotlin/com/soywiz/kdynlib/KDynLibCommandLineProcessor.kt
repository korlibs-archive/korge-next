package com.soywiz.kdynlib

import org.jetbrains.kotlin.compiler.plugin.*

class KDynLibCommandLineProcessor : CommandLineProcessor {
    override val pluginId: String get() = "KDynLib"
    override val pluginOptions: Collection<AbstractCliOption> get() = listOf()
}
