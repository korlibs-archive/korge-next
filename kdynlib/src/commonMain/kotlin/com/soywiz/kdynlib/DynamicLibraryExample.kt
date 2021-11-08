package com.soywiz.kdynlib

@NativeLibrary
internal interface MyNativeLibrary : Library, StdCallLibrary {
    companion object : LibraryCompanion<MyNativeLibrary> {
        override fun invoke(): MyNativeLibrary = invoke("Kernel32.dll") // This will be resolved by the KSP plugin
    }
    fun Sleep(time: Int32): Unit
    fun GetModuleFileNameA(module: VoidPtr?, name: VoidPtr?, size: Int32): Int32
}

// @TODO: Can we do this via the plugin too?
internal class MyNativeStruct(override val ptr: VoidPtr) : NativeStruct {
    companion object : NativeStructDesc<MyNativeStruct>({ MyNativeStruct(it) }) {
        val value = int()
        val struct = structRef(MyNativeStruct)
        //val value = nativeInt()
    }
    var value by MyNativeStruct.value
    var struct by MyNativeStruct.struct
}
