package com.soywiz.kds.concurrent

import com.soywiz.kds.atomic.*
import kotlin.native.concurrent.*

class ProduceConsumeInfo<I, T : Any>(val input: I, val queue: ConcurrentDeque<T>, val produce: (input: I, queue: ConcurrentDeque<T>) -> Unit) {
    init { kdsFreeze(this) }
}

actual class ProduceConsume<I> actual constructor(actual val input: I) {
    val worker = KdsAtomicRef<Worker?>(null)

    actual fun start() {
        if (worker.value == null) {
            worker.value = Worker.start()
        }
    }

    actual fun end() {
        val worker = this.worker.value
        worker?.requestTermination()
        this.worker.value = null
    }

    actual fun <T : Any> produce(produce: (input: I, queue: ConcurrentDeque<T>) -> Unit): ConcurrentDeque<T> {
        val queue = ConcurrentDeque<T>()
        val info = ProduceConsumeInfo(input, queue, produce)
        worker.value!!.execute(TransferMode.SAFE, { info }) { info ->
            try {
                info.produce(info.input, info.queue)
            } catch (e: Throwable) {
                println("ERROR in Worker.produceConsume")
                e.printStackTrace()
            } finally {
                info.queue.close()
            }
        }
        return queue
    }
}
