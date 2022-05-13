package com.soywiz.korgw.osx

import com.soywiz.korev.EventDispatcher
import com.sun.jna.Library
import com.sun.jna.Native

//fun main() {
//}

interface FrameworkInt : Library {

}

class MacosGameController {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val lib = Native.load("/System/Library/Frameworks/GameController.framework/Versions/A/GameController", FrameworkInt::class.java)
            println(NSClass("GCController").msgSend("controllers"))
        }
    }
}

internal class MacosGamepadEventAdapter {
    fun updateGamepads(dispatcher: EventDispatcher) {
        // @TODO:
    }
}
