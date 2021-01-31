package com.soywiz.korau.sound

import com.soywiz.klock.*
import com.soywiz.korau.format.*
import com.soywiz.korau.internal.*
import com.soywiz.korio.file.*
import com.soywiz.korio.lang.*
import kotlin.math.*

abstract class AudioStream(
    val rate: Int,
    val channels: Int
) : AudioStreamable, Closeable {
    open val finished = false
    open val totalLengthInSamples: Long? = null
    val totalLength get() = ((totalLengthInSamples ?: 0L).toDouble() / rate.toDouble()).seconds
    open var currentPositionInSamples: Long = 0L
    var currentTime: TimeSpan
        set(value) = run { currentPositionInSamples = (value.seconds * rate.toDouble()).toLong() }
        get() = (currentPositionInSamples.toDouble() / rate.toDouble()).seconds
    open suspend fun read(out: AudioSamples, offset: Int = 0, length: Int = out.totalSamples): Int = 0
    override fun close() = Unit

    abstract suspend fun clone(): AudioStream
    override suspend fun toStream(): AudioStream = clone()

    companion object {
        fun generator(rate: Int, channels: Int, generateChunk: suspend AudioSamplesDeque.(step: Int) -> Boolean): AudioStream =
            GeneratorAudioStream(rate, channels, generateChunk)
    }

    internal class GeneratorAudioStream(rate: Int, channels: Int, val generateChunk: suspend AudioSamplesDeque.(step: Int) -> Boolean) : AudioStream(rate, channels) {
        val deque = AudioSamplesDeque(channels)
        val availableRead get() = deque.availableRead
        override var finished: Boolean = false
        private var step: Int = 0

        override suspend fun read(out: AudioSamples, offset: Int, length: Int): Int {
            if (finished && availableRead <= 0) return -1
            while (availableRead <= 0) {
                if (!generateChunk(deque, step++)) {
                    finished = true
                    break
                }
            }
            val read = min2(length, availableRead)
            deque.read(out, offset, read)
            return read
        }

        override suspend fun clone(): AudioStream = GeneratorAudioStream(rate, channels, generateChunk)
    }
}

suspend fun AudioStream.toData(maxSamples: Int = Int.MAX_VALUE): AudioData {
    val out = AudioSamplesDeque(channels)
    val buffer = AudioSamples(channels, 1024)
    try {
        while (!finished) {
            val read = read(buffer, 0, buffer.totalSamples)
            if (read <= 0) break
            out.write(buffer, 0, read)
            if (out.availableRead >= maxSamples) break
        }
    } finally {
        close()
    }

    val maxOutSamples = out.availableReadMax

    return AudioData(rate, AudioSamples(channels, maxOutSamples).apply { out.read(this) })
}

suspend fun AudioStream.playAndWait(params: PlaybackParameters = PlaybackParameters.DEFAULT) = nativeSoundProvider.playAndWait(this, params)
suspend fun AudioStream.playAndWait(times: PlaybackTimes = 1.playbackTimes, startTime: TimeSpan = 0.seconds, bufferTime: TimeSpan = 0.1.seconds) = nativeSoundProvider.createStreamingSound(this).playAndWait(PlaybackParameters(times, startTime, bufferTime))

suspend fun AudioStream.toSound(closeStream: Boolean = false, name: String = "Unknown"): Sound =
    nativeSoundProvider.createStreamingSound(this, closeStream, name)

suspend fun AudioStream.toSound(closeStream: Boolean = false, name: String = "Unknown", onComplete: (suspend () -> Unit)? = null): Sound =
    nativeSoundProvider.createStreamingSound(this, closeStream, name, onComplete)

suspend fun VfsFile.readAudioStreamOrNull(formats: AudioFormat = defaultAudioFormats + nativeSoundProvider.audioFormats, props: AudioDecodingProps = AudioDecodingProps.DEFAULT) = formats.decodeStream(this.open(), props)
suspend fun VfsFile.readAudioStream(formats: AudioFormat = defaultAudioFormats + nativeSoundProvider.audioFormats, props: AudioDecodingProps = AudioDecodingProps.DEFAULT) =
    readAudioStreamOrNull(formats, props)
        ?: error("Can't decode audio stream")

suspend fun VfsFile.writeAudio(data: AudioData, formats: AudioFormat = defaultAudioFormats + nativeSoundProvider.audioFormats, props: AudioEncodingProps = AudioEncodingProps.DEFAULT) =
    this.openUse(VfsOpenMode.CREATE_OR_TRUNCATE) {
        formats.encode(data, this, this@writeAudio.baseName, props)
    }
