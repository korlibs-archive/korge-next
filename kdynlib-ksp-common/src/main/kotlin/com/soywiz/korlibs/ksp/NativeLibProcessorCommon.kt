package com.soywiz.korlibs.ksp

import com.google.devtools.ksp.*
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

enum class NativeLibProcessorTarget { DUMMY, JVM, NATIVE, METADATA }

class NativeLibProcessorCommon(
    val environment: SymbolProcessorEnvironment,
    val target: NativeLibProcessorTarget
) : SymbolProcessor {
    val codeGenerator: CodeGenerator = environment.codeGenerator
    val logger: KSPLogger = environment.logger

    val isDummy get() = target == NativeLibProcessorTarget.DUMMY
    val isJvm get() = target == NativeLibProcessorTarget.JVM
    val isNative get() = target == NativeLibProcessorTarget.NATIVE
    val isMetadata get() = target == NativeLibProcessorTarget.METADATA

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("com.soywiz.kdynlib.NativeLibrary")
        val ret = arrayListOf<KSAnnotated>()
        //logger.error("${symbols.toList()} : ${ret}")

        for (symbol in symbols) {
            if (!symbol.validate()) {
                ret.add(symbol)
                continue
            }
            symbol.accept(
                object : KSVisitorVoid() {
                    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
                        val packageName = classDeclaration.packageName.asString()
                        val interfaceName = classDeclaration.simpleName.getShortName()
                        val className = "${interfaceName}Impl"
                        val file = codeGenerator.createNewFile(
                            Dependencies(true, classDeclaration.containingFile!!),
                            packageName,
                            className
                        )

                        file.appendln("package $packageName")
                        file.appendln("")
                        file.appendln("import ${classDeclaration.qualifiedName?.asString()}")
                        file.appendln("")

                        when {
                            isJvm -> {
                                file.appendln("internal class $className(val libName: String) : $interfaceName by com.sun.jna.Native.load(libName, $interfaceName::class.java)")
                                file.appendln("internal operator fun com.soywiz.kdynlib.LibraryCompanion<$interfaceName>.invoke(name: String): $interfaceName = $className(name)")
                            }
                            isDummy -> {
                                file.appendln("internal operator fun com.soywiz.kdynlib.LibraryCompanion<$interfaceName>.invoke(name: String): $interfaceName = TODO(\"Can't instantiate '$interfaceName' in this target\")")
                            }
                            isNative -> {
                                file.appendln("import kotlinx.cinterop.*")
                                file.appendln("internal class $className(val libName: String) : $interfaceName {")
                                file.appendln("  inline private fun com.soywiz.kdynlib.VoidPtr.convert() = this.toLong().toCPointer<CPointed>()")
                                file.appendln("  inline private fun COpaquePointer.convert() = this.rawValue")
                                run {
                                    file.appendln("  val __LIB__ = com.soywiz.kdynlib.DynamicLibrary(libName)\n")
                                    for (func in classDeclaration.getDeclaredFunctions()) {
                                        val funcName = func.simpleName.getShortName()
                                        val paramsStr = func.parameters.map { "${it.name?.getShortName()}: ${it.type.toMyType().type}" }.joinToString(", ")
                                        val paramsLambdaStr = func.parameters.map { "${it.name?.getShortName()}: ${it.type.toMyType().interopType}" }.joinToString(", ")
                                        val paramNamesStr = func.parameters.map {
                                            val paramType = it.type.toMyType()
                                            buildString {
                                                append("${it.name?.getShortName()}")
                                                if (paramType.isPointer) {
                                                    if (paramType.isNullable) append("?")
                                                    append(".convert()")
                                                }
                                            }
                                        }.joinToString(", ")
                                        val retType = func.returnType.toMyType()
                                        val retTypeStr = retType.type
                                        val retTypeLambdaStr = retType.interopType
                                        file.appendln("  val __LIB__$funcName by __LIB__.func<($paramsLambdaStr) -> $retTypeLambdaStr>(\"$funcName\")")

                                        val funcCall = buildString {
                                            append("__LIB__$funcName($paramNamesStr)")
                                            if (retType.isPointer) {
                                                if (retType.isNullable) append("?")
                                                append(".convert()")
                                            }
                                        }

                                        file.appendln("  override fun $funcName($paramsStr): $retTypeStr = $funcCall")
                                    }
                                    file.appendln("}")
                                }
                                file.appendln("internal operator fun com.soywiz.kdynlib.LibraryCompanion<$interfaceName>.invoke(name: String): $interfaceName = $className(name)")
                            }
                            isMetadata -> {
                                file.appendln("internal expect operator fun com.soywiz.kdynlib.LibraryCompanion<$interfaceName>.invoke(name: String): $interfaceName")
                            }
                        }
                    }
                }, Unit
            )
        }

        return ret
    }
}
