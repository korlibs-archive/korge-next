package com.soywiz.kmem.lib

import kotlin.test.*

class DynLibraryTest {
    @Test
    fun test() {
        val kernel = DynLibrary("Kernel32.dll", DynCallConvention.ALT)
        val sleep = kernel.getSymbolTyped<(Int) -> Unit>("Sleep") ?: error("Can't find Sleep")
        println("Sleeping...")
        sleep(1000)
        println("Done")
    }
}
