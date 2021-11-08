package com.soywiz.korlibs.ksp

import com.google.devtools.ksp.*
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

class NativeLibProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        val targetStr = environment.options["kotlinTarget"] ?: error("Can't find 'kotlinTarget'")
        val kindStr = environment.options["kotlinTargetKind"] ?: error("Can't find 'kotlinTargetKind'")
        return NativeLibProcessorCommon(environment, NativeLibProcessorTarget.fromString(targetStr, kindStr))
    }
}

enum class NativeLibProcessorTarget {
    DUMMY, JVM, JS, NATIVE, ANDROID, METADATA;

    companion object {
        fun fromString(target: String, kind: String) = when (kind.lowercase().trim()) {
            "jvm" -> when (target) {
                "android" -> ANDROID
                else -> JVM
            }
            "js" -> JS
            "native" -> NATIVE
            "metadata" -> METADATA
            "android" -> ANDROID
            else -> DUMMY
        }
    }
}

class NativeLibProcessorCommon(
    val environment: SymbolProcessorEnvironment,
    val target: NativeLibProcessorTarget
) : SymbolProcessor {
    val codeGenerator: CodeGenerator = environment.codeGenerator
    val logger: KSPLogger = environment.logger

    val isDummy get() = target == NativeLibProcessorTarget.DUMMY
    val isJs get() = target == NativeLibProcessorTarget.JS
    val isJvm get() = target == NativeLibProcessorTarget.JVM
    val isAndroid get() = target == NativeLibProcessorTarget.ANDROID
    val isNative get() = target == NativeLibProcessorTarget.NATIVE
    val isMetadata get() = target == NativeLibProcessorTarget.METADATA

    override fun process(resolver: Resolver): List<KSAnnotated> {
        //val decls = arrayListOf<>()
        val symbols = LinkedHashSet<KSAnnotated>()

        //for (file in resolver.getAllFiles()) {
        //for (file in resolver.getNewFiles()) {
        //    for (decl in file.declarations.toList()) {
        //        if (!decl.annotations.any { it.shortName.getShortName() == "NativeLibrary" }) continue
        //        logger.warn("file: $file, decl: $decl, annotations=${decl.annotations.toList().map { it.annotationType.toMyType().type }}")
        //        symbols.add(decl)
        //    }
        //}

        symbols.addAll(resolver.getSymbolsWithAnnotation("com.soywiz.kdynlib.NativeLibrary"))

        val ret = arrayListOf<KSAnnotated>()
        //logger.error("${symbols.toList()} : ${ret} : options=${environment.options}, target=$target : ${resolver.getAllFiles().toList()}")

        for (symbol in symbols) {
            val symbolValidate = symbol.validate()
            //logger.error("$symbol, $symbolValidate")
            if (!symbolValidate) {
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
                            }
                            isDummy || isJs || isAndroid || isMetadata -> {
                                file.appendln("internal class $className(val libName: String) : $interfaceName by TODO()")
                            }
                            isNative -> {
                                file.appendln("import com.soywiz.kdynlib.func")
                                file.appendln("import com.soywiz.kdynlib.funcNull")
                                file.appendln("import kotlinx.cinterop.*")
                                file.appendln("internal class $className(val __LIB__: com.soywiz.kdynlib.DynamicSymbolResolver) : $interfaceName {")
                                file.appendln("  constructor(name: String) : this(com.soywiz.kdynlib.DynamicLibrary(name))")
                                file.appendln("  private inline fun com.soywiz.kdynlib.VoidPtr.convert() = this.toLong().toCPointer<CPointed>()")
                                file.appendln("  private inline fun COpaquePointer.convert() = this.rawValue")
                                run {
                                    for (func in classDeclaration.getDeclaredFunctions()) {
                                        val funcName = func.simpleName.getShortName()
                                        val paramsStr = func.parameters.joinToString(", ") { "${it.name?.getShortName()}: ${it.type.toMyType().type}" }
                                        val paramsLambdaStr = func.parameters.joinToString(", ") { "${it.name?.getShortName()}: ${it.type.toMyType().interopType}" }
                                        val paramNamesStr = func.parameters.joinToString(", ") {
                                            val paramType = it.type.toMyType()
                                            buildString {
                                                append("${it.name?.getShortName()}")
                                                if (paramType.isPointer) {
                                                    if (paramType.isNullable) append("?")
                                                    append(".convert()")
                                                }
                                            }
                                        }
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
                                //file.appendln("internal operator fun com.soywiz.kdynlib.LibraryCompanion<$interfaceName>.invoke(name: String): $interfaceName = $className(com.soywiz.kdynlib.DynamicLibrary(name))")
                                //file.appendln("internal operator fun com.soywiz.kdynlib.LibraryCompanion<$interfaceName>.invoke(resolver: com.soywiz.kdynlib.DynamicSymbolResolver): $interfaceName = $className(resolver)")
                            }
                            else -> {
                                logger.error("${symbols.toList()} : options=${environment.options}, unsupported target=$target")
                            }
                        }
                    }
                }, Unit
            )
        }

        return ret
    }
}
