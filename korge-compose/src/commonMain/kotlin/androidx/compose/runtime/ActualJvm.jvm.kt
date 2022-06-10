/*
 * Copyright 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.runtime

import androidx.compose.runtime.internal.ThreadMap
import androidx.compose.runtime.internal.emptyThreadMap
import com.soywiz.kds.identityHashCode
import com.soywiz.kds.linkedHashMapOf
import com.soywiz.korio.concurrent.atomic.KorAtomicInt
import com.soywiz.korio.concurrent.atomic.KorAtomicRef
import com.soywiz.korio.lang.currentThreadId

actual class AtomicReference<V> actual constructor(value: V) {
    val delegate = KorAtomicRef(value)
    actual fun get(): V = delegate.value
    actual fun set(value: V) { delegate.value = value }
    actual fun getAndSet(value: V): V {
        while (true) {
            val old = delegate.value
            if (delegate.compareAndSet(old, value)) {
                return old
            }
        }
    }
    actual fun compareAndSet(expect: V, newValue: V): Boolean = delegate.compareAndSet(expect, newValue)
}

internal actual open class ThreadLocal<T> actual constructor(
    private val initialValue: () -> T
) {
    private val sync = SynchronizedObject()
    private val values = linkedHashMapOf<Long, T>()

    @Suppress("UNCHECKED_CAST")
    actual fun get(): T {
        return synchronized(sync) { values.getOrPut(currentThreadId) { initialValue()!! } }
    }

    actual fun set(value: T) {
        synchronized(sync) { values[currentThreadId] = value }
    }

    fun initialValue(): T? {
        return initialValue.invoke()
    }

    actual fun remove() {
        synchronized(sync) { values.remove(currentThreadId) }
    }
}

internal actual class SnapshotThreadLocal<T> {
    private val map = AtomicReference<ThreadMap>(emptyThreadMap)
    private val writeMutex = SynchronizedObject()

    @Suppress("UNCHECKED_CAST")
    actual fun get(): T? = map.get().get(currentThreadId) as T?

    actual fun set(value: T?) {
        val key = currentThreadId
        synchronized(writeMutex) {
            val current = map.get()
            if (current.trySet(key, value)) return
            map.set(current.newWith(key, value))
        }
    }
}

internal actual fun identityHashCode(instance: Any?): Int = instance.identityHashCode()

//internal actual typealias TestOnly = org.jetbrains.annotations.TestOnly
actual annotation class TestOnly()

internal actual fun invokeComposable(composer: Composer, composable: @Composable () -> Unit) {
    @Suppress("UNCHECKED_CAST")
    val realFn = composable as Function2<Composer, Int, Unit>
    realFn(composer, 1)
}

internal actual fun <T> invokeComposableForResult(
    composer: Composer,
    composable: @Composable () -> T
): T {
    @Suppress("UNCHECKED_CAST")
    val realFn = composable as Function2<Composer, Int, T>
    return realFn(composer, 1)
}

actual annotation class CompositionContextLocal {}

internal actual class AtomicInt actual constructor(value: Int) {
    val delegate = KorAtomicInt(value)
    actual fun get(): Int = delegate.value
    actual fun set(value: Int) { delegate.value = value }
    actual fun add(amount: Int): Int = delegate.addAndGet(amount)
}

internal actual fun ensureMutable(it: Any) { /* NOTHING */ }
