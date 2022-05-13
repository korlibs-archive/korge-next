package com.soywiz.kgl

import kotlinx.cinterop.COpaquePointer

expect class KmlGlNative() : NativeBaseKmlGl {
}

internal expect fun glGetProcAddressAnyOrNull(name: String): COpaquePointer?
