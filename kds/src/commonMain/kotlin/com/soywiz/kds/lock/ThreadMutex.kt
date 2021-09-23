package com.soywiz.kds.lock

class ThreadMutex() {
    private val signal = ThreadLockSignal()
    private var locked = false

    fun lock() {
        while (true) {
            signal.waitAfterExecute {
                if (!locked) {
                    locked = true
                    return
                }
            }
        }
    }
    fun unlock() {
        signal.notifyAllAfterExecute {
            locked = false
        }
    }

    // Release object
    fun destroy(): Unit {
        signal.destroy()
    }
}

inline fun <T> ThreadMutex.lock(block: () -> T): T {
    lock()
    try {
        return block()
    } finally {
        unlock()
    }
}
