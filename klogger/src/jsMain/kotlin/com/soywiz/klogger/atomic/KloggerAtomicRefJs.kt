package com.soywiz.klogger.atomic

internal actual class KloggerAtomicRef<T> actual constructor(initial: T) {
    private var _value: T = initial
    actual val value: T get() = _value
    actual inline fun update(block: (T) -> T) {
        _value = block(_value)
    }
}

