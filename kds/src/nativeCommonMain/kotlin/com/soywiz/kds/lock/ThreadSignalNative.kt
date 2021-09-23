package com.soywiz.kds.lock

import com.soywiz.kds.atomic.*

actual class ThreadSignal actual constructor() {
    var version = KdsAtomicIntRef(0)
}

// @TODO: This is a spinlock! Suspend and resume threads on requirement
actual fun ThreadSignal.wait(timeoutMs: Long) {
    val currentVersion = version.value
    while (version.value != currentVersion) {
        //Sleep()
        // Spinlock!
    }
}

actual fun ThreadSignal.notifyAll() {
    version.increment(+1)
}

actual fun ThreadSignal.destroy() {
}
