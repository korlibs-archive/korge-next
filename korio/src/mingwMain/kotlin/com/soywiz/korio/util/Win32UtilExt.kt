@file:OptIn(ExperimentalUnsignedTypes::class)

package com.soywiz.korio.util

import kotlinx.cinterop.CPointerVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import platform.windows.CHARVar
import platform.windows.DWORD
import platform.windows.FORMAT_MESSAGE_ALLOCATE_BUFFER
import platform.windows.FORMAT_MESSAGE_FROM_SYSTEM
import platform.windows.FORMAT_MESSAGE_IGNORE_INSERTS
import platform.windows.FormatMessageA
import platform.windows.GetLastError
import platform.windows.LANG_NEUTRAL
import platform.windows.LocalFree
import platform.windows.SUBLANG_DEFAULT

fun GetErrorAsString(error: DWORD): String {
    return memScoped {
        if (error.toInt() == 0) return ""
        val ptr = alloc<CPointerVar<CHARVar>>()
        FormatMessageA(
            (FORMAT_MESSAGE_ALLOCATE_BUFFER or FORMAT_MESSAGE_FROM_SYSTEM or FORMAT_MESSAGE_IGNORE_INSERTS).convert(),
            null, error,
            (LANG_NEUTRAL or (SUBLANG_DEFAULT shl 10)).convert(),
            ptr.ptr.reinterpret(), 0, null
        )
        val out = ptr.value?.toKString() ?: ""
        LocalFree(ptr.ptr)
        out
    }
}

fun GetLastErrorAsString(): String = GetErrorAsString(GetLastError().convert())
