package com.soywiz.korag

import com.soywiz.korag.log.*
import com.soywiz.korio.async.*
import kotlinx.coroutines.*
import org.junit.*

class AGDispatchedTest {
    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun test() = suspendTest {
        val ag = object : LogAG() {
            override fun createTexture(premultiplied: Boolean): Texture {
                println("CREATED: ${com.soywiz.korio.lang.currentThreadId}")
                return super.createTexture(premultiplied)
            }
        }
        println("BASE_THREAD: ${com.soywiz.korio.lang.currentThreadId}")
        val agDispatched = AGDispatched(ag, coroutineContext[CoroutineDispatcher.Key]!!)
        newSingleThreadContext("mainCode").use { tc ->
            val deferred = CompletableDeferred<Unit>()
            tc.launchUnscoped {
                println("REQUEST CREATE: ${com.soywiz.korio.lang.currentThreadId}")
                val tex = agDispatched.createTexture()
                deferred.complete(Unit)
                for (n in 0 until 10 ) {
                    agDispatched.createTexture()
                    delay(1L)
                }
            }
            deferred.await()
            delay(10L)
            delay(10L)
            delay(10L)
            println(ag.log)
        }
    }
}
