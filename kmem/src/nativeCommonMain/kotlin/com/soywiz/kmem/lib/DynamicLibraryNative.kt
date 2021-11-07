package com.soywiz.kmem.lib

import com.soywiz.kmem.*
import kotlinx.cinterop.*
import kotlinx.cinterop.invoke as invoke2

actual typealias VoidPtr = kotlin.native.internal.NativePtr
actual typealias NPointed = CPointed
actual typealias FunctionPtr<T> = CFunction<T>
actual typealias FunctionPtrWrapper<T> = CPointer<T>

actual open class DynLibraryBase actual constructor(val name: String, val convention: DynCallConvention) : DynSymbolResolver {
    val library = DynamicLibrary(name)

    actual val isAvailable: Boolean get() = library.isAvailable
    actual fun close() = library.close()
    override fun getSymbol(name: String): FunctionPtrWrapper<FunctionPtr<*>>? = library.getSymbol(name)
}

// e: C:/Users/soywi/projects/korlibs/korge-next/kmem/src/nativeCommonMain/kotlin/com/soywiz/kmem/lib/DynamicLibraryNative.kt: (19, 100): type R of com.soywiz.kmem.lib.invoke  of return value is not supported here: doesn't correspond to any C type
actual inline operator fun <reified R> FunctionPtrWrapper<FunctionPtr<() -> R>>.invoke(): R = this.invoke2<R>()
actual inline operator fun <reified P1, reified R> FunctionPtrWrapper<FunctionPtr<(P1) -> R>>.invoke(p1: P1): R = this.invoke2<P1, R>(p1)
