package com.soywiz.kdynlib

import org.jetbrains.kotlin.backend.common.extensions.*
import org.jetbrains.kotlin.cli.common.*
import org.jetbrains.kotlin.cli.common.messages.*
import org.jetbrains.kotlin.com.intellij.mock.*
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.util.*

class KDynLibComponentRegistrar : ComponentRegistrar {
    override fun registerProjectComponents(project: MockProject, configuration: CompilerConfiguration) {
        val messageCollector = configuration.get(CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY, MessageCollector.NONE)
        IrGenerationExtension.registerExtension(project, KDynLibIrGenerationExtension(messageCollector))
    }
}

class KDynLibIrGenerationExtension(
    private val messageCollector: MessageCollector,
) : IrGenerationExtension {
    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        KDynLibTransformer(pluginContext).visitModuleFragment(moduleFragment)
        println(moduleFragment.dump())
        messageCollector.report(CompilerMessageSeverity.INFO, "moduleFragment=$moduleFragment")
        //messageCollector.report(CompilerMessageSeverity.INFO, "Argument 'file' = $file")
    }
}
