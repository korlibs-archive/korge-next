package com.soywiz.kds.lock

import com.soywiz.kds.*
import kotlin.coroutines.cancellation.*

class MessageQueue<T : Any> {
    private val signal = ThreadLockSignal()
    private val items = Deque<T>()
    private var running = true

    fun enqueue(value: T) {
        signal.notifyAllAfterExecute { items.addLast(value) }
    }

    fun dequeue(): T {
        while (true) {
            if (!running) throw CancellationException("Cancelled")
            signal.waitAfterExecute(100L) {
                if (items.isNotEmpty()) return items.removeFirst()
            }
        }
    }

    fun listen(handler: (T) -> Unit) {
        running = true
        while (running) {
            handler(dequeue())
        }
    }

    fun stop() {
        destroy()
    }

    fun destroy() {
        signal.destroy()
        running = false
    }
}
