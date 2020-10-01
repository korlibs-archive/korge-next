package com.soywiz.korgw.osx

import com.sun.jna.*

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
