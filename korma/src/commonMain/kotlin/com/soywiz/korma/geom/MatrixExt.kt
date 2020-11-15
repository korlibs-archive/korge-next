package com.soywiz.korma.geom

fun Matrix3D.copyFrom(that: Matrix): Matrix3D = that.toMatrix3D(this)

fun Matrix.toMatrix3D(out: Matrix3D = Matrix3D()): Matrix3D = out.setRows(
    a.toFloat(), c.toFloat(), 0f, tx.toFloat(),
    b.toFloat(), d.toFloat(), 0f, ty.toFloat(),
    0f, 0f, 1f, 0f,
    0f, 0f, 0f, 1f
)
