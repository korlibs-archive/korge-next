package com.soywiz.kdynlib

import com.soywiz.kmem.*
import kotlin.test.*

class DynLibraryTest {
    @Test
    fun test() {
        if (!Platform.PLATFORM.isWindows || Platform.RUNTIME.isJs) return

        /*
        val nativeLibrary = MyNativeLibrary()
        println("Sleeping...")
        //sleep(1000)
        nativeLibrary.Sleep(10)
        println("Done")

        nativeLibrary.memScoped {
            val ptr = alloc(1024)
            val result = nativeLibrary.GetModuleFileNameA(null, ptr, 1024)
            println(MyNativeStruct(ptr).value)
            MyNativeStruct(ptr).value = 0x55555555
            MyNativeStruct(ptr).struct.value = 0x66666666
            //ptr.readStringzUtf8()

            val text = ptr.readBytes(result).decodeToString()
            println("result=$result, text=$text")
        }
        */

        fun checkError(value: Int) {
            if (value != 0) {
                error("Error in WinReg")
            }
        }

        WinregLibrary.apply {
            memScoped {
                val keyRef = alloc(VoidPtrSize)
                checkError(RegOpenKeyExW(HKEY_CURRENT_USER, "", 0, KEY_READ, keyRef))
                val key = keyRef.getVoidPtr(0)
                try {
                    val lpcSubKeys = alloc(Int32.SIZE_BYTES)
                    val lpcMaxSubKeyLen = alloc(Int32.SIZE_BYTES)
                    checkError(
                        RegQueryInfoKeyW(
                            key, null, null, null, lpcSubKeys, lpcMaxSubKeyLen, null, null, null, null, null, null
                        )
                    )
                    println(lpcSubKeys.getInt(0))
                    println(lpcMaxSubKeyLen.getInt(0))
                } finally {
                    checkError(RegCloseKey(key))
                }
            }
        }
    }
}
