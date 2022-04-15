package com.soywiz.korio.process

import com.soywiz.korio.file.VfsProcessHandler
import kotlinx.cinterop.*
import platform.posix.*

// @TODO: Should we escape with ^ on windows? Check
internal fun escapeshellarg(str: String) = "'" + str.replace("'", "\\'") + "'"

expect suspend fun posixExec(
    path: String, cmdAndArgs: List<String>, env: Map<String, String>, handler: VfsProcessHandler
): Int
