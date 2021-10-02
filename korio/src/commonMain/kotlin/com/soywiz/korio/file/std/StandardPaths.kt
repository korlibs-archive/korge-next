package com.soywiz.korio.file.std

import com.soywiz.korio.lang.*
import com.soywiz.korio.util.*

// You must be aware that in some implementations, standard paths might not exist,
// and they might rely on memory folders etc.
open class StandardPathsBase {
    fun getListRaw(kind: Kind): List<String?> = when (kind) {
        Kind.FONTS -> {
            when {
                OS.isLinux -> listOf("/usr/share/fonts", "/usr/local/share/fonts", "~/.fonts")
                OS.isWindows -> listOf("%WINDIR%\\Fonts", "%LOCALAPPDATA%\\Microsoft\\Windows\\Fonts")
                OS.isMac -> listOf("/System/Library/Fonts/", "/Library/Fonts/", "~/Library/Fonts/", "/Network/Library/Fonts/")
                OS.isIos -> listOf("/System/Library/Fonts/Cache", "/System/Library/Fonts")
                OS.isAndroid -> listOf("/system/Fonts", "/system/font", "/data/fonts")
                else -> listOf("~/.fonts")
            }
        }
        Kind.TEMP, Kind.CACHE -> listOf(
            SystemProperties["java.io.tmpdir"],
            Environment["TEMP"], Environment["TMP"], Environment["TEMPDIR"], "/tmp"
        )
    }
    enum class Kind {
        TEMP, CACHE, FONTS
    }


    suspend fun init(): Unit = Unit
}

expect object StandardPaths : StandardPathsBase

fun StandardPathsBase.getList(kind: StandardPathsBase.Kind) = getListRaw(kind)
    .filterNotNull()
    .map { Environment.expand(it) }

val StandardPathsBase.temp: String get() = getList(StandardPathsBase.Kind.TEMP).firstOrNull() ?: "/tmp"
val StandardPathsBase.fonts: List<String> get() = getList(StandardPathsBase.Kind.FONTS)
