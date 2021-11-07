package com.soywiz.kmem.lib

import com.sun.jna.*

actual typealias VoidPtr = Pointer
actual abstract class NPointed
actual class FunctionPtr<T : Function<*>>(val func: com.sun.jna.Function) : NPointed()
actual class FunctionPtrWrapper<T : NPointed>(val ptr: FunctionPtr<*>) {
    val func get() = ptr.func
    inline fun <reified R> call(vararg args: Any?): R {
        return when (R::class) {
            Unit::class -> this.func.invoke(args) as R
            Int::class -> this.func.invokeInt(args) as R
            Float::class -> this.func.invokeFloat(args) as R
            Double::class -> this.func.invokeDouble(args) as R
            Long::class -> this.func.invokeLong(args) as R
            Pointer::class -> this.func.invokePointer(args) as R
            else -> TODO()
        }
    }
}


actual inline operator fun <reified R> FunctionPtrWrapper<FunctionPtr<() -> R>>.invoke(): R = call()
actual inline operator fun <reified P1, reified R> FunctionPtrWrapper<FunctionPtr<(P1) -> R>>.invoke(p1: P1): R = call(p1)

//actual operator fun <R> FunctionPtrWrapper<FunctionPtr<() -> R>>.invoke(): R = this.func.invoke(arrayOf()) as R
//actual operator fun <P1, R> FunctionPtrWrapper<FunctionPtr<(P1) -> R>>.invoke(p1: P1): R = this.func.invoke(arrayOf<Any?>(p1)) as R
//actual operator fun <P1, P2, R> FunctionPtrWrapper<FunctionPtr<(P1, P2) -> R>>.invoke(p1: P1, p2: P2): R = this.func.invoke(arrayOf<Any?>(p1, p2)) as R
//actual operator fun <P1, P2, P3, R> FunctionPtrWrapper<FunctionPtr<(P1, P2, P3) -> R>>.invoke(p1: P1, p2: P2, p3: P3): R = this.func.invoke(arrayOf<Any?>(p1, p2, p3)) as R
//actual operator fun <P1, P2, P3, P4, R> FunctionPtrWrapper<FunctionPtr<(P1, P2, P3, P4) -> R>>.invoke(p1: P1, p2: P2, p3: P3, p4: P4): R = this.func.invoke(arrayOf<Any?>(p1, p2, p3, p4)) as R
//actual operator fun <P1, P2, P3, P4, P5, R> FunctionPtrWrapper<FunctionPtr<(P1, P2, P3, P4, P5) -> R>>.invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5): R
//actual operator fun <P1, P2, P3, P4, P5, P6, R> FunctionPtrWrapper<FunctionPtr<(P1, P2, P3, P4, P5, P6) -> R>>.invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6): R
//actual operator fun <P1, P2, P3, P4, P5, P6, P7, R> FunctionPtrWrapper<FunctionPtr<(P1, P2, P3, P4, P5, P6, P7) -> R>>.invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7): R
//actual operator fun <P1, P2, P3, P4, P5, P6, P7, P8, R> FunctionPtrWrapper<FunctionPtr<(P1, P2, P3, P4, P5, P6, P7, P8) -> R>>.invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8): R
//actual operator fun <P1, P2, P3, P4, P5, P6, P7, P8, P9, R> FunctionPtrWrapper<FunctionPtr<(P1, P2, P3, P4, P5, P6, P7, P8, P9) -> R>>.invoke(p1: P1, p2: P2, p3: P3, p4: P4, p5: P5, p6: P6, p7: P7, p8: P8, p9: P9): R


actual open class DynLibraryBase actual constructor(val name: String, val convention: DynCallConvention) : DynSymbolResolver {
    val nativeLibrary: NativeLibrary? = try {
        NativeLibrary.getInstance(name, mapOf(
            Library.OPTION_CALLING_CONVENTION to (when (convention) {
                DynCallConvention.STD -> com.sun.jna.Function.C_CONVENTION
                DynCallConvention.ALT -> com.sun.jna.Function.ALT_CONVENTION
            })
        ))
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }

    override fun getSymbol(name: String): FunctionPtrWrapper<FunctionPtr<*>>? {
        return nativeLibrary?.let { lib -> FunctionPtrWrapper(FunctionPtr<() -> Unit>(lib.getFunction(name))) }
    }

    actual val isAvailable: Boolean get() = nativeLibrary != null

    actual fun close() {
        nativeLibrary?.dispose()
    }
}
