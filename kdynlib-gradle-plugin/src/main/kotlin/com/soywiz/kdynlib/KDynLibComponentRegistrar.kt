package com.soywiz.kdynlib

import org.jetbrains.kotlin.backend.common.*
import org.jetbrains.kotlin.backend.common.extensions.*
import org.jetbrains.kotlin.cli.common.*
import org.jetbrains.kotlin.cli.common.messages.*
import org.jetbrains.kotlin.com.intellij.mock.*
import org.jetbrains.kotlin.compiler.plugin.*
import org.jetbrains.kotlin.config.*
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.interpreter.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.*

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

class KDynLibTransformer(
    private val context: IrPluginContext,
) : IrElementTransformerVoidWithContext() {
    val irBuiltIns = context.irBuiltIns
    val factory = context.irFactory

    val symbol get() = currentScope!!.scope.scopeOwnerSymbol

    override fun visitClassNew(declaration: IrClass): IrStatement {
        if (declaration.annotations.any { it.isAnnotation(FqName("com.soywiz.kdynlib.NativeLibrary")) }) {
            //declaration.addProperty {  }
            return super.visitClassNew(declaration)
        }
        return declaration
    }

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        if (!declaration.isExternal) return declaration

        return factory.cloneFunc(declaration) {
            isExternal = false
        }.also {
            it.body = context.blockBody(it.symbol) {
                //+irThrow()
                //irReturn()

                +irReturn(10.toIrConst(irBuiltIns.intType))
            }
        }
    }

    fun IrFactory.cloneFunc(base: IrFunction, block: IrFunctionBuilder.() -> Unit): IrFunction {
        return buildFun {
            this.name = base.name
            block(this)
        }.also {
            it.valueParameters = base.valueParameters
            it.typeParameters = base.typeParameters
            it.extensionReceiverParameter = base.extensionReceiverParameter
            it.dispatchReceiverParameter = base.dispatchReceiverParameter
            it.returnType = base.returnType
            it.parent = base.parent
        }
    }
}
