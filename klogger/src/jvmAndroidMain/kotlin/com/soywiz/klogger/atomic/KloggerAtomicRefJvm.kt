package com.soywiz.klogger.atomic

import java.util.concurrent.atomic.AtomicReference

internal actual class KloggerAtomicRef<T> actual constructor(initial: T) {
    private val ref = AtomicReference(initial)

    actual val value: T get() = ref.get()
    actual inline fun update(block: (T) -> T) {
        //synchronized(ref) { ref.set(ref.get()) }
        do {
            val old = ref.get()
            val new = block(old)
        } while (!ref.compareAndSet(old, new))
    }
}
