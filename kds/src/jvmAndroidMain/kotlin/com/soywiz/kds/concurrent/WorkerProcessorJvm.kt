package com.soywiz.kds.concurrent

import kotlin.concurrent.*

actual class ProduceConsume<I> actual constructor(actual val input: I) {
    private var thread: Thread? = null

    actual fun start() {
    }

    actual fun end() {
    }

    actual fun <T : Any> produce(produce: (input: I, queue: ConcurrentDeque<T>) -> Unit): ConcurrentDeque<T> {
        val queue = ConcurrentDeque<T>()
        thread = thread { produce(input, queue) }
        return queue
    }
}
