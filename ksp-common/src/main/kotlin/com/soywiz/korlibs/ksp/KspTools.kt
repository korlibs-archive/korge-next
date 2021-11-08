package com.soywiz.korlibs.ksp

import com.google.devtools.ksp.symbol.*
import java.io.*

fun OutputStream.append(str: String) = this.write(str.toByteArray())
fun OutputStream.appendln(str: String) = append("$str\n")

fun KSTypeReference?.toMyType(): MyType {
    val type = this ?: return MyType("kotlin.Unit")
    val resolvedType = type.resolve()
    val typeName = StringBuilder(resolvedType.declaration.qualifiedName?.asString() ?: "<ERROR>")
    val typeArgs = type.element!!.typeArguments
    if (type.element!!.typeArguments.isNotEmpty()) {
        typeName.append("<")
        typeName.append(
            typeArgs.map {
                val type = it.type?.resolve()
                "${it.variance.label} ${type?.declaration?.qualifiedName?.asString() ?: "ERROR"}" +
                    if (type?.nullability == Nullability.NULLABLE) "?" else ""
            }.joinToString(", ")
        )
        typeName.append(">")
    }
    if (resolvedType.isMarkedNullable) {
        typeName.append("?")
    }
    return MyType(typeName.toString())
}

data class MyType(val type: String) {
    val interopType = when (type) {
        "com.soywiz.kmem.lib.VoidPtr?" -> "kotlinx.cinterop.COpaquePointer?"
        "com.soywiz.kmem.lib.VoidPtr" -> "kotlinx.cinterop.COpaquePointer"
        else -> type
    }
    val isNullable get()= type.endsWith("?")
    val isPointer get() = type.startsWith("com.soywiz.kmem.lib.VoidPtr")

    override fun toString(): String = interopType
}
