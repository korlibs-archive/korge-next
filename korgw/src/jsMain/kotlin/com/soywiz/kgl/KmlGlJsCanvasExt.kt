@file:Suppress("USELESS_CAST")

package com.soywiz.kgl

import com.soywiz.kmem.FBuffer
import org.khronos.webgl.Uint8Array

val FBuffer.arrayBuffer: org.khronos.webgl.ArrayBuffer get() = (this.mem as org.khronos.webgl.ArrayBuffer)
val FBuffer.arrayUByte: Uint8Array get() = Uint8Array(this.mem)
