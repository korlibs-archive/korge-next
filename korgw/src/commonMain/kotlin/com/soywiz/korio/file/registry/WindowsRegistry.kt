package com.soywiz.korio.file.registry

import com.soywiz.kdynlib.*
import com.soywiz.kmem.*

object WindowsRegistry {
    val KEY_MAP: Map<String, Int> get() = WinregLibrary.KEY_MAP

    val isSupported: Boolean get() = NativeLibrarySupported && Platform.PLATFORM.isWindows
    fun listSubKeys(path: String): List<String> = WinregLibrary.listSubKeys(path)
    fun listValues(path: String): Map<String, Any?> = WinregLibrary.listValues(path)
    fun getValue(path: String): Any? = WinregLibrary.getValue(path)
    fun setValue(path: String, value: Any?): Unit = WinregLibrary.setValue(path, value)
    fun deleteValue(path: String): Unit = WinregLibrary.deleteValue(path)
    fun createKey(path: String): Boolean = WinregLibrary.createKey(path)
    fun deleteKey(path: String): Unit = WinregLibrary.deleteKey(path)
    fun hasKey(path: String): Boolean = WinregLibrary.hasKey(path)

    fun hasValue(path: String): Boolean = getValue(path) != null
    fun listValueKeys(path: String): List<String> = listValues(path).keys.toList()
}

typealias HKEY = VoidPtr

object WinregLibrary : WinregLibraryIfc by WinregLibraryIfcImpl("Advapi32.dll") {
    val isSupported: Boolean get() = true

    const val KEY_READ = 0x20019
    const val KEY_WRITE = 0x20006

    const val REG_NONE = 0x00000000
    const val REG_SZ = 0x00000001
    const val REG_EXPAND_SZ = 0x00000002
    const val REG_BINARY = 0x00000003
    const val REG_DWORD = 0x00000004
    const val REG_MULTI_SZ = 0x00000007
    const val REG_QWORD = 0x00000008

    const val RRF_RT_ANY = 0x0000ffff

    const val REG_OPTION_NON_VOLATILE = 0x00000000

    const val REG_CREATED_NEW_KEY = 0x00000001
    const val REG_OPENED_EXISTING_KEY = 0x00000002

    const val ERROR_SUCCESS = 0
    const val ERROR_FILE_NOT_FOUND = 2
    const val ERROR_INSUFFICIENT_BUFFER = 122

    const val HKEY_CLASSES_ROOT = (0x80000000L).toInt()
    const val HKEY_CURRENT_USER = (0x80000001L).toInt()
    const val HKEY_LOCAL_MACHINE = (0x80000002L).toInt()
    const val HKEY_USERS = (0x80000003L).toInt()
    const val HKEY_PERFORMANCE_DATA= (0x80000004L).toInt()
    const val HKEY_PERFORMANCE_TEXT= (0x80000050L).toInt()
    const val HKEY_PERFORMANCE_NLSTEXT = (0x80000060L).toInt()
    const val HKEY_CURRENT_CONFIG  = (0x80000005L).toInt()
    const val HKEY_DYN_DATA = (0x80000006L).toInt()
    const val HKEY_CURRENT_USER_LOCAL_SETTINGS = (0x80000007L).toInt()
    val KEY_MAP: Map<String, Int> = mapOf<String, Int>(
        "HKEY_CLASSES_ROOT" to HKEY_CLASSES_ROOT,
        "HKEY_CURRENT_USER" to HKEY_CURRENT_USER,
        "HKEY_LOCAL_MACHINE" to HKEY_LOCAL_MACHINE,
        "HKEY_USERS" to HKEY_USERS,
        "HKEY_CURRENT_CONFIG" to HKEY_CURRENT_CONFIG,
        "HKEY_PERFORMANCE_DATA" to HKEY_PERFORMANCE_DATA,
        "HKEY_PERFORMANCE_TEXT" to HKEY_PERFORMANCE_TEXT,
        "HKEY_PERFORMANCE_NLSTEXT" to HKEY_PERFORMANCE_NLSTEXT,
        "HKEY_DYN_DATA" to HKEY_DYN_DATA,
        "HKEY_CURRENT_USER_LOCAL_SETTINGS" to HKEY_CURRENT_USER_LOCAL_SETTINGS,
    )

    private fun parsePath(path: String): Pair<Int, String>? {
        val rpath = normalizePath(path)
        if (rpath.isEmpty()) return null
        val rootKeyStr = rpath.substringBefore('\\')
        val keyPath = rpath.substringAfter('\\', "")
        val rootKey = KEY_MAP[rootKeyStr.uppercase()] ?: error("Invalid rootKey '${rootKeyStr}', it should start with HKEY_ and be known")
        return rootKey to keyPath
    }

