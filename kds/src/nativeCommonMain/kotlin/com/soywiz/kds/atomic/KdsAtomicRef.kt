package com.soywiz.kds.atomic

import kotlin.native.concurrent.AtomicReference

// @TODO: Use AtomicReference
actual class KdsAtomicRef<T> actual constructor(initial: T) {
    val ref = AtomicReference(kdsFreeze(initial))
    actual var value: T
        get() = ref.value
        set(value) { ref.value = kdsFreeze(value) }
    actual fun compareAndSet(expect: T, update: T): Boolean = ref.compareAndSet(expect, update)
}

actual class KdsAtomicIntRef actual constructor(initial: Int) {
    val ref = KdsAtomicRef(initial)
    actual var value: Int
        get() = ref.value
        set(value) { ref.value = kdsFreeze(value) }
    actual fun compareAndSet(expect: Int, update: Int): Boolean = ref.compareAndSet(expect, update)
}

actual class KdsAtomicLongRef actual constructor(initial: Long) {
    val ref = KdsAtomicRef(initial)
    actual var value: Long
        get() = ref.value
        set(value) { ref.value = kdsFreeze(value) }
    actual fun compareAndSet(expect: Long, update: Long): Boolean = ref.compareAndSet(expect, update)
}
