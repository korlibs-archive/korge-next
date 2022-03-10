package com.github.quillraven.fleks.collection

import kotlin.test.*

internal class BitArrayTest {
    @Test
    fun createEmptyBitArray() {
        val bits = BitArray(0)

        assertEquals(0, bits.length())
        assertEquals(0, bits.capacity)
    }

    @Test
    fun setBitAtIndex3WithSufficientCapacity() {
        val bits = BitArray(3)

        bits.set(2)

        assertEquals(3, bits.length())
        assertEquals(64, bits.capacity)
        assertTrue { bits[2] }
    }

    @Test
    fun setBitAtIndex3WithInsufficientCapacity() {
        val bits = BitArray(0)

        bits.set(2)

        assertEquals(3, bits.length())
        assertEquals(64, bits.capacity)
        assertTrue { bits[2] }
    }

    @Test
    fun getBitOfOutOfBoundsIndex() {
        val bits = BitArray(0)

        assertFalse(bits[64])
    }

    @Test
    fun clearAllSetBits() {
        val bits = BitArray()
        bits.set(2)
        bits.set(4)

        bits.clearAll()

        assertEquals(0, bits.length())
    }

    @Test
    fun clearSpecificBit() {
        val bits = BitArray()
        bits.set(2)

        bits.clear(2)

        assertEquals(0, bits.length())
    }

    @Test
    fun twoBitArraysIntersectWhenTheyHaveAtLeastOneBitSetAtTheSameIndex() {
        val bitsA = BitArray(256)
        val bitsB = BitArray(1)
        bitsA.set(2)
        bitsA.set(4)
        bitsA.set(6)
        bitsB.set(4)

        val actualA = bitsA.intersects(bitsB)
        val actualB = bitsB.intersects(bitsA)

        assertTrue(actualA)
        assertTrue(actualB)
    }

    @Test
    fun twoBitArraysDoNotIntersectWhenTheyDoNotHaveAtLeastOneBitSetAtTheSameIndex() {
        val bitsA = BitArray(256)
        val bitsB = BitArray(1)
        bitsA.set(2)
        bitsA.set(4)
        bitsA.set(6)
        bitsB.set(3)

        val actualA = bitsA.intersects(bitsB)
        val actualB = bitsB.intersects(bitsA)

        assertFalse(actualA)
        assertFalse(actualB)
    }

    @Test
    fun bitArrayContainsBitArrayIfTheSameBitsAreSet() {
        val bitsA = BitArray(256)
        val bitsB = BitArray(1)
        bitsA.set(2)
        bitsA.set(4)
        bitsB.set(2)
        bitsB.set(4)

        val actualA = bitsA.contains(bitsB)
        val actualB = bitsB.contains(bitsA)

        assertTrue(actualA)
        assertTrue(actualB)
    }

    @Test
    fun bitArrayDoesNotContainBitArrayIfDifferentBitsAreSet() {
        val bitsA = BitArray(256)
        val bitsB = BitArray(1)
        bitsA.set(2)
        bitsA.set(4)
        bitsB.set(2)
        bitsB.set(3)

        val actualA = bitsA.contains(bitsB)
        val actualB = bitsB.contains(bitsA)

        assertFalse(actualA)
        assertFalse(actualB)
    }

    @Test
    fun runActionForEachSetBit() {
        val bits = BitArray(128)
        bits.set(3)
        bits.set(5)
        bits.set(117)
        var numCalls = 0
        val bitsCalled = mutableListOf<Int>()

        bits.forEachSetBit {
            ++numCalls
            bitsCalled.add(it)
        }

        assertEquals(3, numCalls)
        assertEquals(listOf(117, 5, 3), bitsCalled)
    }
}
