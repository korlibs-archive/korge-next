package com.soywiz.korim.font

import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
actual val nativeSystemFontProvider: NativeSystemFontProvider = FolderBasedNativeSystemFontProvider()
