package com.soywiz.korag.shaders

import com.soywiz.korag.*
import com.soywiz.korag.shader.*
import com.soywiz.korag.shader.gl.*
import com.soywiz.korio.util.*
import kotlin.test.*

class ShadersTest {
	@Test
	fun testGlslGeneration() {
		val vs = VertexShader {
			IF(true.lit) {
				DefaultShaders.t_Temp0 setTo 1.lit * 2.lit
			} ELSE {
				DefaultShaders.t_Temp0 setTo 3.lit * 4.lit
			}
		}

		// @TODO: Optimizer phase!
        assertEqualsShader(vs) {
            "void main()" {
                +"vec4 temp0;"
                "if (true)" {
                    +"temp0 = (1 * 2);"
                }
                "else" {
                    +"temp0 = (3 * 4);"
                }
            }
        }
	}

    @Test
    fun testGlslGenerationRaw() {
        val vs = VertexShaderRawGlSl("hello")

        assertEquals("hello", vs.toNewGlslString(GlslConfig()))
    }

    val fs = FragmentShader {
        DefaultShaders.apply {
            out setTo vec4(1.lit, 0.lit, 0.lit, 1.lit)
        }
    }

    @Test
    fun testGlslFragmentGenerationOld() {
        assertEqualsShader(fs, version = 100) {
            line("void main()") {
                line("gl_FragColor = vec4(1, 0, 0, 1);")
            }
        }
    }

    @Test
    fun testGlslFragmentGenerationNew() {
        assertEqualsShader(fs, version = 330) {
            line("void main()") {
                line("gl_FragColor = vec4(1, 0, 0, 1);")
            }
            //+"layout(location = 0) out vec4 fragColor;"
            //"void main()" {
            //    +"fragColor = vec4(1, 0, 0, 1);"
            //}
        }
    }

    @Test
    fun testGlslFragmentGenerationNewCustomFunc() {
        assertEqualsShader(FragmentShader {
            DefaultShaders.apply {
                // This is discarded
                FUNC("demo", Float1, "x" to Float1, "y" to Float1) {
                    RETURN(ARG("x", Float1) + ARG("y", Float1) * 2.lit)
                }
                // Latest function with this name is used
                val demo = FUNC("demo", Float1, "x" to Float1, "y" to Float1) {
                    val x = ARG("x", Float1)
                    val y = ARG("y", Float1)
                    RETURN(x + y + 2.lit)
                }

                out setTo vec4(1.lit, 0.lit, 0.lit, demo(0.lit, 1.lit))
            }
        }, version = 330) {
            line("float demo(float x, float y)") {
                line("return ((x + y) + 2);")
            }
            line("void main()") {
                line("gl_FragColor = vec4(1, 0, 0, demo(0, 1));")
            }
            //+"layout(location = 0) out vec4 fragColor;"
            //"void main()" {
            //    +"fragColor = vec4(1, 0, 0, 1);"
            //}
        }
    }

    fun assertEqualsShader(shader: Shader, version: Int = GlslGenerator.DEFAULT_VERSION, gles: Boolean = false, block: Indenter.() -> Unit) {
        assertEquals(
            Indenter {
                block()
            }.toString(),
            shader.toNewGlslStringResult(gles = gles, version = version).result
        )
    }

    @Test
    fun testGlslEquality() {
        fun genFragment(sub: Int) = FragmentShader {
            DefaultShaders {
                out setTo vec4(0f.lit, 0f.lit, 0f.lit, 0f.lit)

                for (y in 0 until 3) {
                    for (x in 0 until 3) {
                        out setTo out + (tex(
                            fragmentCoords + vec2(
                                (x - sub).toFloat().lit,
                                (y - sub).toFloat().lit
                            )
                        )) * u_Weights[x][y]
                    }
                }
            }
        }

        assertEquals(genFragment(1), genFragment(1))
        assertNotEquals(genFragment(1), genFragment(0))
        assertEquals(1, LinkedHashSet<FragmentShader>().apply {
            add(genFragment(1))
            add(genFragment(1))
        }.size)
    }

    @Test
    fun testRegression() {
        assertEquals(Uniform("test1", VarType.Float1), Uniform("test1", VarType.Float1))
        assertNotEquals(Uniform("test1", VarType.Float1), Uniform("test2", VarType.Float1))
    }

    private val u_Weights = Uniform("weights", VarType.Mat3)
    val u_TextureSize = Uniform("effectTextureSize", VarType.Float2)
    val Program.Builder.fragmentCoords01 get() = DefaultShaders.v_Tex["xy"]
    val Program.Builder.fragmentCoords get() = fragmentCoords01 * u_TextureSize
    fun Program.Builder.tex(coords: Operand) = texture2D(DefaultShaders.u_Tex, coords / u_TextureSize)
}
