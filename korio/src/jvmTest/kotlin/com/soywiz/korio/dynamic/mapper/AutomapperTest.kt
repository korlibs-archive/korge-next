package com.soywiz.korio.dynamic.mapper

import com.soywiz.korio.dynamic.serialization.parseTyped
import com.soywiz.korio.dynamic.serialization.stringifyTyped
import com.soywiz.korio.serialization.json.Json
import com.soywiz.korio.util.jvmFallback
import kotlin.test.Test
import kotlin.test.assertEquals

class AutomapperTest {
	@Test
	fun test() {
		val mapper = ObjectMapper().jvmFallback()
		val obj = MyObj(10, "world")
		val json = Json.stringifyTyped(obj, mapper)
		assertEquals("""{"num":10,"hello":"world"}""", json)
		assertEquals(obj, Json.parseTyped<MyObj>(json, mapper))
	}

	data class MyObj(val num: Int, val hello: String)
}
