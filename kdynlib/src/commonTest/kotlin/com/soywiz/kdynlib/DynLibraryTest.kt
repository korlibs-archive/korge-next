package com.soywiz.kdynlib

import kotlin.test.*

class DynLibraryTest {
    @Test
    fun test() {
        val nativeLibrary = MyNativeLibrary()
        println("Sleeping...")
        //sleep(1000)
        nativeLibrary.Sleep(10)
        println("Done")

        nativeLibrary.memScoped {
            val ptr = alloc(1024)
            val result = nativeLibrary.GetModuleFileNameA(null, ptr, 1024)
            println(MyStructDesc(ptr).value)
            MyStructDesc(ptr).value = 0x55555555
            MyStructDesc(ptr).struct.value = 0x66666666
            //ptr.readStringzUtf8()

            val text = ptr.readBytes(result).decodeToString()
            println("result=$result, text=$text")
        }
    }
}
