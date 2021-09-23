package com.soywiz.kds.lock

actual class ThreadSignal actual constructor(){
}

actual fun ThreadSignal.wait(timeoutMs: Long) {
    synchronized(this) {
        (this as java.lang.Object).wait(timeoutMs)
    }
}

actual fun ThreadSignal.notifyAll() {
    synchronized(this) {
        (this as java.lang.Object).notifyAll()
    }
}

actual fun ThreadSignal.destroy() {
}
