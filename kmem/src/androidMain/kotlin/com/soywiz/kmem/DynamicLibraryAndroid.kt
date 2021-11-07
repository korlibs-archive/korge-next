package com.soywiz.kmem.lib

actual typealias Int32 = Int
actual typealias Int64 = Long
actual class VoidPtr
actual abstract class NPointed
actual class FunctionPtr<T : Function<*>>
actual class FunctionPtrWrapper<T : NPointed>
