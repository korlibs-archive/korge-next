package com.soywiz.korge.input

import com.soywiz.kds.*
import com.soywiz.kds.iterators.*
import com.soywiz.korev.*
import com.soywiz.korge.component.*
import com.soywiz.korge.view.*
import com.soywiz.korio.async.*
import com.soywiz.korio.util.*

class TouchEvents(override val view: View) : TouchComponent {
    data class Info(
        var index: Int = -1,
        var id: Int = 0,
        var localX: Double = 0.0,
        var localY: Double = 0.0,
        var startLocalX: Double = 0.0,
        var startLocalY: Double = 0.0,
    ) : Extra by Extra.Mixin() {
        override fun toString(): String = "Touch[$id](${localX.toInt()}, ${localY.toInt()})"
    }

    val start = Signal<Info>()
    val move = Signal<Info>()
    val end = Signal<Info>()

    fun Info.copyFrom(touch: Touch) = this.apply {
        this.id = touch.id
        this.localX = view.globalToLocalX(touch.x, touch.y)
        this.localY = view.globalToLocalY(touch.x, touch.y)
    }

    fun Info.start() = this.apply {
        startLocalX = localX
        startLocalY = localY
    }

    private val infos = Pool { Info(it) }
    private val infoById = FastIntMap<Info>()
    override fun onTouchEvent(views: Views, e: TouchEvent) {
        val actionTouch = e.actionTouch ?: e.touches.firstOrNull() ?: Touch.dummy

        when (e.type) {
            TouchEvent.Type.START -> {
                val info = infos.alloc().copyFrom(actionTouch).start()
                infoById[info.id] = info
                start(info)
            }
            TouchEvent.Type.END -> {
                val info = infoById[actionTouch.id]
                if (info != null) {
                    infoById.remove(info.id)
                    end(info.copyFrom(actionTouch))
                    infos.free(info)
                }
            }
            TouchEvent.Type.MOVE -> {
                e.touches.fastForEach {
                    val info = infoById[it.id]
                    if (info != null) {
                        move(info.copyFrom(it))
                    }
                }
            }
        }
    }
}

fun View.touch(block: TouchEvents.() -> Unit) {
    block(getOrCreateComponentTouch { TouchEvents(this) })
}
