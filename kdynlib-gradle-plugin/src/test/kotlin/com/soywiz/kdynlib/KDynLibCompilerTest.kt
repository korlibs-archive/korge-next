package com.soywiz.kdynlib

import com.tschuchort.compiletesting.*
import org.jetbrains.kotlin.compiler.plugin.*
import kotlin.test.*

class IrPluginTest {
    @Test
    fun `IR plugin success`() {
        val result = compile(
            sourceFiles = listOf(
                SourceFile.kotlin("demo.kt", """
                    package com.soywiz.kdynlib
                    annotation class NativeLibrary
                """.trimIndent()),
                SourceFile.kotlin(
                    "main.kt", """
                        import com.soywiz.kdynlib.*

                        @NativeLibrary
                        object Demo {
                            external fun demo(): Int
                            fun test(): Int {
                                return 10
                            }
                        }
fun main() {
  println(debug())
}
fun debug() = "Hello, World!"
"""
                )
            )
        )
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }


    fun compile(
        sourceFiles: List<SourceFile>,
        plugin: ComponentRegistrar? = null,
    ): KotlinCompilation.Result {
        return KotlinCompilation().apply {
            sources = sourceFiles
            useIR = true
            compilerPlugins = listOf(plugin ?: KDynLibComponentRegistrar())
            inheritClassPath = true
        }.compile()
    }

    fun compile(
        sourceFile: SourceFile,
        plugin: ComponentRegistrar? = null,
    ): KotlinCompilation.Result = compile(listOf(sourceFile), plugin)

}
