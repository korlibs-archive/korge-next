package com.soywiz.kdynlib

import com.tschuchort.compiletesting.*
import org.jetbrains.kotlin.compiler.plugin.*
import kotlin.test.*

class IrPluginTest {
    @Test
    fun `IR plugin success`() {
        val result = compile(
            sourceFiles = listOf(
                SourceFile.kotlin("demo2.kt", """
                    package com.sun.jna
                    object Native {
                        @JvmStatic fun register(name: String): Unit = TODO()
                    }


                """.trimIndent()),
                SourceFile.kotlin("demo.kt", """
                    package com.soywiz.kdynlib
                    annotation class NativeLibrary
                    abstract class BaseLibrary(val name: String)
                    object NativeLibraryJvm {
                        @JvmStatic fun register(clazz: Any, name: String) {
                            //Native.register(clazz, name)
                        }
                    }
                """.trimIndent()),
                SourceFile.kotlin(
                    "main.kt", """
                        import com.soywiz.kdynlib.*
                        import com.sun.jna.*

                        @com.soywiz.kdynlib.NativeLibrary
                        object Demo : com.soywiz.kdynlib.BaseLibrary("Kernel32.dll") {
                            init {
                                println(this.name)
                            }
                            external fun demo(): Int
                            fun test(): Int {
                                return 10
                            }
                            fun lol() {
                                Native.register("test")
                            }                        
                            init {
                                Native.register(this.name)
                            }
                        }
                        fun main() {
                          println(debug())
                            println(Demo::class.java)
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
