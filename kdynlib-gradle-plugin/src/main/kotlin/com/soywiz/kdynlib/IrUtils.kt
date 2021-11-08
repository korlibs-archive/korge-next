package com.soywiz.kdynlib

import org.jetbrains.kotlin.backend.common.extensions.*
import org.jetbrains.kotlin.backend.common.lower.*
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.*

fun IrPluginContext.blockBody(
    symbol: IrSymbol,
    block: IrBlockBodyBuilder.() -> Unit
): IrBlockBody = DeclarationIrBuilder(this, symbol).irBlockBody { block() }
