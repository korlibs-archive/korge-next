package com.soywiz.kmem.lib

import kotlin.reflect.*

typealias Int32 = Int
typealias Int64 = Long
expect class VoidPtr
expect abstract class NPointed
expect class FunctionPtr<T : Function<*>> : NPointed
expect class FunctionPtrWrapper<T : NPointed>

expect inline operator fun <reified R> FunctionPtrWrapper<FunctionPtr<() -> R>>.invoke(): R
expect inline operator fun <reified P1, reified R> FunctionPtrWrapper<FunctionPtr<(P1) -> R>>.invoke(p1: P1): R
//expect operator fun <P1, P2, R> FunctionPtrWrapper<FunctionPtr<(P1, P2) -> R>>.invoke(p1: P1, p2: P2): R
//expect operator fun <P1, P2, P3, R> FunctionPtrWrapper<FunctionPtr<(P1, P2, P3) -> R>>.invoke(p1: P1, p2: P2, p3: P3): R
//expect operator fun <P1, P2, P3, P4, R> FunctionPtrWrapper<FunctionPtr<(P1, P2, P3, P4) -> R>>.invoke(p1: P1, p2: P2, p3: P3, p4: P4): R
//expect operator fun <P1, P2, P3, P4, P5, R> FunctionPtrWrapper<FunctionPtr<(P1, P2, P3, P4, P5) -> R>>.invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5): R
//expect operator fun <P1, P2, P3, P4, P5, P6, R> FunctionPtrWrapper<FunctionPtr<(P1, P2, P3, P4, P5, P6) -> R>>.invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6): R
//expect operator fun <P1, P2, P3, P4, P5, P6, P7, R> FunctionPtrWrapper<FunctionPtr<(P1, P2, P3, P4, P5, P6, P7) -> R>>.invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7): R
//expect operator fun <P1, P2, P3, P4, P5, P6, P7, P8, R> FunctionPtrWrapper<FunctionPtr<(P1, P2, P3, P4, P5, P6, P7, P8) -> R>>.invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8): R
//expect operator fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> FunctionPtrWrapper<FunctionPtr<(P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R>>.invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9): R

expect open class DynLibraryBase(name: String, convention: DynCallConvention) : DynSymbolResolver {
    public val isAvailable: Boolean
    public fun close()
}

enum class DynCallConvention { STD, ALT }

public open class DynLibrary(name: String, convention: DynCallConvention) : DynLibraryBase(name, convention) {
    public fun <T : Function<*>> func(name: String? = null): DynFun<T> = DynFun<T>(this, name)
    public fun <T : Function<*>> funcNull(name: String? = null): DynFunNull<T> = DynFunNull<T>(this, name)
}


fun interface DynSymbolResolver {
    public fun getSymbol(name: String): FunctionPtrWrapper<FunctionPtr<*>>?
}

fun <T : Function<*>> DynSymbolResolver.getSymbolTyped(name: String): FunctionPtrWrapper<FunctionPtr<T>>? =
    getSymbol(name) as? FunctionPtrWrapper<FunctionPtr<T>>?

// @TODO:
class AtomicInt(var value: Int)
class AtomicReference<T>(var value: T)

public abstract class DynamicFunBase<T : Function<*>>(public val name: String? = null) {
    private var _set = AtomicInt(0)
    private var _value = AtomicReference<FunctionPtrWrapper<FunctionPtr<T>>?>(null)

    protected fun getFuncName(property: KProperty<*>): String = name ?: property.name.removeSuffix("Ext")

    protected abstract fun glGetProcAddressT(name: String): FunctionPtrWrapper<FunctionPtr<T>>?

    protected fun _getValue(property: KProperty<*>): FunctionPtrWrapper<FunctionPtr<T>>? {
        if (_set.value == 0) {
            _value.value = glGetProcAddressT(getFuncName(property))
            _set.value = 1
        }
        return _value.value
    }
}

public abstract class DynFunLibrary<T : Function<*>>(public val library: DynSymbolResolver, name: String? = null) : DynamicFunBase<T>(name) {
    override fun glGetProcAddressT(name: String): FunctionPtrWrapper<FunctionPtr<T>>? = library.getSymbol(name) as FunctionPtrWrapper<FunctionPtr<T>>?
}

public open class DynFun<T : Function<*>>(library: DynSymbolResolver, name: String? = null) : DynFunLibrary<T>(library, name) {
    public operator fun getValue(obj: Any?, property: KProperty<*>): FunctionPtrWrapper<FunctionPtr<T>> {
        val out = _getValue(property)
        if (out == null) {
            val message = "Can't find function '${getFuncName(property)}' in $this"
            println(message)
            error(message)
        }
        return out
    }
}

public open class DynFunNull<T : Function<*>>(library: DynSymbolResolver, name: String? = null) : DynFunLibrary<T>(library, name) {
    public operator fun getValue(obj: Any?, property: KProperty<*>): FunctionPtrWrapper<FunctionPtr<T>>? = _getValue(property)
}
