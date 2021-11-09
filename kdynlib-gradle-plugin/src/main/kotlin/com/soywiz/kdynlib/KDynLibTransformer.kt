package com.soywiz.kdynlib

import org.jetbrains.kotlin.backend.common.*
import org.jetbrains.kotlin.backend.common.extensions.*
import org.jetbrains.kotlin.backend.common.ir.*
import org.jetbrains.kotlin.backend.common.lower.*
import org.jetbrains.kotlin.ir.*
import org.jetbrains.kotlin.ir.backend.js.utils.*
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.builders.declarations.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.impl.*
import org.jetbrains.kotlin.ir.descriptors.*
import org.jetbrains.kotlin.ir.expressions.impl.*
import org.jetbrains.kotlin.ir.symbols.*
import org.jetbrains.kotlin.ir.symbols.impl.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.*

class KDynLibTransformer(
    private val context: IrPluginContext,
) : IrElementTransformerVoidWithContext() {
    val irBuiltIns = context.irBuiltIns
    val factory = context.irFactory

    val JNA_NATIVE_CLASS = context.referenceClass(FqName("com.soywiz.kdynlib.NativeLibraryJvm"))!!
    //val JNA_NATIVE_CLASS_REGISTER = JNA_NATIVE_CLASS.getSimpleFunctionWithTypes("register", listOf(irBuiltIns.stringType))!!
    val JNA_NATIVE_CLASS_REGISTER = JNA_NATIVE_CLASS.getSimpleFunction("register")!!

    val BASE_LIBRARY_CLASS = context.referenceClass(FqName("com.soywiz.kdynlib.BaseLibrary"))!!
    val BASE_LIBRARY_CLASS_NAME_PROP = BASE_LIBRARY_CLASS.getPropertyGetter("name")!!
    val printlnFuncs = context.referenceFunctions(FqName("kotlin.io.println")).toList()
    val printlnFunc = printlnFuncs.first { it.owner.valueParameters.map { it.type } == listOf(irBuiltIns.anyNType) }


    val symbol get() = currentScope!!.scope.scopeOwnerSymbol

    override fun visitClassNew(declaration: IrClass): IrStatement {
        if (declaration.annotations.any { it.isAnnotation(FqName("com.soywiz.kdynlib.NativeLibrary")) }) {
            val anonInit = factory.createAnonymousInitializer(
                declaration.startOffset,
                declaration.endOffset,
                declaration.origin,
                IrAnonymousInitializerSymbolImpl(declaration.symbol),
                isStatic = false
                //isStatic = true
            )
            anonInit.parent = declaration

            anonInit.body = context.blockBody(anonInit.symbol) {
                //factory.createValueParameter(0, 0, declaration, IrValueParameterSymbolImpl(),)
                //IrValueParameterImpl()
                irExt(declaration.primaryConstructor!!.dispatchReceiverParameter, this@KDynLibTransformer.context) {
                    fun ANON_INIT_THIS() = builder.irGet(declaration.symbol.owner.thisReceiver!!)

                    //+callNoReceiver(printlnFunc, "hello".ir)
                    //IrClassReferenceImpl(0, 0, declaration.symbol.starProjectedType, )
                    +JNA_NATIVE_CLASS_REGISTER.callStatic(
                        //irGetObject(declaration.symbol),
                        IrClassReferenceImpl(0, 0, irBuiltIns.kClassClass.typeWith(listOf(declaration.symbol.starProjectedType)), declaration.symbol, declaration.symbol.starProjectedType),
                        ANON_INIT_THIS().getter(BASE_LIBRARY_CLASS_NAME_PROP)
                    )
                    //+callNoReceiver(JNA_NATIVE_CLASS_REGISTER, ANON_INIT_THIS().getter(BASE_LIBRARY_CLASS_NAME_PROP))

                    //+callNoReceiver(JNA_NATIVE_CLASS_REGISTER, "hello".ir)
                    //irGetObject()
                    //+irGetObject(JNA_NATIVE_CLASS).call(
                    //    JNA_NATIVE_CLASS_REGISTER,
                    //    "hello".ir
                    //    //irGetThis().getter(BASE_LIBRARY_CLASS_NAME_PROP)
                    //)
                }
            }

            //declaration.addProperty {  }
            declaration.declarations.add(anonInit)
            //IrAnonymousInitializer.
            return super.visitClassNew(declaration)
        }
        return declaration
    }

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        if (!declaration.isExternal) return declaration
        if (declaration.isStatic) return declaration
        // In JVM we return it directly
        return declaration

        // In other targets we set a body to the external function

        return factory.cloneFunc(declaration) {
            isExternal = false
        }.also { func ->
            //func.isStatic = true
            //func.extensionReceiverParameter = null
            //func.dispatchReceiverParameter = null
            //func.valueParameters = func.valueParameters.drop(1)
            //val JvmStaticClass = context.referenceClass(FqName("kotlin.jvm.JvmStatic"))!!

            //func.annotations = func.annotations + IrConstructorCallImpl(
            //    0, 0, context.referenceClass(FqName("kotlin.jvm.JvmStatic"))!!.starProjectedType, JvmStaticClass.constructors.first().owner.symbol, 0, 0, 0
            //)

            func.body = context.blockBody(func.symbol) {
                irExt(func.dispatchReceiverParameter, this@KDynLibTransformer.context) {
                    /*
                    val nameGetter = irGetPropertyGetter("com.soywiz.kdynlib.BaseLibrary", "name")
                    val lengthGetter = irGetPropertyGetter("kotlin.String", "length")
                    val intPlus = irGetFunctionWithTypes("kotlin.Int", "plus", listOf(irBuiltIns.intType))

                    +irReturn(
                        irGetThis().getter(nameGetter).getter(lengthGetter).call(intPlus, 10.ir)
                    )
                     */
                    +irReturn(10.ir)
                }
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
