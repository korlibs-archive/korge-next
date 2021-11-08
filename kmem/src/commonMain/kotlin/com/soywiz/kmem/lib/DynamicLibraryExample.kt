package com.soywiz.kmem.lib

@NativeLibrary
interface MyNativeLibrary : Library, StdCallLibrary {
    companion object : LibraryCompanion<MyNativeLibrary> {
        override fun invoke(): MyNativeLibrary = invoke("Kernel32.dll") // This will be resolved by the KSP plugin
    }
    fun Sleep(time: Int32): Unit
    fun GetModuleFileNameA(module: VoidPtr?, name: VoidPtr?, size: Int32): Int32
}

// @TODO: Can we do this via the plugin too?
class MyStructDesc(override val ptr: VoidPtr) : LibStruct {
    companion object : LibStructDesc<MyStructDesc>({ MyStructDesc(it) }) {
        val value = int()
        val struct = structRef(MyStructDesc)
        //val value = nativeInt()
    }
    var value by MyStructDesc.value
    var struct by MyStructDesc.struct
}
