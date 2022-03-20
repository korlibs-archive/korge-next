package com.soywiz.korio.net

import kotlinx.cinterop.*
import platform.linux.inet_addr
import platform.linux.inet_ntoa
import platform.posix.*
import kotlin.native.concurrent.AtomicInt
import kotlin.native.concurrent.AtomicReference
import kotlin.reflect.KProperty

object OSSL {
    //const val OPENSSL_INIT_ADD_ALL_CIPHERS = 4
    //const val OPENSSL_INIT_ADD_ALL_DIGESTS = 8
    const val OPENSSL_INIT_LOAD_CRYPTO_STRINGS = 2
    const val OPENSSL_INIT_LOAD_SSL_STRINGS = 2097152
    const val SSL_OP_NO_SSLv2 = 0

    val handle: CPointer<out CPointed>? = dlopen("libssl.so.1.1", RTLD_LAZY)
    val isAvailable get() = handle != null

    fun close() {
        dlclose(handle)
    }

    class Func<T : Function<*>>(private val name: String? = null) {
        private var _set = AtomicInt(0)
        private var _value = AtomicReference<CPointer<CFunction<T>>?>(null)

        operator fun getValue(obj: Any?, property: KProperty<*>): CPointer<CFunction<T>> {
            if (_set.value == 0) {
                val rname = name ?: property.name!!
                _value.value = dlsym(handle, rname)?.reinterpret() ?: error("Can't find '$rname'")
                _set.value = 1
            }
            return _value.value?.reinterpret()!!
        }
    }

    val OPENSSL_init_ssl by Func<(ULong, COpaquePointer?) -> Int>()
    val TLSv1_2_method by Func<() -> COpaquePointer?>()
    val SSL_CTX_new by Func<(COpaquePointer?) -> COpaquePointer?>()
    val SSL_CTX_set_options by Func<(COpaquePointer?, ULong) -> ULong>()
    val SSL_write by Func<(COpaquePointer?, COpaquePointer?, Int) -> Int>()
    val SSL_read by Func<(COpaquePointer?, COpaquePointer?, Int) -> Int>()
    val SSL_new by Func<(COpaquePointer?) -> COpaquePointer?>()
    val SSL_free by Func<(COpaquePointer?) -> Int>()
    val SSL_get_fd by Func<(COpaquePointer?) -> Int>()
    val SSL_set_fd by Func<(COpaquePointer?, Int) -> Int>()
    val SSL_set1_host by Func<(COpaquePointer?, COpaquePointer?) -> Int>()
    val SSL_connect by Func<(COpaquePointer?) -> Int>()
    val SSL_CTX_free by Func<(COpaquePointer?) -> Int>()
}

class LinuxSSLSocket {
    companion object {
        init{
            OSSL.OPENSSL_init_ssl((OSSL.OPENSSL_INIT_LOAD_SSL_STRINGS or OSSL.OPENSSL_INIT_LOAD_CRYPTO_STRINGS).convert(), null)
            if (OSSL.OPENSSL_init_ssl(0.convert(), null) < 0) {
                println("Could not initialize the OpenSSL library")
            } else {
                println("Initialized the OpenSSL library")
            }
        }
        val method = OSSL.TLSv1_2_method()
        val ctx = OSSL.SSL_CTX_new(method).also { ctx ->
            if (ctx == null) {
                println("Unable to create a new SSL context structure");
            }else {
                println("Initialized SSL context");
                OSSL.SSL_CTX_set_options(ctx, OSSL.SSL_OP_NO_SSLv2.convert())
            }
        }

        fun deinit() {
            OSSL.SSL_CTX_free(ctx)
            OSSL.close()
        }
    }

    private var ssl: COpaquePointer? = null

    fun connect(host: String, port: Int) {
        close()
        ssl = OSSL.SSL_new(ctx)
        memScoped {
            val hostent: CPointer<hostent>? = gethostbyname(host);
            val ip = hostent!!.pointed.h_addr_list!![0]!!
            val ipStr = "${ip[0].toUByte()}.${ip[1].toUByte()}.${ip[2].toUByte()}.${ip[3].toUByte()}"

            val sockfd = socket(AF_INET, SOCK_STREAM, 0);
            println("sockfd=$sockfd")
            val dest_addr = alloc<sockaddr_in>()
            dest_addr.sin_family = AF_INET.convert()
            dest_addr.sin_port = htons(port.convert())
            dest_addr.sin_addr.s_addr = inet_addr(ipStr)
            val tmp_ptr = inet_ntoa(dest_addr.sin_addr.readValue())
            println(tmp_ptr?.toKString())
            println("[1]")
            val res = connect(sockfd, dest_addr.ptr.reinterpret(), sizeOf<sockaddr_in>().convert())
            println("[2]")
            if (res < 0) {
                println("Cannot connect")
            }
            println("res=$res, sockfd=$sockfd")
            OSSL.SSL_set_fd(ssl, sockfd);
            OSSL.SSL_set1_host(ssl, host.cstr.ptr)
        }
        val connectResult = OSSL.SSL_connect(ssl)
        if (connectResult < 0) {
            println("connectResult=$connectResult")
        }
    }

    fun close() {
        if (ssl != null) {
            OSSL.SSL_free(ssl)
            close(OSSL.SSL_get_fd(ssl))
        }
        ssl = null
    }

    fun write(data: ByteArray, offset: Int = 0, size: Int = data.size - offset): Int {
        if (size <= 0) return 0
        return data.usePinned {
            OSSL.SSL_write(ssl, it.addressOf(0), size.convert())
        }
    }

    fun read(size: Int): ByteArray = ByteArray(size).also { it.copyOf(read(it)) }

    fun read(data: ByteArray, offset: Int = 0, size: Int = data.size - offset): Int {
        if (size <= 0) return 0
        return data.usePinned {
            OSSL.SSL_read(ssl, it.addressOf(0), size.convert()).also {
                if (it < 0) error("Error reading SSL : $it")
            }
        }
    }

}

/*
fun main() {
    val socket = LinuxSSLSocket()
    socket.connect("google.es", 443)
    println("write: " + socket.write("GET / HTTP/1.0\r\nHost: google.es\r\n\r\n".encodeToByteArray()))
    println("read: " + socket.read(1024).decodeToString())
}
*/
