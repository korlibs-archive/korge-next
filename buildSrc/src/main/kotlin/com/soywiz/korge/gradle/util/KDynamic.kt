package com.soywiz.korge.gradle.util

open class KDynamic {
	companion object : KDynamic() {
		inline operator fun <T> invoke(callback: KDynamic.() -> T): T = callback(KDynamic)
		inline operator fun <T, R> invoke(value: T, callback: KDynamic.(T) -> R): R = callback(KDynamic, value)
	}

	operator fun Any?.set(key: Any?, value: Any?) {
		when (this) {
			is MutableMap<*, *> -> (this as MutableMap<Any?, Any?>)[key] = value
			is MutableList<*> -> (this as MutableList<Any?>)[key.toInt()] = value
			else -> throw RuntimeException("Not a map or a list '$this' key=$$key, value=$value")
		}
	}

	operator fun Any?.get(key: Any?): Any? = when (this) {
		null -> null
		is Map<*, *> -> (this as Map<Any?, Any?>)[key]
		is List<*> -> this[key.toInt()]
		else -> throw RuntimeException("Not a map or a list '$this' key=$$key")
	}

	val Any?.map: Map<Any?, Any?> get() = if (this is Map<*, *>) this as Map<Any?, Any?> else LinkedHashMap()
	val Any?.list: List<Any?> get() = if (this == null) listOf() else if (this is List<*>) this else if (this is Iterable<*>) this.toList() else listOf(this)
	val Any?.keys: List<Any?> get() = if (this is Map<*, *>) keys.toList() else listOf()

	fun Any?.toNumber(): Number = when (this) {
		null -> 0
		is Boolean -> if (this) 1 else 0
		is Number -> this
		is String -> (this.toIntOrNull() ?: this.toDoubleOrNull() ?: 0) as Number
		else -> 0
	}

	fun Any?.toBool(): Boolean = when (this) {
		is Boolean -> this
		is String -> when (this.toLowerCase()) {
			"", "0", "false", "NaN", "null", "undefined", "ko", "no" -> false
			else -> true
		}
		else -> toInt() != 0
	}

	fun Any?.toByte(): Byte = toNumber().toByte()
	fun Any?.toChar(): Char = when {
		this is Char -> this
		this is String && (this.length == 1) -> this.first()
		else -> toNumber().toChar()
	}

	fun Any?.toShort(): Short = toNumber().toShort()
	fun Any?.toInt(): Int = toNumber().toInt()
	fun Any?.toLong(): Long = toNumber().toLong()
	fun Any?.toFloat(): Float = toNumber().toFloat()
	fun Any?.toDouble(): Double = toNumber().toDouble()

	fun Any?.toBoolOrNull(): Boolean? = when (this) {
	    is Boolean -> this
		is String -> this == "1" || this == "true" || this == "on"
		is Number -> toInt() != 0
		else -> null
	}

	fun Any?.toIntOrNull(): Int? = when (this) {
		is Number -> toInt()
		is String -> this.toIntOrNull(10)
		else -> null
	}

	fun Any?.toLongOrNull(): Long? = when (this) {
		is Number -> toLong()
		is String -> toLongOrNull(10)
		else -> null
	}

	fun Any?.toDoubleOrNull(): Double? = when (this) {
		is Number -> toDouble()
		is String -> this.toDouble()
		else -> null
	}

	fun Any?.toIntDefault(default: Int = 0): Int = when (this) {
		is Number -> toInt()
		is String -> toIntOrNull(10) ?: default
		else -> default
	}

	fun Any?.toLongDefault(default: Long = 0L): Long = when (this) {
		is Number -> toLong()
		is String -> toLongOrNull(10) ?: default
		else -> default
	}

	fun Any?.toFloatDefault(default: Float = 0f): Float = when (this) {
		is Number -> toFloat()
		is String -> this.toFloat()
		else -> default
	}

	fun Any?.toDoubleDefault(default: Double = 0.0): Double = when (this) {
		is Number -> toDouble()
		is String -> this.toDouble()
		else -> default
	}

	val Any?.str: String get() = toString()
	val Any?.int: Int get() = toIntDefault()
	val Any?.bool: Boolean get() = toBoolOrNull() ?: false
	val Any?.float: Float get() = toFloatDefault()
	val Any?.double: Double get() = toDoubleDefault()
	val Any?.long: Long get() = toLongDefault()

	val Any?.intArray: IntArray get() = this as? IntArray ?: list.map { it.int }.toIntArray()
	val Any?.floatArray: FloatArray get() = this as? FloatArray ?: list.map { it.float }.toFloatArray()
	val Any?.doubleArray: DoubleArray get() = this as? DoubleArray ?: list.map { it.double }.toDoubleArray()
	val Any?.longArray: LongArray get() = this as? LongArray ?: list.map { it.long }.toLongArray()
}
