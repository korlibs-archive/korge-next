package com.soywiz.korge.service.storage

import com.soywiz.kds.CopyOnWriteFrozenMap
import com.soywiz.kds.atomic.KdsAtomicRef
import com.soywiz.korge.native.KorgeSimpleNativeSyncIO
import com.soywiz.korge.view.Views
import com.soywiz.korio.lang.UTF8
import com.soywiz.korio.lang.toByteArray
import com.soywiz.korio.lang.toString
import com.soywiz.korio.serialization.json.fromJson
import com.soywiz.korio.serialization.json.toJson
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.emptyList
import kotlin.collections.set
import kotlin.collections.toList

actual class NativeStorage actual constructor(val views: Views) : IStorage {
    val gameStorageFolder = views.realSettingsFolder.also { KorgeSimpleNativeSyncIO.mkdirs(it) }
    val gameStorageFile get() = "${gameStorageFolder}/game.storage"
    private fun saveStr(data: String) = KorgeSimpleNativeSyncIO.writeBytes(gameStorageFile, data.toByteArray(UTF8))
    private fun loadStr(): String = KorgeSimpleNativeSyncIO.readBytes(gameStorageFile).toString(UTF8)

    private var map = KdsAtomicRef<CopyOnWriteFrozenMap<String, String>?>(null)

    override fun toString(): String = "NativeStorage(${toMap()})"
    actual fun keys(): List<String> {
        ensureMap()
        return map.value?.keys?.toList() ?: emptyList()
    }

    private fun ensureMap(): CopyOnWriteFrozenMap<String, String> {
        if (map.value == null) {
            map.value = CopyOnWriteFrozenMap()
            val str = kotlin.runCatching { loadStr() }.getOrNull()
            if (str != null && str.isNotEmpty()) {
                try {
                    map.value!!.putAll(str.fromJson() as Map<String, String>)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
        return map.value!!
    }

    private fun save() {
        saveStr(ensureMap().toJson())
    }

    actual override fun set(key: String, value: String) {
        ensureMap()[key] = value
        save()
    }

    actual override fun getOrNull(key: String): String? {
        return ensureMap()[key]
    }

    actual override fun remove(key: String) {
        ensureMap().remove(key)
        save()
    }

    actual override fun removeAll() {
        ensureMap().clear()
        save()
    }
}

