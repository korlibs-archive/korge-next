package com.soywiz.korma.geom

import com.soywiz.kds.DoubleArrayList
import com.soywiz.kds.Extra

interface IVectorArrayList : Extra {
    val closed: Boolean
    val size: Int
    val dimensions: Int
    fun get(index: Int, dim: Int): Double
}

fun IVectorArrayList.getX(index: Int): Double = get(index, 0)
fun IVectorArrayList.getY(index: Int): Double = get(index, 1)
fun IVectorArrayList.getZ(index: Int): Double = get(index, 2)

class VectorArrayList(
    override val dimensions: Int,
    capacity: Int = 7,
) : IVectorArrayList, Extra by Extra.Mixin() {
    val data = DoubleArrayList(capacity * dimensions)

    override var closed: Boolean = false
    override val size: Int get() = data.size / dimensions

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

    fun vectorToStringBuilder(index: Int, out: StringBuilder) {
        out.append("[")
        for (dim in 0 until dimensions) {
            if (dim != 0) out.append(", ")
            out.append(get(index, dim))
        }
        out.append("]")
    }

    fun vectorToString(index: Int): String = buildString { vectorToStringBuilder(index, this) }

    override fun toString(): String = buildString {
        append("VectorArrayList[${this@VectorArrayList.size}](")
        for (n in 0 until this@VectorArrayList.size) {
            if (n != 0) append(", ")
            this@VectorArrayList.vectorToStringBuilder(n, this)
        }
        append(")")
    }
}
