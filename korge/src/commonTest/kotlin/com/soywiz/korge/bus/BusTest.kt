package com.soywiz.korge.bus

import com.soywiz.korinject.*
import com.soywiz.korio.async.*
import kotlin.test.*

class BusTest {
    val out = arrayListOf<String>()

    inner class Scene1(val bus: Bus) {
        init {
            bus.register<Int> { out += "HELLO$it" }
        }
    }

    @Test
    fun test() = suspendTest {
        val injector = AsyncInjector()
        injector.mapInstance(coroutineContext) // This should be mapped already in the Korge { } block
        injector.mapBus()
        injector.mapPrototype { Scene1(get()) }
        val injector2 = injector.child()
        val scene1 = injector2.get<Scene1>()
        val bus = injector.get<Bus>()
        bus.send(1)
        bus.send(2)
        //scene1.sceneDestroyInternal()
        injector2.deinit()
        bus.send(3)
        assertEquals("HELLO1,HELLO2", out.joinToString(","))
    }
}
