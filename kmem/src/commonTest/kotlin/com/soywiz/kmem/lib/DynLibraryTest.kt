package com.soywiz.kmem.lib

import kotlin.test.*

class DynLibraryTest {
    @Test
    fun test() {
        val nativeLibrary: MyNativeLibrary = MyNativeLibrary::class.get("Kernel32.dll")
        //val kernel = DynLibrary("Kernel32.dll", DynCallConvention.ALT)
        //val sleep = kernel.getSymbolTyped<(Int) -> Unit>("Sleep") ?: error("Can't find Sleep")
        println("Sleeping...")
        //sleep(1000)
        nativeLibrary.Sleep(1000)
        println("Done")
    }
}
