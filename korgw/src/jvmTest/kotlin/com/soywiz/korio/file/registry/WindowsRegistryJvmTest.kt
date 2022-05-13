package com.soywiz.korio.file.registry

import com.soywiz.korio.async.suspendTest
import kotlin.test.Test

class WindowsRegistryJvmTest {
    @Test
    fun testRegistry() = suspendTest({ WindowsRegistry.isSupported }) {
    }
}
