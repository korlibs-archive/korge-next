package com.soywiz.krypto

import kotlin.test.Test
import kotlin.test.assertEquals

class SHA512Test {
    @Test
    fun testEmpty() {
        assertEquals(
            "cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e",
            SHA512.digest("".encodeToByteArray()).hexLower
        )
    }

    @Test
    fun testA() {
        assertEquals(
            "1f40fc92da241694750979ee6cf582f2d5d7d28e18335de05abc54d0560e0f5302860c652bf08d560252aa5e74210546f369fbbbce8c12cfc7957b2652fe9a75",
            SHA512.digest("a".encodeToByteArray()).hexLower
        )
    }
}
