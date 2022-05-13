package com.soywiz.korio.net

import java.net.InetSocketAddress
import java.net.SocketAddress

fun SocketAddress?.toAsyncAddress(): AsyncAddress {
    if (this is InetSocketAddress) {
        return AsyncAddress(this.hostString, this.port)
    }
    return AsyncAddress()
}
