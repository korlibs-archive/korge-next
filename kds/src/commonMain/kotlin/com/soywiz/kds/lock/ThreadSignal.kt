package com.soywiz.kds.lock

@Deprecated("Use ThreadLockSignal instead, since it is safer")
expect class ThreadSignal() {
}

@Deprecated("Use ThreadLockSignal.waitAfter instead, since it is safer")
expect fun ThreadSignal.wait(timeoutMs: Long = 0L): Unit
@Deprecated("Use ThreadLockSignal.notifyAllAfter instead, since it is safer")
expect fun ThreadSignal.notifyAll(): Unit
expect fun ThreadSignal.destroy(): Unit