    private fun parsePathWithValue(path: String): Triple<Int, String, String>? {
        val (root, keyPath) = parsePath(path) ?: return null
        val keyPathPath = keyPath.substringBeforeLast('\\')
        val valueName = keyPath.substringAfterLast('\\', "")
        return Triple(root, keyPathPath, valueName)
    }

    private fun normalizePath(path: String) = path.trim('/').replace('/', '\\')

    fun Number.toHKEY(): HKEY? = (this.toLong() and 0xFFFFFFFFL).toVoidPtr()

    private fun parsePathEx(path: String): Pair<HKEY?, String>? =
        parsePath(path)?.let { Pair(it.first.toHKEY(), it.second) }

    private fun parsePathWithValueEx(path: String): Triple<HKEY?, String, String>? =
        parsePathWithValue(path)?.let { Triple(it.first.toHKEY(), it.second, it.third) }

    @PublishedApi
    internal fun checkError(result: Int): Int {
        if (result != ERROR_SUCCESS) {
            //WinregLibrary.memScoped { val nameArray = alloc(1024 * Char.SIZE_BYTES)
            //    FormatMessageW(FORMAT_MESSAGE_FROM_SYSTEM, null, result.convert(), LANG_ENGLISH, nameArray, 1024, null)
            //}
            error("Error in Winreg result=$result")
        }
        return result
    }

    inline fun <T> openUseKey(
        root: HKEY?,
        keyPath: String,
        extraOpenMode: Int = 0,
        block: NArena.(key: HKEY?) -> T
    ): T = WinregLibrary.memScoped {
        val key = allocVoidPtr()
        checkError(RegOpenKeyExW(root, allocStringzUtf16(keyPath), 0, (KEY_READ or extraOpenMode).toInt(), key.ptr))
        try {
            block(key.value)
        } finally {
            checkError(RegCloseKey(key.value))
        }
    }

    fun listSubKeys(path: String): List<String> {
        val (root, keyPath) = parsePathEx(path) ?: return KEY_MAP.keys.toList()
        openUseKey(root, keyPath) { key ->
            val lpcSubKeys = alloc(Int.SIZE_BYTES)
            val lpcMaxSubKeyLen = alloc(Int.SIZE_BYTES)
            checkError(
                RegQueryInfoKeyW(
                    key, null, null, null, lpcSubKeys, lpcMaxSubKeyLen, null, null, null, null, null, null
                )
            )
            //println("lpcSubKeys=${lpcSubKeys.value}, lpcMaxSubKeyLen=${lpcMaxSubKeyLen.value}")

            val names = arrayListOf<String>()

            for (n in 0 until lpcSubKeys.getInt(0).toInt()) {
                memScoped {
                    val arrayLength = lpcMaxSubKeyLen.getInt(0) + 1
                    val nameArray = alloc(arrayLength * Char.SIZE_BYTES)
                    val lpcchValueName = alloc(Int.SIZE_BYTES)
                    lpcchValueName.setInt(0, arrayLength)
                    checkError(
                        RegEnumKeyExW(
                            key, n, nameArray, lpcchValueName,
                            null, null, null, null
                        )
                    )
                    val name = nameArray.readStringzUtf16()
                    names += name
                    //println("name=${name}")
                }
            }

            return names
        }
    }

    fun listValues(path: String): Map<String, Any?> {
        val (root, keyPath) = parsePathEx(path) ?: return emptyMap()
        openUseKey(root, keyPath) { hKey ->
            val lpcValues = allocInt()
            val lpcMaxValueNameLen = allocInt()
            val lpcMaxValueLen = allocInt()
            checkError(RegQueryInfoKeyW(
                hKey, null, null, null, null, null, null,
                lpcValues.ptr, lpcMaxValueNameLen.ptr, lpcMaxValueLen.ptr, null, null
            ))
            val map = LinkedHashMap<String, Any?>()
            val byteDataLength = (lpcMaxValueLen.value.toInt() + 1) * kotlin.Short.SIZE_BYTES
            val nameLength = lpcMaxValueNameLen.value.toInt() + 1

            for (n in 0 until lpcValues.value.toInt()) {
                memScoped {
                    // Data
                    val byteData = alloc(byteDataLength * Byte.SIZE_BYTES)
                    val lpcbData = allocInt().also { it.value = lpcMaxValueLen.value }

                    // Name
                    val nameArray = alloc(nameLength * Char.SIZE_BYTES)
                    val lpcchValueName = allocInt().also { it.value = nameLength.toInt() }

                    // Type
                    val lpType = allocInt()

                    checkError(RegEnumValueW(
                        hKey, n, nameArray, lpcchValueName.ptr,
                        null, lpType.ptr, byteData, lpcbData.ptr
                    ))

                    val keyName = nameArray.readStringzUtf16()
                    val dataSize = lpcbData.value.toInt()
                    val keyType = lpType.value.toInt()

                    map[keyName] = bytesToValue(byteData, dataSize, keyType)
                }
            }
            return map
        }
    }

