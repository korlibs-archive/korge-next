package com.soywiz.kds.lock

import com.soywiz.kds.atomic.*
import kotlin.time.*

@Suppress("DEPRECATION")
@OptIn(ExperimentalTime::class)
class ThreadLockSignal {
    @PublishedApi internal val lock = Lock()
    @PublishedApi internal val signal = ThreadSignal()
    //val signal = ThreadSignal()
    @PublishedApi internal val version = KdsAtomicIntRef(0)

    // No notifyAll/notifyAllAfter can be executed until wait has been called
    inline fun waitAfterExecute(timeoutMs: Long = 0L, block: () -> Unit) {
        val start = TimeSource.Monotonic.markNow()
        val currentVersion = lock {
            block()
            version.value
        }
        while (version.value == currentVersion) {
            // @TODO: In some cases we might wait 1 millisecond, if we have a race condition
            signal.wait(1L)
            if (timeoutMs > 0) {
                if (start.elapsedNow().inWholeMilliseconds >= timeoutMs) {
                    return
                }
            }
        }
    }
    inline fun notifyAllAfterExecute(block: () -> Unit) {
        lock {
            block()
            signal.notifyAll()
            version.increment(+1)
        }
    }

    fun destroy() {
        //lock.destroy()
        signal.destroy()
    }

    //fun onNotifyOnce(block: () -> Unit) { TODO() }
}
