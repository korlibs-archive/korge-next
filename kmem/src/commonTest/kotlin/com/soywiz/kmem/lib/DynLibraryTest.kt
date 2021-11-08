package com.soywiz.kmem.lib

import kotlin.test.*

class DynLibraryTest {
    @Test
    fun test() {
        //val nativeLibrary: MyNativeLibrary = MyNativeLibrary::class.get("Kernel32.dll")
        val nativeLibrary = MyNativeLibrary()
        //val kernel = DynLibrary("Kernel32.dll", DynCallConvention.ALT)
        //val sleep = kernel.getSymbolTyped<(Int) -> Unit>("Sleep") ?: error("Can't find Sleep")
        println("Sleeping...")
        //sleep(1000)
        nativeLibrary.Sleep(10)
        println("Done")

        nativeLibrary.memScoped {
            val ptr = alloc(1024)
            val result = nativeLibrary.GetModuleFileNameA(null, ptr, 1024)
            val text = ptr.readBytes(result).decodeToString()
            //ptr.readStringzUtf8()
            println("result=$result, text=$text")
        }
    }
}
