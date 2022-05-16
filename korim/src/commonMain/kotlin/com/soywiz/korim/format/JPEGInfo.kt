package com.soywiz.korim.format

import com.soywiz.korio.stream.AsyncStream

object JPEGInfo : ImageFormatSuspend("jpeg") {
    override suspend fun decodeHeaderSuspend(s: AsyncStream, props: ImageDecodingProps): ImageInfo? {
        return kotlin.runCatching { EXIF.readExifFromJpeg(s) }
            .also {
                //it.exceptionOrNull()?.printStackTrace()
            }
            .getOrNull()
    }
}
