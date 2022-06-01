package com.soywiz.korma.geom

import com.soywiz.kds.DoubleArrayList
import com.soywiz.kds.Extra

interface IVectorArrayList : Extra {
    val closed: Boolean
    val size: Int
    val dimensions: Int
    fun get(index: Int, dim: Int): Double
}

class VectorArrayList(
    override val dimensions: Int,
    capacity: Int = 6,
) : IVectorArrayList, Extra by Extra.Mixin() {
    val data = DoubleArrayList(capacity * dimensions)

    override var closed: Boolean = false
    override val size: Int = data.size / dimensions

    override fun get(index: Int, dim: Int): Double = data[index * dimensions + dim]
    fun set(index: Int, dim: Int, value: Double) {
        data[index * dimensions + dim] = value
    }
    fun set(index: Int, vararg values: Double) {
        val rindex = index * dimensions
        for (n in 0 until dimensions) data[rindex + n] = values[n]
    }
    fun add(vararg values: Double) {
        if (values.size != dimensions) error("Invalid dimensions ${values.size} != $dimensions")
        data.add(values)
    }
}
