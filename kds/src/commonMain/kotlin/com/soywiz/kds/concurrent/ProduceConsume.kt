package com.soywiz.kds.concurrent

expect class ProduceConsume<I>(input: I) {
    actual val input: I
    fun start()
    fun end()
    fun <T : Any> produce(produce: (input: I, queue: ConcurrentDeque<T>) -> Unit): ConcurrentDeque<T>
}

inline fun <I, T> ProduceConsume<I>.startEnd(block: () -> T): T {
    start()
    try {
        return block()
    } finally {
        end()
    }
}

fun <I, T : Any> ProduceConsume<I>.produceConsumeSync(produce: (input: I, queue: ConcurrentDeque<T>) -> Unit, consume: (T) -> Unit) {
    this.produce(produce).consumeAll { consume(it) }
}

fun <I, T : Any> produceConsumeSync(input: I, produce: (input: I, queue: ConcurrentDeque<T>) -> Unit, consume: (T) -> Unit) {
    val pc = ProduceConsume(input)
    pc.startEnd {
        pc.produceConsumeSync(produce, consume)
    }
}