    fun bytesToValue(byteData: VoidPtr?, dataSize: Int, keyType: Int): Any? {
        return when (keyType) {
            REG_NONE -> null
            REG_BINARY -> if (dataSize == 0 || byteData == null) byteArrayOf() else byteData.readBytes(dataSize)
            REG_QWORD -> if (dataSize == 0 || byteData == null) 0L else byteData.getLong(0)
            REG_DWORD -> if (dataSize == 0 || byteData == null) 0 else byteData.getInt(0)
            REG_SZ, REG_EXPAND_SZ -> if (dataSize == 0 || byteData == null) "" else byteData.readStringzUtf16()
            REG_MULTI_SZ -> TODO()
            else -> error("Unsupported reg type $keyType")
        }
    }

    fun hasKey(path: String): Boolean {
        val (root, keyPath) = parsePathEx(path) ?: return false
        memScoped {
            val key = allocVoidPtr()
            val result = RegOpenKeyExW(root, allocStringzUtf16(keyPath), 0, KEY_READ, key.ptr)
            when (result) {
                ERROR_SUCCESS -> return true.also { RegCloseKey(key.value) }
                ERROR_FILE_NOT_FOUND -> return false
                else -> checkError(result)
            }
        }
        return false
    }

    fun createKey(path: String): Boolean {
        val (root, keyPath) = parsePathEx(path) ?: return false
        memScoped {
            val phkResult = allocVoidPtr()
            val lpdwDisposition = allocInt()
            checkError(RegCreateKeyExW(
                root, allocStringzUtf16(keyPath), 0, null, REG_OPTION_NON_VOLATILE, KEY_READ,
                null, phkResult.ptr, lpdwDisposition.ptr
            ))
            checkError(RegCloseKey(phkResult.value))
            return lpdwDisposition.value.toInt() == REG_CREATED_NEW_KEY.toInt()
        }
    }

    fun deleteKey(path: String) {
        val (root, keyPath) = parsePathEx(path) ?: return
        memScoped {
            checkError(RegDeleteKeyW(root, allocStringzUtf16(keyPath)))
        }
    }

    fun deleteValue(path: String) {
        val (root, keyPath, valueName) = parsePathWithValueEx(path) ?: return
        openUseKey(root, keyPath) { hKey ->
            checkError(RegDeleteValueW(hKey, allocStringzUtf16(valueName)))
        }
    }

    private fun String.encodeToByteArrayUTF16(): ByteArray {
        val out = ByteArray(length * 2)
        for (n in 0 until length) {
            val char = this[n]
            out[n * 2 + 0] = ((char.code ushr 0) and 0xFF).toByte()
            out[n * 2 + 1] = ((char.code ushr 8) and 0xFF).toByte()
        }
        return out
    }

    fun setValue(path: String, value: Any?) {
        val (root, keyPath, valueName) = parsePathWithValueEx(path) ?: return
        openUseKey(root, keyPath, KEY_WRITE) { hKey ->
            val (bytes, kind) = when (value) {
                null -> byteArrayOf() to REG_NONE
                is String -> "$value\u0000".encodeToByteArrayUTF16() to REG_SZ
                is ByteArray -> value to REG_BINARY
                is Int -> ByteArray(4).also { it.write32LE(0, value) } to REG_DWORD
                is Long -> ByteArray(8).also { it.write64LE(0, value) } to REG_QWORD
                is List<*> -> value.joinToString("") { "$it\u0000" }.encodeToByteArrayUTF16() to REG_MULTI_SZ
                else -> TODO("Unimplemented setValue for type ${value::class}")
            }
            RegSetValueExW(hKey, allocStringzUtf16(valueName), 0, kind, allocBytes(bytes), bytes.size)
        }
    }

    fun getValue(path: String): Any? {
        val (root, keyPath, valueName) = parsePathWithValueEx(path) ?: return null
        memScoped {
            val lpType = allocInt()
            val lpcbData = allocInt()
            val keyPathPtr = allocStringzUtf16(keyPath)
            val valueNamePtr = allocStringzUtf16(valueName)

            val rc = RegGetValueW(root, keyPathPtr, valueNamePtr, RRF_RT_ANY, lpType.ptr, null, lpcbData.ptr)
            val keyType = lpType.value.toInt()
            if (keyType == REG_NONE) return null
            if (rc != ERROR_SUCCESS && rc != ERROR_INSUFFICIENT_BUFFER) checkError(rc)

            val byteSize = lpcbData.value.toInt()
            val byteData = alloc(byteSize + Short.SIZE_BYTES)

            checkError(RegGetValueW(root, keyPathPtr, valueNamePtr, RRF_RT_ANY, lpType.ptr, byteData, lpcbData.ptr))

            return bytesToValue(byteData, byteSize, keyType)
        }
    }
}
