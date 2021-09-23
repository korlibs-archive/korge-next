package com.soywiz.kds.concurrent

actual class ProduceConsume<I> actual constructor(actual val input: I) {
    actual fun start() {
    }

    actual fun end() {
    }

    actual fun <T : Any> produce(produce: (input: I, queue: ConcurrentDeque<T>) -> Unit): ConcurrentDeque<T> {
        val queue = ConcurrentDeque<T>()
        // We should use a Worker here
        //TODO()
        println("ProduceConsume in JS is not using a worker and may produce a deadlock")
        produce(input, queue)
        return queue
    }
}
