package com.soywiz.korio.util

import com.soywiz.korio.file.PathInfo
import com.soywiz.korio.file.baseName
import java.net.URL

val URL.basename: String get() = PathInfo(this.file).baseName
