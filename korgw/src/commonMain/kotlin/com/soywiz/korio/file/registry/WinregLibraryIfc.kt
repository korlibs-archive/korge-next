package com.soywiz.korio.file.registry

import com.soywiz.kdynlib.*

@NativeLibrary
interface WinregLibraryIfc : Library, StdCallLibrary {
    fun RegOpenKeyExW(key: VoidPtr?, lpSubKey: VoidPtr?, ulOptions: Int32, samDesired: Int32, phkResult: VoidPtr?): Int32
    fun RegCloseKey(key: VoidPtr?): Int32
    fun RegQueryInfoKeyW(hKey: VoidPtr?, lpClass: VoidPtr?, lpcchClass: VoidPtr?, lpReserved: VoidPtr?, lpcSubKeys: VoidPtr?, lpcbMaxSubKeyLen: VoidPtr?, lpcbMaxClassLen: VoidPtr?, lpcValues: VoidPtr?, lpcbMaxValueNameLen: VoidPtr?, lpcbMaxValueLen: VoidPtr?, lpcbSecurityDescriptor: VoidPtr?, lpftLastWriteTime: VoidPtr?): Int32
    fun RegEnumKeyExW(hKey: VoidPtr?, dwIndex: Int32, lpName: VoidPtr?, lpcchName: VoidPtr?, lpReserved: VoidPtr?, lpClass: VoidPtr?, lpcchClass: VoidPtr?, lpftLastWriteTime: VoidPtr?): Int32
    fun RegEnumValueW(hKey: VoidPtr?, dwIndex: Int, lpValueName: VoidPtr?, lpcchValueName: VoidPtr?, lpReserved: VoidPtr?, lpType: VoidPtr?, lpData: VoidPtr?, lpcbData: VoidPtr?): Int
    fun RegDeleteKeyW(hKey: VoidPtr?, lpSubKey: VoidPtr?): Int
    fun RegDeleteValueW(hKey: VoidPtr?, lpValueName: VoidPtr?): Int
    fun RegGetValueW(hkey: VoidPtr?, lpSubKey: VoidPtr?, lpValue: VoidPtr?, dwFlags: Int, pdwType: VoidPtr?, pvData: VoidPtr?, pcbData: VoidPtr?): Int
    fun RegSetValueExW(hKey: VoidPtr?, lpValueName: VoidPtr?, Reserved: Int, dwType: Int, lpData: VoidPtr?, cbData: Int): Int
    fun RegCreateKeyExW(hKey: VoidPtr?, lpSubKey: VoidPtr?, Reserved: Int, lpClass: VoidPtr?, dwOptions: Int, samDesired: Int, lpSecurityAttributes: VoidPtr?, phkResult: VoidPtr?, lpdwDisposition: VoidPtr?): Int
}
