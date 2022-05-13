package com.soywiz.korio.file.std

import java.io.File

suspend fun ByteArray.writeToFile(file: File) = localVfs(file).write(this)
