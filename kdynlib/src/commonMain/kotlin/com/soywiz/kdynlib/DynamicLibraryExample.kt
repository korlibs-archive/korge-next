package com.soywiz.kdynlib

@NativeLibrary
internal interface MyNativeLibrary : Library, StdCallLibrary {
    companion object : LibraryCompanion<MyNativeLibrary> {
        override fun invoke(): MyNativeLibrary = invoke("Kernel32.dll") // This will be resolved by the KSP plugin
    }
    fun Sleep(time: Int32): Unit
    fun GetModuleFileNameA(module: VoidPtr?, name: VoidPtr?, size: Int32): Int32
}

// @TODO: Can we do this via the plugin too?
internal class MyNativeStruct(override val ptr: VoidPtr) : NativeStruct {
    companion object : NativeStructDesc<MyNativeStruct>({ MyNativeStruct(it) }) {
        val value = int()
        val struct = structRef(MyNativeStruct)
        //val value = nativeInt()
    }
    var value by MyNativeStruct.value
    var struct by MyNativeStruct.struct
}

@NativeLibrary
internal interface WinregLibrary : StdCallLibrary {
    companion object : WinregLibrary by WinregLibraryImpl("Advapi32.dll") {
        const val KEY_READ = 0x20019
        const val KEY_WRITE = 0x20006

        val HKEY_CLASSES_ROOT = VoidPtrNew((0x80000000L).toInt())
        val HKEY_CURRENT_USER = VoidPtrNew((0x80000001L).toInt())
        val HKEY_LOCAL_MACHINE = VoidPtrNew((0x80000002L).toInt())
        val HKEY_USERS = VoidPtrNew((0x80000003L).toInt())
        val HKEY_PERFORMANCE_DATA= VoidPtrNew((0x80000004L).toInt())
        val HKEY_PERFORMANCE_TEXT= VoidPtrNew((0x80000050L).toInt())
        val HKEY_PERFORMANCE_NLSTEXT = VoidPtrNew((0x80000060L).toInt())
        val HKEY_CURRENT_CONFIG  = VoidPtrNew((0x80000005L).toInt())
        val HKEY_DYN_DATA = VoidPtrNew((0x80000006L).toInt())
        val HKEY_CURRENT_USER_LOCAL_SETTINGS = VoidPtrNew((0x80000007L).toInt())
    }
    fun RegOpenKeyExW(key: VoidPtr?, lpSubKey: VoidPtr?, ulOptions: Int32, samDesired: Int32, phkResult: VoidPtr?): Int32
    fun RegCloseKey(key: VoidPtr?): Int32
    fun RegQueryInfoKeyW(hKey: VoidPtr?, lpClass: VoidPtr?, lpcchClass: VoidPtr?, lpReserved: VoidPtr?, lpcSubKeys: VoidPtr?, lpcbMaxSubKeyLen: VoidPtr?, lpcbMaxClassLen: VoidPtr?, lpcValues: VoidPtr?, lpcbMaxValueNameLen: VoidPtr?, lpcbMaxValueLen: VoidPtr?, lpcbSecurityDescriptor: VoidPtr?, lpftLastWriteTime: VoidPtr?): Int32
}

internal fun WinregLibrary.RegOpenKeyExW(key: VoidPtr, lpSubKey: String, ulOptions: Int32, samDesired: Int32, phkResult: VoidPtr): Int32 =
    memScoped { RegOpenKeyExW(key, allocStringzUtf16(lpSubKey), ulOptions, samDesired, phkResult) }

