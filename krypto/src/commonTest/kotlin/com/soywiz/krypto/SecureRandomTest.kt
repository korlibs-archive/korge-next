package com.soywiz.krypto

import kotlin.test.Test
import kotlin.test.assertNotEquals

class SecureRandomTest {
    @Test
    fun test() {
        randomUnittesting = true // Required for android not mocking some stuff required for PRNGFixes
        SecureRandom.addSeed(byteArrayOf(1, 2, 3)) // This shouldn't reduce entropy
        println(SecureRandom.nextBytes(15).toList())
        println(SecureRandom.nextBytes(15).toList())
        assertNotEquals(SecureRandom.nextBytes(16).toList(), SecureRandom.nextBytes(16).toList())
        assertNotEquals(SecureRandom.nextBytes(16).toList(), SecureRandom.nextBytes(16).toList())
        println(SecureRandom.nextBytes(15).toList())
    }
}
