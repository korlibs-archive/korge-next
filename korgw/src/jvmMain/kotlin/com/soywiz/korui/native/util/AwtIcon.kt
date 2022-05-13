package com.soywiz.korui.native.util

import com.soywiz.korim.awt.toAwt
import com.soywiz.korim.bitmap.Bitmap

fun Bitmap.toAwtIcon() = javax.swing.ImageIcon(this.toAwt())
