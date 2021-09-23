package com.soywiz.kds.lock

class ThreadSemaphore(private var permits: Int = 0) {
    private val lock = ThreadLockSignal()

    fun acquire(permits: Int = 1, timeoutMs: Long = 0L) {
        while (true) {
            lock.waitAfterExecute(timeoutMs) {
                if (this.permits >= permits) {
                    this.permits -= permits
                    return@acquire
                }
            }
        }
    }

    fun release(permits: Int = 1) {
        lock.notifyAllAfterExecute {
            this.permits += permits
        }
    }

    fun destroy() {
        lock.destroy()
    }
}
