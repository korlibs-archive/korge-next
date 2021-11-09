package com.soywiz.kdynlib

import org.jetbrains.kotlin.backend.common.extensions.*
import org.jetbrains.kotlin.backend.common.lower.*
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.interpreter.*
import org.jetbrains.kotlin.ir.symbols.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.*

public fun IrClassSymbol.getSimpleFunctionWithTypes(name: kotlin.String, types: List<IrType>): IrSimpleFunctionSymbol? {
    return this.functions.firstOrNull { it.owner.name.asString() == name && it.owner.valueParameters.map { it.type } == types }
}

fun IrPluginContext.blockBody(
    symbol: IrSymbol,
    block: IrBlockBodyBuilder.() -> Unit
): IrBlockBody = DeclarationIrBuilder(this, symbol).irBlockBody { block() }

class IrExt(val dispatcher: IrValueParameter?, val builder: IrBlockBodyBuilder, val context: IrPluginContext) {
    fun irGetThis(): IrGetValue = builder.irGet(dispatcher!!)
    fun IrExpression.getter(getter: IrSimpleFunctionSymbol) = builder.irCallOp(getter, getter.owner.returnType, this)
    fun IrExpression.call(func: IrSimpleFunctionSymbol, vararg params: IrExpression) = builder.irCall(func, func.owner.returnType).also {
        //func.symbol.owner
        //func.owner.dispatchReceiverParameter
        it.dispatchReceiver = this
        for ((index, param) in params.withIndex()) {
            //it.putTypeArgument(index + 1, param.type)
            it.putValueArgument(index, param)
        }
    }
    fun IrExpression.callStatic(func: IrSimpleFunctionSymbol, vararg params: IrExpression) = builder.irCall(func, func.owner.returnType).also {
        //it.dispatchReceiver = builder.irGet(func.owner.dispatchReceiverParameter!!)
        for ((index, param) in params.withIndex()) {
            //it.putTypeArgument(index + 1, param.type)
            it.putValueArgument(index, param)
        }
    }
    fun callNoReceiver(func: IrSimpleFunctionSymbol, vararg params: IrExpression) = builder.irCall(func, func.owner.returnType).also {
        for ((index, param) in params.withIndex()) {
            //it.putTypeArgument(index + 1, param.type)
            it.putValueArgument(index, param)
        }
    }
    fun IrSimpleFunctionSymbol.callStatic(vararg params: IrExpression) = builder.irCall(this, this.owner.returnType).also {
        return callNoReceiver(this, *params)
    }
    val Boolean.ir get() = this.toIrConst(context.irBuiltIns.booleanType)
    val Int.ir get() = this.toIrConst(context.irBuiltIns.intType)
    val Float.ir get() = this.toIrConst(context.irBuiltIns.floatType)
    val Double.ir get() = this.toIrConst(context.irBuiltIns.doubleType)
    val String.ir get() = this.toIrConst(context.irBuiltIns.stringType)
    fun irGetPropertyGetter(className: String, propertyName: String): IrSimpleFunctionSymbol =
        context.referenceClass(FqName(className))?.getPropertyGetter(propertyName) ?: error("Can't get '$className':'$propertyName'")
    fun irGetFunction(className: String, functionName: String): IrSimpleFunctionSymbol =
        context.referenceClass(FqName(className))?.getSimpleFunction(functionName) ?: error("Can't get '$className':'$functionName'")
    fun irGetFunctionWithTypes(className: String, functionName: String, types: List<IrType>): IrSimpleFunctionSymbol =
        context.referenceClass(FqName(className))
            ?.functions
            ?.firstOrNull {
                it.owner.name.asString() == functionName && it.owner.valueParameters.map { it.type }  == types
            }
            ?: error("Can't get '$className':'$functionName'")
}

fun <T> IrBlockBodyBuilder.irExt(dispatcher: IrValueParameter?, context: IrPluginContext, block: IrExt.() -> T): T = IrExt(dispatcher, this, context).block()
