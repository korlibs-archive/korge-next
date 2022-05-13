package com.soywiz.korio.net.http

internal actual val httpFactory: HttpFactory by lazy {
	object : HttpFactory {
		override fun createClient(): HttpClient = HttpPortable.createClient()
		override fun createServer(): HttpServer = HttpPortable.createServer()
	}
}
