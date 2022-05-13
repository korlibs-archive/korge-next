package com.soywiz.korge

import com.soywiz.korim.format.ImageOrientation
import com.soywiz.korim.format.PNG
import com.soywiz.korim.format.orientation
import com.soywiz.korim.format.orientationSure
import com.soywiz.korim.format.readImageInfo
import com.soywiz.korio.file.std.resourcesVfs
import kotlinx.coroutines.runBlocking
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

class KorgeMultithreadedTest {
    @Test
    @Ignore
    fun test() {
        val worker = Worker.start()
        val result = try {
            worker.execute(TransferMode.SAFE, { Unit.freeze() }) {
                println("[1]")
                val log = arrayListOf<String>()
                println("[2]")
                runBlocking {
                    val imageInfo = resourcesVfs["Exif5-2x.png"].readImageInfo(PNG)
                    log.add("orientationSure=${imageInfo?.orientationSure}")
                    imageInfo?.orientation = ImageOrientation.MIRROR_HORIZONTAL
                    log.add("orientationSure=${imageInfo?.orientationSure}")
                }
                /*
                val viewsForTesting = ViewsForTesting()
                viewsForTesting.viewsTest(timeout = 5.seconds, cond = { true }) {
                    println("[3]")
                    try {
                        val imageInfo = resourcesVfs["Exif5-2x.png"].readImageInfo(PNG)
                        println("[4]")
                        log.add("orientationSure=${imageInfo?.orientationSure}")
                        println("[5]")
                    } catch (e: Throwable) {
                        println("[6e]")
                        e.printStackTrace()
                    }
                }
                 */
                log.freeze()
            }.result
        } finally {
            worker.requestTermination(processScheduledJobs = false)
        }
        assertEquals(
            """
                orientationSure=ImageOrientation(rotation=R270, flipX=true, flipY=false)
                orientationSure=ImageOrientation(rotation=R0, flipX=true, flipY=false)
            """.trimIndent(),
            result.joinToString("\n")
        )
    }
}
