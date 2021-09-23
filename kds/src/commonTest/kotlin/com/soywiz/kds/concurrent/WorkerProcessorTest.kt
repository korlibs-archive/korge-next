package com.soywiz.kds.concurrent

import kotlin.test.*

class WorkerProcessorTest {
    @Test
    fun test() {
        val data = arrayListOf<Int>()
        produceConsumeSync<Int, Int>(10,
            produce = { input, queue ->
                queue.add(input)
                queue.add(input + 1)
                queue.add(input + 2)
            },
            consume = {
                data += it
            }
        )
        assertEquals(listOf(10, 11, 12), data)
    }
}
