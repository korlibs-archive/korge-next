package com.soywiz.kds.atomic

actual class KdsAtomicRef<T> actual constructor(initial: T) {
    actual var value: T = initial
    actual fun compareAndSet(expect: T, update: T): Boolean {
        this.value = update
        return true
    }
}

actual class KdsAtomicIntRef actual constructor(initial: Int) {
    actual var value: Int = initial
    actual fun compareAndSet(expect: Int, update: Int): Boolean {
        this.value = update
        return true
    }
}

actual class KdsAtomicLongRef actual constructor(initial: Long) {
    actual var value: Long = initial
    actual fun compareAndSet(expect: Long, update: Long): Boolean {
        this.value = update
        return true
    }
}
