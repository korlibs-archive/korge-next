// WARNING: File autogenerated DO NOT modify
// https://www.khronos.org/registry/OpenGL/api/GLES2/gl2.h
@file:Suppress("unused", "RedundantUnitReturnType", "PropertyName")

package com.soywiz.kgl

import com.soywiz.kmem.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korio.lang.*

object KmlGlDummy : KmlGlDummyBase()

open class KmlGlDummyBase : KmlGl() {
    enum class Kind { PROGRAM, SHADER, BUFFER, FRAME_BUFFER, RENDER_BUFFER, TEXTURE }
    val ids = LinkedHashMap<Kind, Int>()

    fun alloc(kind: Kind): Int {
        ids.getOrPut(kind) { 0 }
        return ids.getOrPut(kind){ ids[kind]!! +1 }
    }

    override fun activeTexture(texture: Int): Unit = Unit
    override fun attachShader(program: Int, shader: Int): Unit = Unit
    override fun bindAttribLocation(program: Int, index: Int, name: String): Unit = Unit
    override fun bindBuffer(target: Int, buffer: Int): Unit = Unit
    override fun bindFramebuffer(target: Int, framebuffer: Int): Unit = Unit
    override fun bindRenderbuffer(target: Int, renderbuffer: Int): Unit = Unit
    override fun bindTexture(target: Int, texture: Int): Unit = Unit
    override fun blendColor(red: Float, green: Float, blue: Float, alpha: Float): Unit = Unit
    override fun blendEquation(mode: Int): Unit = Unit
    override fun blendEquationSeparate(modeRGB: Int, modeAlpha: Int): Unit = Unit
    override fun blendFunc(sfactor: Int, dfactor: Int): Unit = Unit
    override fun blendFuncSeparate(sfactorRGB: Int, dfactorRGB: Int, sfactorAlpha: Int, dfactorAlpha: Int): Unit = Unit
    override fun bufferData(target: Int, size: Int, data: FBuffer, usage: Int): Unit = Unit
    override fun bufferSubData(target: Int, offset: Int, size: Int, data: FBuffer): Unit = Unit
    override fun checkFramebufferStatus(target: Int): Int = 0
    override fun clear(mask: Int): Unit = Unit
    override fun clearColor(red: Float, green: Float, blue: Float, alpha: Float): Unit = Unit
    override fun clearDepthf(d: Float): Unit = Unit
    override fun clearStencil(s: Int): Unit = Unit
    override fun colorMask(red: Boolean, green: Boolean, blue: Boolean, alpha: Boolean): Unit = Unit
    override fun compileShader(shader: Int): Unit = Unit
    override fun compressedTexImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, imageSize: Int, data: FBuffer): Unit = Unit
    override fun compressedTexSubImage2D(target: Int, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int, format: Int, imageSize: Int, data: FBuffer): Unit = Unit
    override fun copyTexImage2D(target: Int, level: Int, internalformat: Int, x: Int, y: Int, width: Int, height: Int, border: Int): Unit = Unit
    override fun copyTexSubImage2D(target: Int, level: Int, xoffset: Int, yoffset: Int, x: Int, y: Int, width: Int, height: Int): Unit = Unit
    override fun createProgram(): Int = alloc(Kind.PROGRAM)
    override fun createShader(type: Int): Int = alloc(Kind.SHADER)
    override fun cullFace(mode: Int): Unit = Unit
    override fun deleteBuffers(n: Int, items: FBuffer): Unit = Unit
    override fun deleteFramebuffers(n: Int, items: FBuffer): Unit = Unit
    override fun deleteProgram(program: Int): Unit = Unit
    override fun deleteRenderbuffers(n: Int, items: FBuffer): Unit = Unit
    override fun deleteShader(shader: Int): Unit = Unit
    override fun deleteTextures(n: Int, items: FBuffer): Unit = Unit
    override fun depthFunc(func: Int): Unit = Unit
    override fun depthMask(flag: Boolean): Unit = Unit
    override fun depthRangef(n: Float, f: Float): Unit = Unit
    override fun detachShader(program: Int, shader: Int): Unit = Unit
    override fun disable(cap: Int): Unit = Unit
    override fun disableVertexAttribArray(index: Int): Unit = Unit
    override fun drawArrays(mode: Int, first: Int, count: Int): Unit = Unit
    override fun drawElements(mode: Int, count: Int, type: Int, indices: Int): Unit = Unit
    override fun enable(cap: Int): Unit = Unit
    override fun enableVertexAttribArray(index: Int): Unit = Unit
    override fun finish(): Unit = Unit
    override fun flush(): Unit = Unit
    override fun framebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: Int): Unit = Unit
    override fun framebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: Int, level: Int): Unit = Unit
    override fun frontFace(mode: Int): Unit = Unit

    private fun gen(n: Int, buffer: FBuffer, kind: Kind) {
        for (i in 0 until n) buffer.i32[i] = alloc(kind)
    }

    override fun genBuffers(n: Int, buffers: FBuffer): Unit = gen(n, buffers, Kind.BUFFER)
    override fun generateMipmap(target: Int): Unit = Unit
    override fun genFramebuffers(n: Int, framebuffers: FBuffer): Unit = gen(n, framebuffers, Kind.FRAME_BUFFER)
    override fun genRenderbuffers(n: Int, renderbuffers: FBuffer): Unit = gen(n, renderbuffers, Kind.RENDER_BUFFER)
    override fun genTextures(n: Int, textures: FBuffer): Unit = gen(n, textures, Kind.TEXTURE)

    override fun getActiveAttrib(program: Int, index: Int, bufSize: Int, length: FBuffer, size: FBuffer, type: FBuffer, name: FBuffer): Unit = Unit
    override fun getActiveUniform(program: Int, index: Int, bufSize: Int, length: FBuffer, size: FBuffer, type: FBuffer, name: FBuffer): Unit = Unit
    override fun getAttachedShaders(program: Int, maxCount: Int, count: FBuffer, shaders: FBuffer): Unit = Unit
    override fun getAttribLocation(program: Int, name: String): Int = 0
    override fun getUniformLocation(program: Int, name: String): Int = 0
    override fun getBooleanv(pname: Int, data: FBuffer): Unit = Unit
    override fun getBufferParameteriv(target: Int, pname: Int, params: FBuffer): Unit = Unit
    override fun getError(): Int = 0
    override fun getFloatv(pname: Int, data: FBuffer): Unit = Unit
    override fun getFramebufferAttachmentParameteriv(target: Int, attachment: Int, pname: Int, params: FBuffer): Unit = Unit
    override fun getIntegerv(pname: Int, data: FBuffer): Unit = Unit
    override fun getProgramInfoLog(program: Int, bufSize: Int, length: FBuffer, infoLog: FBuffer): Unit = Unit
    override fun getRenderbufferParameteriv(target: Int, pname: Int, params: FBuffer): Unit = Unit
    override fun getProgramiv(program: Int, pname: Int, params: FBuffer): Unit = Unit
    override fun getShaderiv(shader: Int, pname: Int, params: FBuffer): Unit {
        when (pname) {
            COMPILE_STATUS -> params.i32[0] = GTRUE
        }
    }
    override fun getShaderInfoLog(shader: Int, bufSize: Int, length: FBuffer, infoLog: FBuffer): Unit = Unit
    override fun getShaderPrecisionFormat(shadertype: Int, precisiontype: Int, range: FBuffer, precision: FBuffer): Unit = Unit
    override fun getShaderSource(shader: Int, bufSize: Int, length: FBuffer, source: FBuffer): Unit = Unit
    override fun getString(name: Int): String = ""
    override fun getTexParameterfv(target: Int, pname: Int, params: FBuffer): Unit = Unit
    override fun getTexParameteriv(target: Int, pname: Int, params: FBuffer): Unit = Unit
    override fun getUniformfv(program: Int, location: Int, params: FBuffer): Unit = Unit
    override fun getUniformiv(program: Int, location: Int, params: FBuffer): Unit = Unit
    override fun getVertexAttribfv(index: Int, pname: Int, params: FBuffer): Unit = Unit
    override fun getVertexAttribiv(index: Int, pname: Int, params: FBuffer): Unit = Unit
    override fun getVertexAttribPointerv(index: Int, pname: Int, pointer: FBuffer): Unit = Unit
    override fun hint(target: Int, mode: Int): Unit = Unit
    override fun isBuffer(buffer: Int): Boolean = false
    override fun isEnabled(cap: Int): Boolean = false
    override fun isFramebuffer(framebuffer: Int): Boolean = false
    override fun isProgram(program: Int): Boolean = false
    override fun isRenderbuffer(renderbuffer: Int): Boolean = false
    override fun isShader(shader: Int): Boolean = false
    override fun isTexture(texture: Int): Boolean = false
    override fun lineWidth(width: Float): Unit = Unit
    override fun linkProgram(program: Int): Unit = Unit
    override fun pixelStorei(pname: Int, param: Int): Unit = Unit
    override fun polygonOffset(factor: Float, units: Float): Unit = Unit
    override fun readPixels(x: Int, y: Int, width: Int, height: Int, format: Int, type: Int, pixels: FBuffer): Unit = Unit
    override fun releaseShaderCompiler(): Unit = Unit
    override fun renderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int): Unit = Unit
    override fun sampleCoverage(value: Float, invert: Boolean): Unit = Unit
    override fun scissor(x: Int, y: Int, width: Int, height: Int): Unit = Unit
    override fun shaderBinary(count: Int, shaders: FBuffer, binaryformat: Int, binary: FBuffer, length: Int): Unit = Unit
    override fun shaderSource(shader: Int, string: String): Unit = Unit
    override fun stencilFunc(func: Int, ref: Int, mask: Int): Unit = Unit
    override fun stencilFuncSeparate(face: Int, func: Int, ref: Int, mask: Int): Unit = Unit
    override fun stencilMask(mask: Int): Unit = Unit
    override fun stencilMaskSeparate(face: Int, mask: Int): Unit = Unit
    override fun stencilOp(fail: Int, zfail: Int, zpass: Int): Unit = Unit
    override fun stencilOpSeparate(face: Int, sfail: Int, dpfail: Int, dppass: Int): Unit = Unit
    override fun texImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, pixels: FBuffer?): Unit = Unit
    override fun texImage2D(target: Int, level: Int, internalformat: Int, format: Int, type: Int, data: NativeImage): Unit = Unit
    override fun texParameterf(target: Int, pname: Int, param: Float): Unit = Unit
    override fun texParameterfv(target: Int, pname: Int, params: FBuffer): Unit = Unit
    override fun texParameteri(target: Int, pname: Int, param: Int): Unit = Unit
    override fun texParameteriv(target: Int, pname: Int, params: FBuffer): Unit = Unit
    override fun texSubImage2D(target: Int, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int, format: Int, type: Int, pixels: FBuffer): Unit = Unit
    override fun uniform1f(location: Int, v0: Float): Unit = Unit
    override fun uniform1fv(location: Int, count: Int, value: FBuffer): Unit = Unit
    override fun uniform1i(location: Int, v0: Int): Unit = Unit
    override fun uniform1iv(location: Int, count: Int, value: FBuffer): Unit = Unit
    override fun uniform2f(location: Int, v0: Float, v1: Float): Unit = Unit
    override fun uniform2fv(location: Int, count: Int, value: FBuffer): Unit = Unit
    override fun uniform2i(location: Int, v0: Int, v1: Int): Unit = Unit
    override fun uniform2iv(location: Int, count: Int, value: FBuffer): Unit = Unit
    override fun uniform3f(location: Int, v0: Float, v1: Float, v2: Float): Unit = Unit
    override fun uniform3fv(location: Int, count: Int, value: FBuffer): Unit = Unit
    override fun uniform3i(location: Int, v0: Int, v1: Int, v2: Int): Unit = Unit
    override fun uniform3iv(location: Int, count: Int, value: FBuffer): Unit = Unit
    override fun uniform4f(location: Int, v0: Float, v1: Float, v2: Float, v3: Float): Unit = Unit
    override fun uniform4fv(location: Int, count: Int, value: FBuffer): Unit = Unit
    override fun uniform4i(location: Int, v0: Int, v1: Int, v2: Int, v3: Int): Unit = Unit
    override fun uniform4iv(location: Int, count: Int, value: FBuffer): Unit = Unit
    override fun uniformMatrix2fv(location: Int, count: Int, transpose: Boolean, value: FBuffer): Unit = Unit
    override fun uniformMatrix3fv(location: Int, count: Int, transpose: Boolean, value: FBuffer): Unit = Unit
    override fun uniformMatrix4fv(location: Int, count: Int, transpose: Boolean, value: FBuffer): Unit = Unit
    override fun useProgram(program: Int): Unit = Unit
    override fun validateProgram(program: Int): Unit = Unit
    override fun vertexAttrib1f(index: Int, x: Float): Unit = Unit
    override fun vertexAttrib1fv(index: Int, v: FBuffer): Unit = Unit
    override fun vertexAttrib2f(index: Int, x: Float, y: Float): Unit = Unit
    override fun vertexAttrib2fv(index: Int, v: FBuffer): Unit = Unit
    override fun vertexAttrib3f(index: Int, x: Float, y: Float, z: Float): Unit = Unit
    override fun vertexAttrib3fv(index: Int, v: FBuffer): Unit = Unit
    override fun vertexAttrib4f(index: Int, x: Float, y: Float, z: Float, w: Float): Unit = Unit
    override fun vertexAttrib4fv(index: Int, v: FBuffer): Unit = Unit
    override fun vertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, pointer: Long): Unit = Unit
    override fun viewport(x: Int, y: Int, width: Int, height: Int): Unit = Unit

    override fun drawArraysInstanced(mode: Int, first: Int, count: Int, instancecount: Int): Unit = Unit
    override fun drawElementsInstanced(mode: Int, count: Int, type: Int, indices: Int, instancecount: Int): Unit = Unit
    override fun vertexAttribDivisor(index: Int, divisor: Int): Unit = Unit
}
