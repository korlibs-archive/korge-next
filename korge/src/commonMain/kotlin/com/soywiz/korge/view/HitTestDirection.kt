package com.soywiz.korge.view

import com.soywiz.kds.iterators.*
import com.soywiz.korma.geom.*

enum class HitTestDirection {
    ANY, UP, RIGHT, DOWN, LEFT;

    companion object {
        fun fromPoint(point: Point): HitTestDirection {
            if (point.x == 0.0 && point.y == 0.0) return ANY
            return fromAngle(Point.Zero.angleTo(point))
        }
        fun fromAngle(angle: Angle): HitTestDirection {
            val quadrant = ((angle + 45.degrees) / 90.degrees).toInt()
            return when (quadrant) {
                0 -> HitTestDirection.RIGHT
                1 -> HitTestDirection.DOWN
                2 -> HitTestDirection.LEFT
                3 -> HitTestDirection.UP
                else -> HitTestDirection.RIGHT
            }
        }
    }
}

interface HitTestable {
    fun hitTestAny(x: Double, y: Double, direction: HitTestDirection = HitTestDirection.ANY): Boolean
}

fun List<HitTestable>.toHitTestable(): HitTestable {
    val list = this
    return object : HitTestable {
        override fun hitTestAny(x: Double, y: Double, direction: HitTestDirection): Boolean {
            list.fastForEach { if (it.hitTestAny(x, y, direction)) return true }
            return false
        }
    }
}

private val MOVE_ANGLES = arrayOf(0.degrees, 5.degrees, 10.degrees, 15.degrees, 20.degrees, 30.degrees, 45.degrees, 60.degrees, 80.degrees, 85.degrees)
private val MOVE_SCALES = arrayOf(+1.0, -1.0)

fun View.moveWithCollision(collision: HitTestable, dx: Double, dy: Double) {
    val char = this
    val deltaXY = Point(dx, dy)
    val angle = Angle.between(0.0, 0.0, deltaXY.x, deltaXY.y)
    val length = deltaXY.length
    val oldX = char.x
    val oldY = char.y
    MOVE_ANGLES.fastForEach { dangle ->
        MOVE_SCALES.fastForEach { dscale ->
            val rangle = angle + dangle * dscale
            val lengthScale = dangle.cosine
            val dpoint = Point.fromPolar(rangle, length * lengthScale)
            char.x = oldX + dpoint.x
            char.y = oldY + dpoint.y
            val hitTestDirection = HitTestDirection.fromAngle(angle)
            if (!collision.hitTestAny(char.globalX, char.globalY, hitTestDirection)) {
                return // Accept movement
            }
        }
    }
    char.x = oldX
    char.y = oldY
}
