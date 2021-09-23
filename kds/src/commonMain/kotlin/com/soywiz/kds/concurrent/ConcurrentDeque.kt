package com.soywiz.kds.concurrent

import com.soywiz.kds.atomic.*
import com.soywiz.kds.lock.*
import kotlin.coroutines.cancellation.*
import kotlin.time.*

class ConcurrentDeque<T : Any> {
    private val items = KdsAtomicRef(kdsFreeze(emptyList<T>()))
    //@PublishedApi internal val signal = ThreadLockSignal()
    val signal = ThreadLockSignal()
    @PublishedApi internal val lock = Lock()
    @PublishedApi internal val running = KdsAtomicBoolRef(true)

    init {
        kdsFreeze(this)
    }

    val size get() = items.value.size

    fun add(item: T) {
        signal.notifyAllAfterExecute {
            this.items.update { kdsFreeze(it + item) }
        }
    }

    fun close() {
        signal.notifyAllAfterExecute {
            running.value = false
        }
    }

    //val length get() = items.value.size
    fun consumeOrTimeoutException(timeoutMs: Long = -1): T {
        return consumeOrTimeoutNull(timeoutMs) ?: throw CancellationException("timeout")
    }

    @OptIn(ExperimentalTime::class)
    fun consumeOrTimeoutNull(timeoutMs: Long = -1): T? {
        var steps = 0
        val start = TimeSource.Monotonic.markNow()

        while (running.value) {
            signal.waitAfterExecute(1L) {
                val value = tryConsume()
                if (value != null) return value
                if (++steps >= 2 && timeoutMs >= 0L && start.elapsedNow().inWholeMilliseconds >= timeoutMs) return null
            }
        }
        return null
    }

    fun tryConsume(): T? {
        var lastItem: T? = null
        this.items.update { oldList ->
            lastItem = oldList.firstOrNull()
            if (lastItem == null) {
                oldList
            } else {
                kdsFreeze(oldList.subList(1, oldList.size))
            }
        }
        return lastItem
    }

    fun isEmpty() = this.items.value.isEmpty()
    fun isNotEmpty() = this.items.value.isNotEmpty()

    inline fun consumeAvailable(block: (T) -> Unit) {
        while (true) {
            val value = tryConsume() ?: break
            block(value)
        }
    }

    inline fun consumeAll(block: (T) -> Unit) {
        while (true) {
            signal.waitAfterExecute {
                if (isEmpty() && !running.value) return
                consumeAvailable {
                    block(it)
                }
                if (isEmpty() && !running.value) return
            }
        }
    }
}
