package com.soywiz.korim.color

import java.awt.Color

fun RGBA.toAwt(): Color = Color(r, g, b, a)
fun Color.toRgba(): RGBA = RGBA(red, green, blue, alpha)
