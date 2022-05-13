package com.soywiz.korge.issues

import com.soywiz.korge.Korge
import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onDown
import com.soywiz.korge.view.anchor
import com.soywiz.korge.view.image
import com.soywiz.korge.view.position
import com.soywiz.korge.view.scale
import com.soywiz.korim.color.Colors
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import kotlinx.coroutines.runBlocking

object Issue83Sample {
    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            main()
        }
    }

    suspend fun main() = Korge(width = 512, height = 512, bgcolor = Colors["#2b2b2b"]) {
        val image = image(resourcesVfs["korge.png"].readBitmap()) {
            anchor(.5, .5)
            scale(.8)
            position(256, 256)
        }

        onClick {
            println("onClick:" + it.currentPosLocal)
        }
        onDown {
            println("onDown:" + it.downPosGlobal)
        }

    }
}
