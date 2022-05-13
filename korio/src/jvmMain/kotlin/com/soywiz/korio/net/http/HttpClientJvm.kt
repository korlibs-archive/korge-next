package com.soywiz.korio.net.http

import com.soywiz.kmem.toUintClamp
import com.soywiz.korio.async.executeInWorkerJVM
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korio.async.toAsyncInputStream
import com.soywiz.korio.concurrent.atomic.incrementAndGet
import com.soywiz.korio.lang.invalidOp
import com.soywiz.korio.lang.runIgnoringExceptions
import com.soywiz.korio.stream.AsyncInputStreamWithLength
import com.soywiz.korio.stream.getAvailable
import com.soywiz.korio.stream.withLength
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.newSingleThreadContext
import java.net.BindException
import java.net.HttpURLConnection
import java.net.URL
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.math.min

class HttpClientJvm : HttpClient() {
	companion object {
		var lastId = 0
	}

	val clientId = lastId++
	var lastRequestId = 0

	class FakeHttpsTrustManager : X509TrustManager {
		override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf<X509Certificate>()
		override fun checkClientTrusted(x509Certificates: Array<X509Certificate>, s: String) = Unit
		override fun checkServerTrusted(x509Certificates: Array<X509Certificate>, s: String) = Unit
		fun isClientTrusted(chain: Array<X509Certificate>): Boolean = true
		fun isServerTrusted(chain: Array<X509Certificate>): Boolean = true
	}

	override suspend fun requestInternal(
		method: Http.Method,
		url: String,
		headers: Http.Headers,
		content: AsyncInputStreamWithLength?
	): Response {
		val result = executeInWorkerJVM {
			val requestId = lastRequestId++
			val id = "request[$clientId,$requestId]"

			//println("url[$id] thread=$currentThreadId: $url ")
			val aurl = URL(url)
			HttpURLConnection.setFollowRedirects(false)
			val con = aurl.openConnection() as HttpURLConnection
			if (ignoreSslCertificates && (con is HttpsURLConnection)) {
				con.hostnameVerifier = HostnameVerifier { _, _ -> true }
				val context = SSLContext.getInstance("TLS")
				context!!.init(null, arrayOf<TrustManager>(FakeHttpsTrustManager()), SecureRandom())
				con.sslSocketFactory = context.socketFactory
			}

			con.requestMethod = method.name
			//println(" --> [$id]${method.name}")
			//println("URL:$url")
			//println("METHOD:${method.name}")
			for (header in headers) {
				//println("HEADER:$header")
				con.addRequestProperty(header.first, header.second)
				//println(" --> [$id]${header.first} - ${header.second}")
			}
			//println(" --> [$id]content=${content?.size()}")
			if (content != null) {
				con.doOutput = true

				//val ccontent = content.sliceStart()
				val len = content.getAvailable()
				var left = len
				val temp = ByteArray(1024)
				//println("HEADER:content-length, $len")

				while (true) {
					try {
						con.connect()
						HttpStats.connections.incrementAndGet()
						break
					} catch (e: BindException) {
						// Potentially no more ports available. Too many pending connections.
						e.printStackTrace()
						delay(1000)
						continue
					}
				}

				val os = con.outputStream
				while (left > 0) {
					val read = content.read(temp, 0, min(temp.size, left.toUintClamp()))
					if (read <= 0) invalidOp("Problem reading")
					left -= read
					os.write(temp, 0, read)
				}
				os.flush()
				os.close()
                content.close()
			} else {
				con.connect()
			}

			val channel = Channel<ByteArray>(4)

			val pheaders = Http.Headers.fromListMap(con.headerFields)
			val length = pheaders[Http.Headers.ContentLength]?.toLongOrNull()

			launchImmediately(newSingleThreadContext("HttpRequest: $method: $url")) {
				val syncStream = runIgnoringExceptions { con.inputStream } ?: runIgnoringExceptions { con.errorStream }
				try {
					if (syncStream != null) {
						//val stream = syncStream.toAsync(length).toAsyncStream()
						val stream = syncStream
						val temp = ByteArray(0x1000)
						loop@ while (true) {
							// @TODO: Totally cancel reading if nobody is consuming this. Think about the best way of doing this.
							// node.js pause equivalent?
							//val chunkStartTime = DateTime.now()
							//while (produceConsumer.isFull > 4) { // Prevent filling the memory if nobody is consuming data
							//	//println("PREVENT!")
							//	delay(10)
							//	val chunkCurrentTime = DateTime.now()
							//	if ((chunkCurrentTime - chunkStartTime) >= 2.seconds) {
							//		System.err.println("[$id] thread=$currentThreadId Two seconds passed without anyone reading data (available=${produceConsumer.availableCount}) from $url. Closing...")
							//		break@loop
							//	}
							//}
							val read = stream.read(temp)
							//println(" --- [$id][D] thread=$currentThreadId : $read")
							if (read <= 0) break
							channel.send(temp.copyOf(read))
						}
					}
				} finally {
					runIgnoringExceptions { syncStream?.close() }
					runIgnoringExceptions { channel.close() }
					runIgnoringExceptions { con.disconnect() }
					HttpStats.disconnections.incrementAndGet()
				}
			}

			//Response(
			//		status = con.responseCode,
			//		statusText = con.responseMessage,
			//		headers = Http.Headers.fromListMap(con.headerFields),
			//		content = if (con.responseCode < 400) con.inputStream.readBytes().openAsync() else con.errorStream.toAsync().toAsyncStream()
			//)

			val acontent = channel.toAsyncInputStream()
			Response(
				status = con.responseCode,
				statusText = con.responseMessage,
				headers = pheaders,
				rawContent = if (length != null) acontent.withLength(length) else acontent
			)
		}
		return result
	}
}
