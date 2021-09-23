package com.soywiz.kds.atomic

actual class KdsAtomicRef<T> actual constructor(initial: T) {
    val ref = java.util.concurrent.atomic.AtomicReference<T>(initial)
    actual var value: T
        get() = ref.get()
        set(value) { ref.set(value) }

    actual fun compareAndSet(expect: T, update: T): Boolean = ref.compareAndSet(expect, update)
}

actual class KdsAtomicIntRef actual constructor(initial: Int) {
    val ref = java.util.concurrent.atomic.AtomicInteger(initial)
    actual var value: Int
        get() = ref.get()
        set(value) { ref.set(value) }

    actual fun compareAndSet(expect: Int, update: Int): Boolean = ref.compareAndSet(expect, update)
}

actual class KdsAtomicLongRef actual constructor(initial: Long) {
    val ref = java.util.concurrent.atomic.AtomicLong(initial)
    actual var value: Long
        get() = ref.get()
        set(value) { ref.set(value) }

    actual fun compareAndSet(expect: Long, update: Long): Boolean = ref.compareAndSet(expect, update)
}
