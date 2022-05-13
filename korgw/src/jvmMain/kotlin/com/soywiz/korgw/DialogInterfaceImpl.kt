package com.soywiz.korgw

import com.soywiz.korgw.awt.DialogInterfaceAwt
import java.awt.Component

actual fun createDialogInterfaceForComponent(nativeComponent: Any?): DialogInterface =
    DialogInterfaceAwt { nativeComponent as? Component? }
