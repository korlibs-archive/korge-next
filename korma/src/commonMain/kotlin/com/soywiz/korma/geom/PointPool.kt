package com.soywiz.korma.geom

@Suppress("NOTHING_TO_INLINE")
class PointPool(val size: Int) {
    @PublishedApi
    internal val points = Array(size) { com.soywiz.korma.geom.Point() }
    @PublishedApi
    internal var offset = 0

    @PublishedApi
    internal fun alloc(): Point = points[offset++]

    fun MPoint(): Point = alloc()
    fun Point(x: Double, y: Double): IPoint = alloc().setTo(x, y)
    fun Point(x: Float, y: Float): IPoint = Point(x.toDouble(), y.toDouble())
    fun Point(x: Int, y: Int): IPoint = Point(x.toDouble(), y.toDouble())
    fun Point(): IPoint = Point(0.0, 0.0)
    fun Point(angle: Angle, length: Double = 1.0): IPoint = Point.fromPolar(angle, length, alloc())
    fun Point(base: IPoint, angle: Angle, length: Double = 1.0): IPoint = Point.fromPolar(base, angle, length, alloc())
    fun Point(angle: Angle, length: Float = 1f): IPoint = Point.fromPolar(angle, length.toDouble(), alloc())
    fun Point(base: IPoint, angle: Angle, length: Float = 1f): IPoint = Point.fromPolar(base, angle, length.toDouble(), alloc())

    operator fun IPoint.plus(other: IPoint): IPoint = alloc().setToAdd(this, other)
    operator fun IPoint.minus(other: IPoint): IPoint = alloc().setToSub(this, other)

    operator fun IPoint.times(value: IPoint): IPoint = alloc().setToMul(this, value)
    operator fun IPoint.times(value: Double): IPoint = alloc().setToMul(this, value)
    operator fun IPoint.times(value: Float): IPoint = this * value.toDouble()
    operator fun IPoint.times(value: Int): IPoint = this * value.toDouble()

    operator fun IPoint.div(value: IPoint): IPoint = alloc().setToDiv(this, value)
    operator fun IPoint.div(value: Double): IPoint = alloc().setToDiv(this, value)
    operator fun IPoint.div(value: Float): IPoint = this / value.toDouble()
    operator fun IPoint.div(value: Int): IPoint = this / value.toDouble()

    operator fun IPoint.rem(value: IPoint): IPoint = Point(this.x % value.x, this.y % value.y)
    operator fun IPoint.rem(value: Double): IPoint = Point(this.x % value, this.y % value)
    operator fun IPoint.rem(value: Float): IPoint = this % value.toDouble()
    operator fun IPoint.rem(value: Int): IPoint = this % value.toDouble()

    operator fun IPointArrayList.get(index: Int): IPoint = Point(this.getX(index), this.getY(index))

    inline operator fun invoke(callback: PointPool.() -> Unit) {
        val oldOffset = offset
        try {
            callback()
        } finally {
            offset = oldOffset
        }
    }
}
