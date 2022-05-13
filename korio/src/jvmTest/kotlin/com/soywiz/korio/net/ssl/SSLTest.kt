package com.soywiz.korio.net.ssl

import com.soywiz.korio.async.suspendTest
import com.soywiz.korio.net.http.Http
import com.soywiz.korio.net.http.createHttpClient
import kotlin.test.Test
import kotlin.test.assertEquals

class SSLTest {
    @Test
    //@Ignore
    fun testDownloadHttpsFile() = suspendTest {
        val client = createHttpClient()
        val result = client.requestAsString(Http.Method.GET, "https://docs.korge.org/ssltest.txt")
        //println(result.headers)
        //println(result.content)

        assertEquals("file used for SSL tests\n", result.content)
    }
}
