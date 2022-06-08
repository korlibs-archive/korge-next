package com.soywiz.korgw

import com.soywiz.kgl.GLFuncNull
import com.soywiz.korag.gl.AGNative
import kotlinx.cinterop.CFunction
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.UIntVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
import platform.windows.GetProcAddress
import platform.windows.HANDLE
import platform.windows.HMONITOR
import platform.windows.HMONITORVar
import platform.windows.HWND
import platform.windows.LoadLibraryA
import platform.windows.MONITOR_DEFAULTTONEAREST
import platform.windows.MonitorFromWindow

class Win32AGOpengl(val hwnd: () -> HWND?) : AGNative() {
    override val pixelsPerInch: Double get() {
        val monitor = MonitorFromWindow(hwnd(), MONITOR_DEFAULTTONEAREST)
        memScoped {
            val dpiX = alloc<UIntVar>()
            val dpiY = alloc<UIntVar>()
            if (GetDpiForMonitor != null) {
                GetDpiForMonitor?.invoke(monitor, MDT_RAW_DPI, dpiX.ptr, dpiY.ptr)
                return dpiX.value.toDouble()
            } else {
                // @TODO: Use monitor information:
                // @TODO: https://stackoverflow.com/questions/577736/how-to-obtain-the-correct-physical-size-of-the-monitor
                return 96.0
            }
        }
    }

    companion object {
        const val MDT_EFFECTIVE_DPI = 0
        const val MDT_ANGULAR_DPI = 1
        const val MDT_RAW_DPI = 2
        const val MDT_DEFAULT = MDT_EFFECTIVE_DPI

        private val shCore by lazy { LoadLibraryA("Shcore.dll") }
        private val GetDpiForMonitor: CPointer<CFunction<(hmonitor: HMONITOR?, dpiType: Int, dpiX: CPointer<UIntVar>, dpiY: CPointer<UIntVar>) -> Int>>? by lazy {
            GetProcAddress(shCore, "GetDpiForMonitor")?.reinterpret()
        }
    }
}
