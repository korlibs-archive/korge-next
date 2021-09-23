package com.soywiz.kds.atomic

expect class KdsAtomicRef<T>(initial: T) {
    var value: T
    fun compareAndSet(expect: T, update: T): Boolean
}

typealias KdsAtomicBoolRef = KdsAtomicRef<Boolean>

expect class KdsAtomicIntRef(initial: Int) {
    var value: Int
    fun compareAndSet(expect: Int, update: Int): Boolean
}

expect class KdsAtomicLongRef(initial: Long) {
    var value: Long
    fun compareAndSet(expect: Long, update: Long): Boolean
}

inline fun <T> KdsAtomicRef<T>.update(updater: (T) -> T): T {
    while (true) {
        val oldValue = this.value
        val newValue = updater(oldValue)
        if (compareAndSet(oldValue, newValue)) {
            return newValue
        }
    }
}

inline fun KdsAtomicIntRef.update(updater: (Int) -> Int): Int {
    while (true) {
        val oldValue = this.value
        val newValue = updater(oldValue)
        if (compareAndSet(oldValue, newValue)) {
            return newValue
        }
    }
}
fun KdsAtomicIntRef.increment(count: Int): Int = update { it + count }
fun KdsAtomicIntRef.decrement(count: Int): Int = update { it - count }

inline fun KdsAtomicLongRef.update(updater: (Long) -> Long): Long {
    while (true) {
        val oldValue = this.value
        val newValue = updater(oldValue)
        if (compareAndSet(oldValue, newValue)) {
            return newValue
        }
    }
}
fun KdsAtomicLongRef.increment(count: Long): Long = update { it + count }
fun KdsAtomicLongRef.decrement(count: Long): Long = update { it - count }
