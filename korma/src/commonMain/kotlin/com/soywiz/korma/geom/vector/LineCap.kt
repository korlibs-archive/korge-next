package com.soywiz.korma.geom.vector

/**
 * Determines how the lines end or start
 */
enum class LineCap {
    /**
     * Creates a butt cap, keeping
     * the size of the path.
     *
     *  ┌───────
     *  │┈┈┈┈┈┈┈
     *  └───────
     */
    BUTT,

    /**
     * Creates a square cap, expanding
     * the size of the path.
     *
     *  ┌─────────
     *  │  ┈┈┈┈┈┈┈
     *  └─────────
     */
    SQUARE,
    /**
     * Creates a rounded circular cap,
     * expanding the size of the path.
     *
     *  ╭─────────
     *  │  ┈┈┈┈┈┈┈
     *  ╰─────────
     *
     * Note:
     * The roundness of the figure is limited by the characters used.
     */
    ROUND;

    companion object {
        operator fun get(str: String?): LineCap = when {
            str.isNullOrEmpty() -> BUTT
            else -> when (str[0].uppercaseChar()) {
                'B' -> BUTT
                'S' -> SQUARE
                'R' -> ROUND
                else -> BUTT
            }
        }
    }
}
