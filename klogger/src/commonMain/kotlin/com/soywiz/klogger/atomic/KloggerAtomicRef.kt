package com.soywiz.klogger.atomic

import kotlin.reflect.KProperty

internal expect class KloggerAtomicRef<T>(initial: T) {
    val value: T
    inline fun update(block: (T) -> T)
}

internal operator fun <T> KloggerAtomicRef<T>.setValue(receiver: Any?, prop: KProperty<*>, newValue: T) {
    update { newValue }
}

internal operator fun <T> KloggerAtomicRef<T>.getValue(receiver: Any?, prop: KProperty<*>): T {
    return value
}

//expect fun <T> kloggerAtomicRef(initial: T): KloggerAtomicRef<T>
internal fun <T> kloggerAtomicRef(initial: T): KloggerAtomicRef<T> = KloggerAtomicRef(initial)
