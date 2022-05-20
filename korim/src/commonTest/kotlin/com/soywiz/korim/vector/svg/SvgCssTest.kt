package com.soywiz.korim.vector.svg

import com.soywiz.korim.vector.format.SVG
import kotlin.test.Test
import kotlin.test.assertEquals

class SvgCssTest {
    @Test
    fun test() {
        assertEquals(
            mapOf(
                "stop-color" to "#d4d969",
                "stop-opacity" to "1",
            ),
            SVG.CSSDeclarations.parseToMap("stop-color:#d4d969;stop-opacity:1")
        )
    }
}
