package com.soywiz.kmem.lib

actual class VoidPtr
actual abstract class NPointed
actual class FunctionPtr<T : Function<*>> : NPointed()
actual class FunctionPtrWrapper<T : NPointed>

actual open class DynLibraryBase actual constructor(val name: String, val convention: DynCallConvention) : DynSymbolResolver {
    actual val isAvailable: Boolean get() = false
    actual fun close() = Unit
    override fun getSymbol(name: String): FunctionPtrWrapper<FunctionPtr<*>>? = null
}
