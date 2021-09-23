package com.soywiz.korio.kds

import com.soywiz.kds.concurrent.*

suspend fun <I, T : Any> ProduceConsume<I>.produceConsumeSuspend(produce: (input: I, queue: ConcurrentDeque<T>) -> Unit, consume: suspend (T) -> Unit) {
    this.produce(produce).consumeAllSuspend { consume(it) }
}

suspend fun <I, T : Any> produceConsumeSuspend(input: I, produce: (input: I, queue: ConcurrentDeque<T>) -> Unit, consume: suspend (T) -> Unit) {
    val pc = ProduceConsume(input)
    pc.startEnd {
        pc.produceConsumeSuspend(produce, consume)
    }
}
