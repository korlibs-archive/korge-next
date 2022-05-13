package com.soywiz.korio.net

import com.soywiz.korio.async.suspendTest
import com.soywiz.korio.lang.UTF8
import com.soywiz.korio.lang.toByteArray
import com.soywiz.korio.lang.toString
import com.soywiz.korio.stream.readAll
import com.soywiz.krypto.encoding.hex
import kotlin.test.Ignore
import kotlin.test.Test

class MingwX64NativeSocketTest {
    @Test
    @Ignore
    fun test() = suspendTest {
        //val http = createHttpClient()
        //localVfs("C:/temp/demo.bin").writeBytes(http.request(Http.Method.GET, "https://www.google.es/").rawContent.readAll())

        //println(http.readString("https://google.es/"))

        val client = createTcpClient(secure = true)
        client.connect("www.google.es", 443)
        client.write("GET / HTTP/1.1\r\nHost: google.es\r\nConnection: close\r\n\r\n".toByteArray(UTF8))
        val bytes = client.readAll()
        println("READ BYTES ${bytes.size} ${bytes.hex}")
        println(bytes.toString(UTF8))
    }
}
