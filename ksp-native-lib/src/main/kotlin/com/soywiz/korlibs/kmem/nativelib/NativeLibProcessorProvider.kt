package com.soywiz.korlibs.kmem.nativelib

import com.google.devtools.ksp.*
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import java.io.*

class NativeLibProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        //environment.logger.error("${environment.kotlinVersion} ${environment.options}")
        return NativeLibProcessor(environment.codeGenerator, environment.logger)
    }
}

class NativeLibProcessor(
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger
) : SymbolProcessor {
    fun OutputStream.append(str: String) = this.write(str.toByteArray())
    fun OutputStream.appendln(str: String) = append("$str\n")

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("com.soywiz.kmem.lib.NativeLibrary")
        val ret = symbols.filter { !it.validate() }.toList()
        //logger.error("${symbols.toList()} : ${ret}")

        for (symbol in symbols) {
            symbol.accept(
                object : KSVisitorVoid() {
                    fun KSTypeReference?.toFQString(): String {
                        val type = this ?: return "Unit"
                        val typeName = StringBuilder(type.resolve().declaration.qualifiedName?.asString() ?: "<ERROR>")
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
                        return typeName.toString()
                    }

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
                        file.appendln("class $className(val libName: String) : $interfaceName {")
                        file.appendln("  companion object {")
                        file.appendln("    fun register() { com.soywiz.kmem.lib.NativeLibrary.register<$interfaceName> { $className(it) } }")
                        file.appendln("  }")
                        for (func in classDeclaration.getDeclaredFunctions()) {
                            val funcName = func.simpleName.getShortName()
                            val paramsStrs = func.parameters.map { "${it.name?.getShortName()}: ${it.type.toFQString()}" }
                            file.appendln("  override fun $funcName(${paramsStrs.joinToString(", ")}): ${func.returnType.toFQString()} {")
                            file.appendln("    TODO()")
                            file.appendln("  }")

                        }
                        file.appendln("}")
                        //file.appendln("fun kotlin.reflect.KClass<$interfaceName>.register() { $className.register() }")
                        file.appendln("fun kotlin.reflect.KClass<$interfaceName>.get(name: String): $interfaceName = $className(name)")

                        //logger.error("classDeclaration=$classDeclaration, funcs=${classDeclaration.getDeclaredFunctions().toList()}")

                        //super.visitClassDeclaration(classDeclaration, data)
                    }
                }, Unit
            )
        }

        return emptyList()
    }
}
