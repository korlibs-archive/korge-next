package com.soywiz.korio.process

import com.soywiz.korio.file.VfsProcessHandler

internal fun escapeshellargUnix(str: String): String {
    return buildString {
        append("'")
        for (c in str) {
            when (c) {
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                '\\' -> append("\\\\")
                '\'' -> append("'\"'\"'") // https://stackoverflow.com/questions/1250079/how-to-escape-single-quotes-within-single-quoted-strings
                else -> append(c)
            }
        }
        append("'")
    }
}

// https://sourcedaddy.com/windows-7/escaping-special-characters.html
internal fun escapeshellargWin(str: String): String {
    return buildString {
        for (c in str) {
            when (c) {
                '<', '>', '(', ')', '&', '|', ',', ';', '^', '"', '\'', '\n', '\r', '\t' -> append('^')
            }
            append(c)
        }
    }
}

expect suspend fun posixExec(
    path: String, cmdAndArgs: List<String>, env: Map<String, String>, handler: VfsProcessHandler
): Int
