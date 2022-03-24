package com.soywiz.korgw.platform

import com.soywiz.kgl.*
import com.soywiz.kmem.*
import com.soywiz.korim.awt.*
import com.soywiz.korim.bitmap.NativeImage
import com.soywiz.korio.lang.*
import com.soywiz.korio.time.*
import com.soywiz.korio.util.*
import com.sun.jna.*
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.nio.IntBuffer

typealias GLboolean = Byte // Check https://github.com/korlibs/korge/issues/268#issuecomment-729056184 for details
typealias GLbyte = Byte
typealias GLubyte = Byte
typealias GLshort = Short
typealias GLushort = Short
typealias GLint = Int
typealias GLuint = Int
typealias GLfixed = Int
typealias GLint64 = Long
typealias GLuint64 = Long
typealias GLsizei = Int
typealias GLenum = Int
typealias GLintptr = NativeLong
typealias GLsizeiptr = NativeLong
typealias GLsync = NativeLong
typealias GLbitfield = Int
typealias GLhalf = Short // Use com.soywiz.kmem.Float16
typealias GLfloat = Float
typealias GLclampf = Float
typealias GLdouble = Double
typealias GLclampd = Double

typealias IntSize = NativeLong
typealias VoidPtr = ByteBuffer
typealias IntPtr = IntBuffer
typealias FloatPtr = FloatBuffer

internal object GL {
    @JvmStatic external fun glActiveTexture(texture: GLenum)
    @JvmStatic external fun glAttachShader(program: GLuint, shader: GLuint)
    @JvmStatic external fun glBindAttribLocation(program: GLuint, index: GLuint, name: String)
    @JvmStatic external fun glBindBuffer(target: GLenum, buffer: GLuint)
    @JvmStatic external fun glBindFramebuffer(target: GLenum, framebuffer: GLuint)
    @JvmStatic external fun glBindRenderbuffer(target: GLenum, renderbuffer: GLuint)
    @JvmStatic external fun glBindTexture(target: GLenum, texture: GLuint)
    @JvmStatic external fun glBlendColor(red: GLfloat, green: GLfloat, blue: GLfloat, alpha: GLfloat)
    @JvmStatic external fun glBlendEquation(mode: GLenum)
    @JvmStatic external fun glBlendEquationSeparate(modeRGB: GLenum, modeAlpha: GLenum)
    @JvmStatic external fun glBlendFunc(sfactor: GLenum, dfactor: GLenum)
    @JvmStatic external fun glBlendFuncSeparate(sfactorRGB: GLenum, dfactorRGB: GLenum, sfactorAlpha: GLenum, dfactorAlpha: GLenum)
    @JvmStatic external fun glBufferData(target: GLenum, size: GLsizeiptr, data: VoidPtr, usage: GLenum)
    @JvmStatic external fun glBufferSubData(target: GLenum, offset: GLintptr, size: GLsizeiptr, data: VoidPtr)
    @JvmStatic external fun glCheckFramebufferStatus(target: GLenum): GLenum
    @JvmStatic external fun glClear(mask: GLbitfield)
    @JvmStatic external fun glClearColor(red: GLfloat, green: GLfloat, blue: GLfloat, alpha: GLfloat)
    @JvmStatic external fun glClearDepth(d: GLdouble)
    @JvmStatic external fun glClearStencil(s: GLint)
    @JvmStatic external fun glColorMask(red: GLboolean, green: GLboolean, blue: GLboolean, alpha: GLboolean)
    @JvmStatic external fun glCompileShader(shader: GLuint)
    @JvmStatic external fun glCompressedTexImage2D(target: GLenum, level: GLint, internalformat: GLenum, width: GLsizei, height: GLsizei, border: GLint, imageSize: GLsizei, data: VoidPtr)
    @JvmStatic external fun glCompressedTexSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, width: GLsizei, height: GLsizei, format: GLenum, imageSize: GLsizei, data: VoidPtr)
    @JvmStatic external fun glCopyTexImage2D(target: GLenum, level: GLint, internalformat: GLenum, x: GLint, y: GLint, width: GLsizei, height: GLsizei, border: GLint)
    @JvmStatic external fun glCopyTexSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, x: GLint, y: GLint, width: GLsizei, height: GLsizei)
    @JvmStatic external fun glCreateProgram(): GLuint
    @JvmStatic external fun glCreateShader(type: GLenum): GLuint
    @JvmStatic external fun glCullFace(mode: GLenum)
    @JvmStatic external fun glDeleteBuffers(n: GLsizei, items: IntPtr)
    @JvmStatic external fun glDeleteFramebuffers(n: GLsizei, items: IntPtr)
    @JvmStatic external fun glDeleteProgram(program: GLuint)
    @JvmStatic external fun glDeleteRenderbuffers(n: GLsizei, items: IntPtr)
    @JvmStatic external fun glDeleteShader(shader: GLuint)
    @JvmStatic external fun glDeleteTextures(n: GLsizei, items: IntPtr)
    @JvmStatic external fun glDepthFunc(func: GLenum)
    @JvmStatic external fun glDepthMask(flag: GLboolean)
    @JvmStatic external fun glDepthRange(n: GLdouble, f: GLdouble)
    @JvmStatic external fun glDetachShader(program: GLuint, shader: GLuint)
    @JvmStatic external fun glDisable(cap: GLenum)
    @JvmStatic external fun glDisableVertexAttribArray(index: GLuint)
    @JvmStatic external fun glDrawArrays(mode: GLenum, first: GLint, count: GLsizei)
    @JvmStatic external fun glDrawElements(mode: GLenum, count: GLsizei, type: GLenum, indices: IntSize)
    @JvmStatic external fun glEnable(cap: GLenum)
    @JvmStatic external fun glEnableVertexAttribArray(index: GLuint)
    @JvmStatic external fun glFinish()
    @JvmStatic external fun glFlush()
    @JvmStatic external fun glFramebufferRenderbuffer(target: GLenum, attachment: GLenum, renderbuffertarget: GLenum, renderbuffer: GLuint)
    @JvmStatic external fun glFramebufferTexture2D(target: GLenum, attachment: GLenum, textarget: GLenum, texture: GLuint, level: GLint)
    @JvmStatic external fun glFrontFace(mode: GLenum)
    @JvmStatic external fun glGenBuffers(n: GLsizei, buffers: IntPtr)
    @JvmStatic external fun glGenerateMipmap(target: GLenum)
    @JvmStatic external fun glGenFramebuffers(n: GLsizei, framebuffers: IntPtr)
    @JvmStatic external fun glGenRenderbuffers(n: GLsizei, renderbuffers: IntPtr)
    @JvmStatic external fun glGenTextures(n: GLsizei, textures: IntPtr)
    @JvmStatic external fun glGetActiveAttrib(program: GLuint, index: GLuint, bufSize: GLsizei, length: IntPtr, size: IntPtr, type: IntPtr, name: VoidPtr)
    @JvmStatic external fun glGetActiveUniform(program: GLuint, index: GLuint, bufSize: GLsizei, length: IntPtr, size: IntPtr, type: IntPtr, name: VoidPtr)
    @JvmStatic external fun glGetAttachedShaders(program: GLuint, maxCount: GLsizei, count: IntPtr, shaders: IntPtr)
    @JvmStatic external fun glGetAttribLocation(program: GLuint, name: String): Int
    @JvmStatic external fun glGetUniformLocation(program: GLuint, name: String): Int
    @JvmStatic external fun glGetBooleanv(pname: GLenum, data: VoidPtr)
    @JvmStatic external fun glGetBufferParameteriv(target: GLenum, pname: GLenum, params: IntPtr)
    @JvmStatic external fun glGetError(): GLenum
    @JvmStatic external fun glGetFloatv(pname: GLenum, data: FloatPtr)
    @JvmStatic external fun glGetFramebufferAttachmentParameteriv(target: GLenum, attachment: GLenum, pname: GLenum, params: IntPtr)
    @JvmStatic external fun glGetIntegerv(pname: GLenum, data: IntPtr)
    @JvmStatic external fun glGetProgramInfoLog(program: GLuint, bufSize: GLsizei, length: IntPtr, infoLog: VoidPtr)
    @JvmStatic external fun glGetRenderbufferParameteriv(target: GLenum, pname: GLenum, params: IntPtr)
    @JvmStatic external fun glGetProgramiv(program: GLuint, pname: GLenum, params: IntPtr)
    @JvmStatic external fun glGetShaderiv(shader: GLuint, pname: GLenum, params: IntPtr)
    @JvmStatic external fun glGetShaderInfoLog(shader: GLuint, bufSize: GLsizei, length: IntPtr, infoLog: VoidPtr)
    @JvmStatic external fun glGetShaderPrecisionFormat(shadertype: GLenum, precisiontype: GLenum, range: IntPtr, precision: IntPtr)
    @JvmStatic external fun glGetShaderSource(shader: GLuint, bufSize: GLsizei, length: IntPtr, source: VoidPtr)
    @JvmStatic external fun glGetString(name: GLenum): String?
    @JvmStatic external fun glGetStringi(name: GLenum, i: GLuint): String?
    @JvmStatic external fun glGetTexParameterfv(target: GLenum, pname: GLenum, params: FloatPtr)
    @JvmStatic external fun glGetTexParameteriv(target: GLenum, pname: GLenum, params: IntPtr)
    @JvmStatic external fun glGetUniformfv(program: GLuint, location: GLint, params: FloatPtr)
    @JvmStatic external fun glGetUniformiv(program: GLuint, location: GLint, params: IntPtr)
    @JvmStatic external fun glGetVertexAttribfv(index: GLuint, pname: GLenum, params: FloatPtr)
    @JvmStatic external fun glGetVertexAttribiv(index: GLuint, pname: GLenum, params: IntPtr)
    //@JvmStatic external fun glGetVertexAttribPointerv(index: GLuint, pname: GLenum, pointer: FBuffer)
    @JvmStatic external fun glHint(target: GLenum, mode: GLenum)
    @JvmStatic external fun glIsBuffer(buffer: GLuint): GLboolean
    @JvmStatic external fun glIsEnabled(cap: GLenum): GLboolean
    @JvmStatic external fun glIsFramebuffer(framebuffer: GLuint): GLboolean
    @JvmStatic external fun glIsProgram(program: GLuint): GLboolean
    @JvmStatic external fun glIsRenderbuffer(renderbuffer: GLuint): GLboolean
    @JvmStatic external fun glIsShader(shader: GLuint): GLboolean
    @JvmStatic external fun glIsTexture(texture: GLuint): GLboolean
    @JvmStatic external fun glLineWidth(width: GLfloat)
    @JvmStatic external fun glLinkProgram(program: GLuint)
    @JvmStatic external fun glPixelStorei(pname: GLenum, param: GLint)
    @JvmStatic external fun glPolygonOffset(factor: GLfloat, units: GLfloat)
    @JvmStatic external fun glReadPixels(x: GLint, y: GLint, width: GLsizei, height: GLsizei, format: GLenum, type: GLenum, pixels: VoidPtr)
    @JvmStatic external fun glReleaseShaderCompiler()
    @JvmStatic external fun glRenderbufferStorage(target: GLenum, internalformat: GLenum, width: GLsizei, height: GLsizei)
    @JvmStatic external fun glSampleCoverage(value: GLfloat, invert: GLboolean)
    @JvmStatic external fun glScissor(x: GLint, y: GLint, width: GLsizei, height: GLsizei)
    @JvmStatic external fun glShaderBinary(count: GLsizei, shaders: IntPtr, binaryformat: GLenum, binary: VoidPtr, length: GLsizei)
    //@JvmStatic external fun glShaderSource(shader: GLuint, count: GLsizei, string: Array<String>, length: IntArray?)
    @JvmStatic external fun glStencilFunc(func: GLenum, ref: GLint, mask: GLuint)
    @JvmStatic external fun glStencilFuncSeparate(face: GLenum, func: GLenum, ref: GLint, mask: GLuint)
    @JvmStatic external fun glStencilMask(mask: GLuint)
    @JvmStatic external fun glStencilMaskSeparate(face: GLenum, mask: GLuint)
    @JvmStatic external fun glStencilOp(fail: GLenum, zfail: GLenum, zpass: GLenum)
    @JvmStatic external fun glStencilOpSeparate(face: GLenum, sfail: GLenum, dpfail: GLenum, dppass: GLenum)
    @JvmStatic external fun glTexImage2D(target: GLenum, level: GLint, internalformat: GLint, width: GLsizei, height: GLsizei, border: GLint, format: GLenum, type: GLenum, pixels: VoidPtr?)
    @JvmStatic external fun glTexParameterf(target: GLenum, pname: GLenum, param: GLfloat)
    @JvmStatic external fun glTexParameterfv(target: GLenum, pname: GLenum, params: FloatPtr)
    @JvmStatic external fun glTexParameteri(target: GLenum, pname: GLenum, param: GLint)
    @JvmStatic external fun glTexParameteriv(target: GLenum, pname: GLenum, params: IntPtr)
    @JvmStatic external fun glTexSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, width: GLsizei, height: GLsizei, format: GLenum, type: GLenum, pixels: VoidPtr)
    @JvmStatic external fun glUniform1f(location: GLint, v0: GLfloat)
    @JvmStatic external fun glUniform1fv(location: GLint, count: GLsizei, value: FloatPtr)
    @JvmStatic external fun glUniform1i(location: GLint, v0: GLint)
    @JvmStatic external fun glUniform1iv(location: GLint, count: GLsizei, value: IntPtr)
    @JvmStatic external fun glUniform2f(location: GLint, v0: GLfloat, v1: GLfloat)
    @JvmStatic external fun glUniform2fv(location: GLint, count: GLsizei, value: FloatPtr)
    @JvmStatic external fun glUniform2i(location: GLint, v0: GLint, v1: GLint)
    @JvmStatic external fun glUniform2iv(location: GLint, count: GLsizei, value: IntPtr)
    @JvmStatic external fun glUniform3f(location: GLint, v0: GLfloat, v1: GLfloat, v2: GLfloat)
    @JvmStatic external fun glUniform3fv(location: GLint, count: GLsizei, value: FloatPtr)
    @JvmStatic external fun glUniform3i(location: GLint, v0: GLint, v1: GLint, v2: GLint)
    @JvmStatic external fun glUniform3iv(location: GLint, count: GLsizei, value: IntPtr)
    @JvmStatic external fun glUniform4f(location: GLint, v0: GLfloat, v1: GLfloat, v2: GLfloat, v3: GLfloat)
    @JvmStatic external fun glUniform4fv(location: GLint, count: GLsizei, value: FloatPtr)
    @JvmStatic external fun glUniform4i(location: GLint, v0: GLint, v1: GLint, v2: GLint, v3: GLint)
    @JvmStatic external fun glUniform4iv(location: GLint, count: GLsizei, value: IntPtr)
    @JvmStatic external fun glUniformMatrix2fv(location: GLint, count: GLsizei, transpose: GLboolean, value: FloatPtr)
    @JvmStatic external fun glUniformMatrix3fv(location: GLint, count: GLsizei, transpose: GLboolean, value: FloatPtr)
    @JvmStatic external fun glUniformMatrix4fv(location: GLint, count: GLsizei, transpose: GLboolean, value: FloatPtr)
    @JvmStatic external fun glUseProgram(program: GLuint)
    @JvmStatic external fun glValidateProgram(program: GLuint)
    @JvmStatic external fun glVertexAttrib1f(index: GLuint, x: GLfloat)
    @JvmStatic external fun glVertexAttrib1fv(index: GLuint, v: FloatPtr)
    @JvmStatic external fun glVertexAttrib2f(index: GLuint, x: GLfloat, y: GLfloat)
    @JvmStatic external fun glVertexAttrib2fv(index: GLuint, v: FloatPtr)
    @JvmStatic external fun glVertexAttrib3f(index: GLuint, x: GLfloat, y: GLfloat, z: GLfloat)
    @JvmStatic external fun glVertexAttrib3fv(index: GLuint, v: FloatPtr)
    @JvmStatic external fun glVertexAttrib4f(index: GLuint, x: GLfloat, y: GLfloat, z: GLfloat, w: GLfloat)
    @JvmStatic external fun glVertexAttrib4fv(index: GLuint, v: FloatPtr)
    @JvmStatic external fun glVertexAttribPointer(index: GLuint, size: GLint, type: GLenum, normalized: GLboolean, stride: GLsizei, pointer: IntSize)
    @JvmStatic external fun glViewport(x: GLint, y: GLint, width: GLsizei, height: GLsizei)
    @JvmStatic external fun glDrawArraysInstanced(mode: GLenum, first: GLint, count: GLsizei, instancecount: GLsizei)
    @JvmStatic external fun glDrawElementsInstanced(mode: GLenum, count: GLsizei, type: GLenum, indices: IntSize, instancecount: GLsizei)
    @JvmStatic external fun glVertexAttribDivisor(index: GLuint, divisor: GLuint)
    @JvmStatic external fun glRenderbufferStorageMultisample(target: GLenum, samples: GLint, internalformat: GLenum, width: GLsizei, height: GLsizei)

    internal var loaded = false

    init {
        try {
            if (nativeOpenGLLibraryPath == null) error("Can't get OpenGL library")
            traceTime("OpenGL Native.register") {
                // @TODO: Can we provide a custom loader? like a dysym, to use the glGetProcAddress? If so, we will be able to use this on Windows too
                Native.register(nativeOpenGLLibraryPath)
            }
            loaded = true
        } catch (e: Throwable) {
            com.soywiz.klogger.Console.error("Failed to initialize OpenAL: arch=$arch, OS.rawName=${OS.rawName}, nativeOpenGLLibraryPath=$nativeOpenGLLibraryPath, message=${e.message}")
            //e.printStackTrace()
        }
    }
}

private val arch by lazy { System.getProperty("os.arch").toLowerCase() }

val nativeOpenGLLibraryPath: String? by lazy {
    Environment["OPENGL_LIB_PATH"]?.let { path ->
        return@lazy path
    }
    when {
        OS.isMac -> {
            //getNativeFileLocalPath("natives/macosx64/libopenal.dylib")
            "OpenGL" // Mac already includes the OpenAL library
        }
        OS.isWindows -> {
            "OpenGL32"
        }
        else -> {
            println("  - Unknown/Unsupported OS")
            null
        }
    }
}

open class DirectINativeGL(val fallback: INativeGL) : INativeGL {
    override fun glActiveTexture(texture: GLenum) = GL.glActiveTexture(texture)
    override fun glAttachShader(program: GLuint, shader: GLuint) = GL.glAttachShader(program, shader)
    override fun glBindAttribLocation(program: GLuint, index: GLuint, name: String) = GL.glBindAttribLocation(program, index, name)
    override fun glBindBuffer(target: GLenum, buffer: GLuint) = GL.glBindBuffer(target, buffer)
    override fun glBindFramebuffer(target: GLenum, framebuffer: GLuint) = GL.glBindFramebuffer(target, framebuffer)
    override fun glBindRenderbuffer(target: GLenum, renderbuffer: GLuint) = GL.glBindRenderbuffer(target, renderbuffer)
    override fun glBindTexture(target: GLenum, texture: GLuint) = GL.glBindTexture(target, texture)
    override fun glBlendColor(red: GLfloat, green: GLfloat, blue: GLfloat, alpha: GLfloat) = GL.glBlendColor(red, green, blue, alpha)
    override fun glBlendEquation(mode: GLenum) = GL.glBlendEquation(mode)
    override fun glBlendEquationSeparate(modeRGB: GLenum, modeAlpha: GLenum) = GL.glBlendEquationSeparate(modeRGB, modeAlpha)
    override fun glBlendFunc(sfactor: GLenum, dfactor: GLenum) = GL.glBlendFunc(sfactor, dfactor)
    override fun glBlendFuncSeparate(sfactorRGB: GLenum, dfactorRGB: GLenum, sfactorAlpha: GLenum, dfactorAlpha: GLenum) = GL.glBlendFuncSeparate(sfactorRGB, dfactorRGB, sfactorAlpha, dfactorAlpha)
    override fun glBufferData(target: GLenum, size: GLsizeiptr, data: VoidPtr, usage: GLenum) = GL.glBufferData(target, size, data, usage)
    override fun glBufferSubData(target: GLenum, offset: GLintptr, size: GLsizeiptr, data: VoidPtr) = GL.glBufferSubData(target, offset, size, data)
    override fun glCheckFramebufferStatus(target: GLenum): GLenum = GL.glCheckFramebufferStatus(target)
    override fun glClear(mask: GLbitfield) = GL.glClear(mask)
    override fun glClearColor(red: GLfloat, green: GLfloat, blue: GLfloat, alpha: GLfloat) = GL.glClearColor(red, green, blue, alpha)
    override fun glClearDepth(d: GLdouble) = GL.glClearDepth(d)
    override fun glClearStencil(s: GLint) = GL.glClearStencil(s)
    override fun glColorMask(red: GLboolean, green: GLboolean, blue: GLboolean, alpha: GLboolean) = GL.glColorMask(red, green, blue, alpha)
    override fun glCompileShader(shader: GLuint) = GL.glCompileShader(shader)
    override fun glCompressedTexImage2D(target: GLenum, level: GLint, internalformat: GLenum, width: GLsizei, height: GLsizei, border: GLint, imageSize: GLsizei, data: VoidPtr) = GL.glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, data)
    override fun glCompressedTexSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, width: GLsizei, height: GLsizei, format: GLenum, imageSize: GLsizei, data: VoidPtr) = GL.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, data)
    override fun glCopyTexImage2D(target: GLenum, level: GLint, internalformat: GLenum, x: GLint, y: GLint, width: GLsizei, height: GLsizei, border: GLint) = GL.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border)
    override fun glCopyTexSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, x: GLint, y: GLint, width: GLsizei, height: GLsizei) = GL.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height)
    override fun glCreateProgram(): GLuint = GL.glCreateProgram()
    override fun glCreateShader(type: GLenum): GLuint = GL.glCreateShader(type)
    override fun glCullFace(mode: GLenum) = GL.glCullFace(mode)
    override fun glDeleteBuffers(n: GLsizei, items: IntPtr) = GL.glDeleteBuffers(n, items)
    override fun glDeleteFramebuffers(n: GLsizei, items: IntPtr) = GL.glDeleteFramebuffers(n, items)
    override fun glDeleteProgram(program: GLuint) = GL.glDeleteProgram(program)
    override fun glDeleteRenderbuffers(n: GLsizei, items: IntPtr) = GL.glDeleteRenderbuffers(n, items)
    override fun glDeleteShader(shader: GLuint) = GL.glDeleteShader(shader)
    override fun glDeleteTextures(n: GLsizei, items: IntPtr) = GL.glDeleteTextures(n, items)
    override fun glDepthFunc(func: GLenum) = GL.glDepthFunc(func)
    override fun glDepthMask(flag: GLboolean) = GL.glDepthMask(flag)
    override fun glDepthRange(n: GLdouble, f: GLdouble) = GL.glDepthRange(n, f)
    override fun glDetachShader(program: GLuint, shader: GLuint) = GL.glDetachShader(program, shader)
    override fun glDisable(cap: GLenum) = GL.glDisable(cap)
    override fun glDisableVertexAttribArray(index: GLuint) = GL.glDisableVertexAttribArray(index)
    override fun glDrawArrays(mode: GLenum, first: GLint, count: GLsizei) = GL.glDrawArrays(mode, first, count)
    override fun glDrawElements(mode: GLenum, count: GLsizei, type: GLenum, indices: IntSize) = GL.glDrawElements(mode, count, type, indices)
    override fun glEnable(cap: GLenum) = GL.glEnable(cap)
    override fun glEnableVertexAttribArray(index: GLuint) = GL.glEnableVertexAttribArray(index)
    override fun glFinish() = GL.glFinish()
    override fun glFlush() = GL.glFlush()
    override fun glFramebufferRenderbuffer(target: GLenum, attachment: GLenum, renderbuffertarget: GLenum, renderbuffer: GLuint) = GL.glFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer)
    override fun glFramebufferTexture2D(target: GLenum, attachment: GLenum, textarget: GLenum, texture: GLuint, level: GLint) = GL.glFramebufferTexture2D(target, attachment, textarget, texture, level)
    override fun glFrontFace(mode: GLenum) = GL.glFrontFace(mode)
    override fun glGenBuffers(n: GLsizei, buffers: IntPtr) = GL.glGenBuffers(n, buffers)
    override fun glGenerateMipmap(target: GLenum) = GL.glGenerateMipmap(target)
    override fun glGenFramebuffers(n: GLsizei, framebuffers: IntPtr) = GL.glGenFramebuffers(n, framebuffers)
    override fun glGenRenderbuffers(n: GLsizei, renderbuffers: IntPtr) = GL.glGenRenderbuffers(n, renderbuffers)
    override fun glGenTextures(n: GLsizei, textures: IntPtr) = GL.glGenTextures(n, textures)
    override fun glGetActiveAttrib(program: GLuint, index: GLuint, bufSize: GLsizei, length: IntPtr, size: IntPtr, type: IntPtr, name: VoidPtr) = GL.glGetActiveAttrib(program, index, bufSize, length, size, type, name)
    override fun glGetActiveUniform(program: GLuint, index: GLuint, bufSize: GLsizei, length: IntPtr, size: IntPtr, type: IntPtr, name: VoidPtr) = GL.glGetActiveUniform(program, index, bufSize, length, size, type, name)
    override fun glGetAttachedShaders(program: GLuint, maxCount: GLsizei, count: IntPtr, shaders: IntPtr) = GL.glGetAttachedShaders(program, maxCount, count, shaders)
    override fun glGetAttribLocation(program: GLuint, name: String): Int = GL.glGetAttribLocation(program, name)
    override fun glGetUniformLocation(program: GLuint, name: String): Int = GL.glGetUniformLocation(program, name)
    override fun glGetBooleanv(pname: GLenum, data: VoidPtr) = GL.glGetBooleanv(pname, data)
    override fun glGetBufferParameteriv(target: GLenum, pname: GLenum, params: IntPtr) = GL.glGetBufferParameteriv(target, pname, params)
    override fun glGetError(): GLenum = GL.glGetError()
    override fun glGetFloatv(pname: GLenum, data: FloatPtr) = GL.glGetFloatv(pname, data)
    override fun glGetFramebufferAttachmentParameteriv(target: GLenum, attachment: GLenum, pname: GLenum, params: IntPtr) = GL.glGetFramebufferAttachmentParameteriv(target, attachment, pname, params)
    override fun glGetIntegerv(pname: GLenum, data: IntPtr) = GL.glGetIntegerv(pname, data)
    override fun glGetProgramInfoLog(program: GLuint, bufSize: GLsizei, length: IntPtr, infoLog: VoidPtr) = GL.glGetProgramInfoLog(program, bufSize, length, infoLog)
    override fun glGetRenderbufferParameteriv(target: GLenum, pname: GLenum, params: IntPtr) = GL.glGetRenderbufferParameteriv(target, pname, params)
    override fun glGetProgramiv(program: GLuint, pname: GLenum, params: IntPtr) = GL.glGetProgramiv(program, pname, params)
    override fun glGetShaderiv(shader: GLuint, pname: GLenum, params: IntPtr) = GL.glGetShaderiv(shader, pname, params)
    override fun glGetShaderInfoLog(shader: GLuint, bufSize: GLsizei, length: IntPtr, infoLog: VoidPtr) = GL.glGetShaderInfoLog(shader, bufSize, length, infoLog)
    override fun glGetShaderPrecisionFormat(shadertype: GLenum, precisiontype: GLenum, range: IntPtr, precision: IntPtr) = GL.glGetShaderPrecisionFormat(shadertype, precisiontype, range, precision)
    override fun glGetShaderSource(shader: GLuint, bufSize: GLsizei, length: IntPtr, source: VoidPtr) = GL.glGetShaderSource(shader, bufSize, length, source)
    override fun glGetString(name: GLenum): String? = GL.glGetString(name)
    override fun glGetStringi(name: GLenum, i: GLuint): String? = GL.glGetStringi(name, i)
    override fun glGetTexParameterfv(target: GLenum, pname: GLenum, params: FloatPtr) = GL.glGetTexParameterfv(target, pname, params)
    override fun glGetTexParameteriv(target: GLenum, pname: GLenum, params: IntPtr) = GL.glGetTexParameteriv(target, pname, params)
    override fun glGetUniformfv(program: GLuint, location: GLint, params: FloatPtr) = GL.glGetUniformfv(program, location, params)
    override fun glGetUniformiv(program: GLuint, location: GLint, params: IntPtr) = GL.glGetUniformiv(program, location, params)
    override fun glGetVertexAttribfv(index: GLuint, pname: GLenum, params: FloatPtr) = GL.glGetVertexAttribfv(index, pname, params)
    override fun glGetVertexAttribiv(index: GLuint, pname: GLenum, params: IntPtr) = GL.glGetVertexAttribiv(index, pname, params)
    override fun glHint(target: GLenum, mode: GLenum) = GL.glHint(target, mode)
    override fun glIsBuffer(buffer: GLuint): GLboolean = GL.glIsBuffer(buffer)
    override fun glIsEnabled(cap: GLenum): GLboolean = GL.glIsEnabled(cap)
    override fun glIsFramebuffer(framebuffer: GLuint): GLboolean = GL.glIsFramebuffer(framebuffer)
    override fun glIsProgram(program: GLuint): GLboolean = GL.glIsProgram(program)
    override fun glIsRenderbuffer(renderbuffer: GLuint): GLboolean = GL.glIsRenderbuffer(renderbuffer)
    override fun glIsShader(shader: GLuint): GLboolean = GL.glIsShader(shader)
    override fun glIsTexture(texture: GLuint): GLboolean = GL.glIsTexture(texture)
    override fun glLineWidth(width: GLfloat) = GL.glLineWidth(width)
    override fun glLinkProgram(program: GLuint) = GL.glLinkProgram(program)
    override fun glPixelStorei(pname: GLenum, param: GLint) = GL.glPixelStorei(pname, param)
    override fun glPolygonOffset(factor: GLfloat, units: GLfloat) = GL.glPolygonOffset(factor, units)
    override fun glReadPixels(x: GLint, y: GLint, width: GLsizei, height: GLsizei, format: GLenum, type: GLenum, pixels: VoidPtr) = GL.glReadPixels(x, y, width, height, format, type, pixels)
    override fun glReleaseShaderCompiler() = GL.glReleaseShaderCompiler()
    override fun glRenderbufferStorage(target: GLenum, internalformat: GLenum, width: GLsizei, height: GLsizei) = GL.glRenderbufferStorage(target, internalformat, width, height)
    override fun glSampleCoverage(value: GLfloat, invert: GLboolean) = GL.glSampleCoverage(value, invert)
    override fun glScissor(x: GLint, y: GLint, width: GLsizei, height: GLsizei) = GL.glScissor(x, y, width, height)
    override fun glShaderBinary(count: GLsizei, shaders: IntPtr, binaryformat: GLenum, binary: VoidPtr, length: GLsizei) = GL.glShaderBinary(count, shaders, binaryformat, binary, length)
    override fun glShaderSource(shader: GLuint, count: GLsizei, string: Array<String>, length: IntArray?): Unit {
        fallback.glShaderSource(shader, count, string, length)
    }
    override fun glStencilFunc(func: GLenum, ref: GLint, mask: GLuint) = GL.glStencilFunc(func, ref, mask)
    override fun glStencilFuncSeparate(face: GLenum, func: GLenum, ref: GLint, mask: GLuint) = GL.glStencilFuncSeparate(face, func, ref, mask)
    override fun glStencilMask(mask: GLuint) = GL.glStencilMask(mask)
    override fun glStencilMaskSeparate(face: GLenum, mask: GLuint) = GL.glStencilMaskSeparate(face, mask)
    override fun glStencilOp(fail: GLenum, zfail: GLenum, zpass: GLenum) = GL.glStencilOp(fail, zfail, zpass)
    override fun glStencilOpSeparate(face: GLenum, sfail: GLenum, dpfail: GLenum, dppass: GLenum) = GL.glStencilOpSeparate(face, sfail, dpfail, dppass)
    override fun glTexImage2D(target: GLenum, level: GLint, internalformat: GLint, width: GLsizei, height: GLsizei, border: GLint, format: GLenum, type: GLenum, pixels: VoidPtr?) = GL.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels)
    override fun glTexParameterf(target: GLenum, pname: GLenum, param: GLfloat) = GL.glTexParameterf(target, pname, param)
    override fun glTexParameterfv(target: GLenum, pname: GLenum, params: FloatPtr) = GL.glTexParameterfv(target, pname, params)
    override fun glTexParameteri(target: GLenum, pname: GLenum, param: GLint) = GL.glTexParameteri(target, pname, param)
    override fun glTexParameteriv(target: GLenum, pname: GLenum, params: IntPtr) = GL.glTexParameteriv(target, pname, params)
    override fun glTexSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, width: GLsizei, height: GLsizei, format: GLenum, type: GLenum, pixels: VoidPtr) = GL.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels)
    override fun glUniform1f(location: GLint, v0: GLfloat) = GL.glUniform1f(location, v0)
    override fun glUniform1fv(location: GLint, count: GLsizei, value: FloatPtr) = GL.glUniform1fv(location, count, value)
    override fun glUniform1i(location: GLint, v0: GLint) = GL.glUniform1i(location, v0)
    override fun glUniform1iv(location: GLint, count: GLsizei, value: IntPtr) = GL.glUniform1iv(location, count, value)
    override fun glUniform2f(location: GLint, v0: GLfloat, v1: GLfloat) = GL.glUniform2f(location, v0, v1)
    override fun glUniform2fv(location: GLint, count: GLsizei, value: FloatPtr) = GL.glUniform2fv(location, count, value)
    override fun glUniform2i(location: GLint, v0: GLint, v1: GLint) = GL.glUniform2i(location, v0, v1)
    override fun glUniform2iv(location: GLint, count: GLsizei, value: IntPtr) = GL.glUniform2iv(location, count, value)
    override fun glUniform3f(location: GLint, v0: GLfloat, v1: GLfloat, v2: GLfloat) = GL.glUniform3f(location, v0, v1, v2)
    override fun glUniform3fv(location: GLint, count: GLsizei, value: FloatPtr) = GL.glUniform3fv(location, count, value)
    override fun glUniform3i(location: GLint, v0: GLint, v1: GLint, v2: GLint) = GL.glUniform3i(location, v0, v1, v2)
    override fun glUniform3iv(location: GLint, count: GLsizei, value: IntPtr) = GL.glUniform3iv(location, count, value)
    override fun glUniform4f(location: GLint, v0: GLfloat, v1: GLfloat, v2: GLfloat, v3: GLfloat) = GL.glUniform4f(location, v0, v1, v2, v3)
    override fun glUniform4fv(location: GLint, count: GLsizei, value: FloatPtr) = GL.glUniform4fv(location, count, value)
    override fun glUniform4i(location: GLint, v0: GLint, v1: GLint, v2: GLint, v3: GLint) = GL.glUniform4i(location, v0, v1, v2, v3)
    override fun glUniform4iv(location: GLint, count: GLsizei, value: IntPtr) = GL.glUniform4iv(location, count, value)
    override fun glUniformMatrix2fv(location: GLint, count: GLsizei, transpose: GLboolean, value: FloatPtr) = GL.glUniformMatrix2fv(location, count, transpose, value)
    override fun glUniformMatrix3fv(location: GLint, count: GLsizei, transpose: GLboolean, value: FloatPtr) = GL.glUniformMatrix3fv(location, count, transpose, value)
    override fun glUniformMatrix4fv(location: GLint, count: GLsizei, transpose: GLboolean, value: FloatPtr) = GL.glUniformMatrix4fv(location, count, transpose, value)
    override fun glUseProgram(program: GLuint) = GL.glUseProgram(program)
    override fun glValidateProgram(program: GLuint) = GL.glValidateProgram(program)
    override fun glVertexAttrib1f(index: GLuint, x: GLfloat) = GL.glVertexAttrib1f(index, x)
    override fun glVertexAttrib1fv(index: GLuint, v: FloatPtr) = GL.glVertexAttrib1fv(index, v)
    override fun glVertexAttrib2f(index: GLuint, x: GLfloat, y: GLfloat) = GL.glVertexAttrib2f(index, x, y)
    override fun glVertexAttrib2fv(index: GLuint, v: FloatPtr) = GL.glVertexAttrib2fv(index, v)
    override fun glVertexAttrib3f(index: GLuint, x: GLfloat, y: GLfloat, z: GLfloat) = GL.glVertexAttrib3f(index, x, y, z)
    override fun glVertexAttrib3fv(index: GLuint, v: FloatPtr) = GL.glVertexAttrib3fv(index, v)
    override fun glVertexAttrib4f(index: GLuint, x: GLfloat, y: GLfloat, z: GLfloat, w: GLfloat) = GL.glVertexAttrib4f(index, x, y, z, w)
    override fun glVertexAttrib4fv(index: GLuint, v: FloatPtr) = GL.glVertexAttrib4fv(index, v)
    override fun glVertexAttribPointer(index: GLuint, size: GLint, type: GLenum, normalized: GLboolean, stride: GLsizei, pointer: IntSize) = GL.glVertexAttribPointer(index, size, type, normalized, stride, pointer)
    override fun glViewport(x: GLint, y: GLint, width: GLsizei, height: GLsizei) = GL.glViewport(x, y, width, height)

    override fun glDrawArraysInstanced(mode: GLenum, first: GLint, count: GLsizei, instancecount: GLsizei): Unit = GL.glDrawArraysInstanced(mode, first, count, instancecount)
    override fun glDrawElementsInstanced(mode: GLenum, count: GLsizei, type: GLenum, indices: IntSize, instancecount: GLsizei) = GL.glDrawElementsInstanced(mode, count, type, indices, instancecount)
    override fun glVertexAttribDivisor(index: GLuint, divisor: GLuint) = GL.glVertexAttribDivisor(index, divisor)
    override fun glRenderbufferStorageMultisample(target: GLenum, samples: GLint, internalformat: GLenum, width: GLsizei, height: GLsizei) = GL.glRenderbufferStorageMultisample(target, samples, internalformat, width, height)

}

interface INativeGL {
    fun glActiveTexture(texture: GLenum)
    fun glAttachShader(program: GLuint, shader: GLuint)
    fun glBindAttribLocation(program: GLuint, index: GLuint, name: String)
    fun glBindBuffer(target: GLenum, buffer: GLuint)
    fun glBindFramebuffer(target: GLenum, framebuffer: GLuint)
    fun glBindRenderbuffer(target: GLenum, renderbuffer: GLuint)
    fun glBindTexture(target: GLenum, texture: GLuint)
    fun glBlendColor(red: GLfloat, green: GLfloat, blue: GLfloat, alpha: GLfloat)
    fun glBlendEquation(mode: GLenum)
    fun glBlendEquationSeparate(modeRGB: GLenum, modeAlpha: GLenum)
    fun glBlendFunc(sfactor: GLenum, dfactor: GLenum)
    fun glBlendFuncSeparate(sfactorRGB: GLenum, dfactorRGB: GLenum, sfactorAlpha: GLenum, dfactorAlpha: GLenum)
    fun glBufferData(target: GLenum, size: GLsizeiptr, data: VoidPtr, usage: GLenum)
    fun glBufferSubData(target: GLenum, offset: GLintptr, size: GLsizeiptr, data: VoidPtr)
    fun glCheckFramebufferStatus(target: GLenum): GLenum
    fun glClear(mask: GLbitfield)
    fun glClearColor(red: GLfloat, green: GLfloat, blue: GLfloat, alpha: GLfloat)
    fun glClearDepth(d: GLdouble)
    fun glClearStencil(s: GLint)
    fun glColorMask(red: GLboolean, green: GLboolean, blue: GLboolean, alpha: GLboolean)
    fun glCompileShader(shader: GLuint)
    fun glCompressedTexImage2D(target: GLenum, level: GLint, internalformat: GLenum, width: GLsizei, height: GLsizei, border: GLint, imageSize: GLsizei, data: VoidPtr)
    fun glCompressedTexSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, width: GLsizei, height: GLsizei, format: GLenum, imageSize: GLsizei, data: VoidPtr)
    fun glCopyTexImage2D(target: GLenum, level: GLint, internalformat: GLenum, x: GLint, y: GLint, width: GLsizei, height: GLsizei, border: GLint)
    fun glCopyTexSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, x: GLint, y: GLint, width: GLsizei, height: GLsizei)
    fun glCreateProgram(): GLuint
    fun glCreateShader(type: GLenum): GLuint
    fun glCullFace(mode: GLenum)
    fun glDeleteBuffers(n: GLsizei, items: IntPtr)
    fun glDeleteFramebuffers(n: GLsizei, items: IntPtr)
    fun glDeleteProgram(program: GLuint)
    fun glDeleteRenderbuffers(n: GLsizei, items: IntPtr)
    fun glDeleteShader(shader: GLuint)
    fun glDeleteTextures(n: GLsizei, items: IntPtr)
    fun glDepthFunc(func: GLenum)
    fun glDepthMask(flag: GLboolean)
    fun glDepthRange(n: GLdouble, f: GLdouble)
    fun glDetachShader(program: GLuint, shader: GLuint)
    fun glDisable(cap: GLenum)
    fun glDisableVertexAttribArray(index: GLuint)
    fun glDrawArrays(mode: GLenum, first: GLint, count: GLsizei)
    fun glDrawElements(mode: GLenum, count: GLsizei, type: GLenum, indices: IntSize)
    fun glEnable(cap: GLenum)
    fun glEnableVertexAttribArray(index: GLuint)
    fun glFinish()
    fun glFlush()
    fun glFramebufferRenderbuffer(target: GLenum, attachment: GLenum, renderbuffertarget: GLenum, renderbuffer: GLuint)
    fun glFramebufferTexture2D(target: GLenum, attachment: GLenum, textarget: GLenum, texture: GLuint, level: GLint)
    fun glFrontFace(mode: GLenum)
    fun glGenBuffers(n: GLsizei, buffers: IntPtr)
    fun glGenerateMipmap(target: GLenum)
    fun glGenFramebuffers(n: GLsizei, framebuffers: IntPtr)
    fun glGenRenderbuffers(n: GLsizei, renderbuffers: IntPtr)
    fun glGenTextures(n: GLsizei, textures: IntPtr)
    fun glGetActiveAttrib(program: GLuint, index: GLuint, bufSize: GLsizei, length: IntPtr, size: IntPtr, type: IntPtr, name: VoidPtr)
    fun glGetActiveUniform(program: GLuint, index: GLuint, bufSize: GLsizei, length: IntPtr, size: IntPtr, type: IntPtr, name: VoidPtr)
    fun glGetAttachedShaders(program: GLuint, maxCount: GLsizei, count: IntPtr, shaders: IntPtr)
    fun glGetAttribLocation(program: GLuint, name: String): Int
    fun glGetUniformLocation(program: GLuint, name: String): Int
    fun glGetBooleanv(pname: GLenum, data: VoidPtr)
    fun glGetBufferParameteriv(target: GLenum, pname: GLenum, params: IntPtr)
    fun glGetError(): GLenum
    fun glGetFloatv(pname: GLenum, data: FloatPtr)
    fun glGetFramebufferAttachmentParameteriv(target: GLenum, attachment: GLenum, pname: GLenum, params: IntPtr)
    fun glGetIntegerv(pname: GLenum, data: IntPtr)
    fun glGetProgramInfoLog(program: GLuint, bufSize: GLsizei, length: IntPtr, infoLog: VoidPtr)
    fun glGetRenderbufferParameteriv(target: GLenum, pname: GLenum, params: IntPtr)
    fun glGetProgramiv(program: GLuint, pname: GLenum, params: IntPtr)
    fun glGetShaderiv(shader: GLuint, pname: GLenum, params: IntPtr)
    fun glGetShaderInfoLog(shader: GLuint, bufSize: GLsizei, length: IntPtr, infoLog: VoidPtr)
    fun glGetShaderPrecisionFormat(shadertype: GLenum, precisiontype: GLenum, range: IntPtr, precision: IntPtr)
    fun glGetShaderSource(shader: GLuint, bufSize: GLsizei, length: IntPtr, source: VoidPtr)
    fun glGetString(name: GLenum): String?
    fun glGetStringi(name: GLenum, i: GLuint): String?
    fun glGetTexParameterfv(target: GLenum, pname: GLenum, params: FloatPtr)
    fun glGetTexParameteriv(target: GLenum, pname: GLenum, params: IntPtr)
    fun glGetUniformfv(program: GLuint, location: GLint, params: FloatPtr)
    fun glGetUniformiv(program: GLuint, location: GLint, params: IntPtr)
    fun glGetVertexAttribfv(index: GLuint, pname: GLenum, params: FloatPtr)
    fun glGetVertexAttribiv(index: GLuint, pname: GLenum, params: IntPtr)
    //fun glGetVertexAttribPointerv(index: GLuint, pname: GLenum, pointer: FBuffer)
    fun glHint(target: GLenum, mode: GLenum)
    fun glIsBuffer(buffer: GLuint): GLboolean
    fun glIsEnabled(cap: GLenum): GLboolean
    fun glIsFramebuffer(framebuffer: GLuint): GLboolean
    fun glIsProgram(program: GLuint): GLboolean
    fun glIsRenderbuffer(renderbuffer: GLuint): GLboolean
    fun glIsShader(shader: GLuint): GLboolean
    fun glIsTexture(texture: GLuint): GLboolean
    fun glLineWidth(width: GLfloat)
    fun glLinkProgram(program: GLuint)
    fun glPixelStorei(pname: GLenum, param: GLint)
    fun glPolygonOffset(factor: GLfloat, units: GLfloat)
    fun glReadPixels(x: GLint, y: GLint, width: GLsizei, height: GLsizei, format: GLenum, type: GLenum, pixels: VoidPtr)
    fun glReleaseShaderCompiler()
    fun glRenderbufferStorage(target: GLenum, internalformat: GLenum, width: GLsizei, height: GLsizei)
    fun glSampleCoverage(value: GLfloat, invert: GLboolean)
    fun glScissor(x: GLint, y: GLint, width: GLsizei, height: GLsizei)
    fun glShaderBinary(count: GLsizei, shaders: IntPtr, binaryformat: GLenum, binary: VoidPtr, length: GLsizei)
    fun glShaderSource(shader: GLuint, count: GLsizei, string: Array<String>, length: IntArray?)
    fun glStencilFunc(func: GLenum, ref: GLint, mask: GLuint)
    fun glStencilFuncSeparate(face: GLenum, func: GLenum, ref: GLint, mask: GLuint)
    fun glStencilMask(mask: GLuint)
    fun glStencilMaskSeparate(face: GLenum, mask: GLuint)
    fun glStencilOp(fail: GLenum, zfail: GLenum, zpass: GLenum)
    fun glStencilOpSeparate(face: GLenum, sfail: GLenum, dpfail: GLenum, dppass: GLenum)
    fun glTexImage2D(target: GLenum, level: GLint, internalformat: GLint, width: GLsizei, height: GLsizei, border: GLint, format: GLenum, type: GLenum, pixels: VoidPtr?)
    //fun glTexImage2D(target: GLenum, level: GLint, internalformat: GLint, format: GLsizei, type: GLsizei, data: NativeImage)
    fun glTexParameterf(target: GLenum, pname: GLenum, param: GLfloat)
    fun glTexParameterfv(target: GLenum, pname: GLenum, params: FloatPtr)
    fun glTexParameteri(target: GLenum, pname: GLenum, param: GLint)
    fun glTexParameteriv(target: GLenum, pname: GLenum, params: IntPtr)
    fun glTexSubImage2D(target: GLenum, level: GLint, xoffset: GLint, yoffset: GLint, width: GLsizei, height: GLsizei, format: GLenum, type: GLenum, pixels: VoidPtr)
    fun glUniform1f(location: GLint, v0: GLfloat)
    fun glUniform1fv(location: GLint, count: GLsizei, value: FloatPtr)
    fun glUniform1i(location: GLint, v0: GLint)
    fun glUniform1iv(location: GLint, count: GLsizei, value: IntPtr)
    fun glUniform2f(location: GLint, v0: GLfloat, v1: GLfloat)
    fun glUniform2fv(location: GLint, count: GLsizei, value: FloatPtr)
    fun glUniform2i(location: GLint, v0: GLint, v1: GLint)
    fun glUniform2iv(location: GLint, count: GLsizei, value: IntPtr)
    fun glUniform3f(location: GLint, v0: GLfloat, v1: GLfloat, v2: GLfloat)
    fun glUniform3fv(location: GLint, count: GLsizei, value: FloatPtr)
    fun glUniform3i(location: GLint, v0: GLint, v1: GLint, v2: GLint)
    fun glUniform3iv(location: GLint, count: GLsizei, value: IntPtr)
    fun glUniform4f(location: GLint, v0: GLfloat, v1: GLfloat, v2: GLfloat, v3: GLfloat)
    fun glUniform4fv(location: GLint, count: GLsizei, value: FloatPtr)
    fun glUniform4i(location: GLint, v0: GLint, v1: GLint, v2: GLint, v3: GLint)
    fun glUniform4iv(location: GLint, count: GLsizei, value: IntPtr)
    fun glUniformMatrix2fv(location: GLint, count: GLsizei, transpose: GLboolean, value: FloatPtr)
    fun glUniformMatrix3fv(location: GLint, count: GLsizei, transpose: GLboolean, value: FloatPtr)
    fun glUniformMatrix4fv(location: GLint, count: GLsizei, transpose: GLboolean, value: FloatPtr)
    fun glUseProgram(program: GLuint)
    fun glValidateProgram(program: GLuint)
    fun glVertexAttrib1f(index: GLuint, x: GLfloat)
    fun glVertexAttrib1fv(index: GLuint, v: FloatPtr)
    fun glVertexAttrib2f(index: GLuint, x: GLfloat, y: GLfloat)
    fun glVertexAttrib2fv(index: GLuint, v: FloatPtr)
    fun glVertexAttrib3f(index: GLuint, x: GLfloat, y: GLfloat, z: GLfloat)
    fun glVertexAttrib3fv(index: GLuint, v: FloatPtr)
    fun glVertexAttrib4f(index: GLuint, x: GLfloat, y: GLfloat, z: GLfloat, w: GLfloat)
    fun glVertexAttrib4fv(index: GLuint, v: FloatPtr)
    fun glVertexAttribPointer(index: GLuint, size: GLint, type: GLenum, normalized: GLboolean, stride: GLsizei, pointer: IntSize)
    fun glViewport(x: GLint, y: GLint, width: GLsizei, height: GLsizei)

    fun glDrawArraysInstanced(mode: GLenum, first: GLint, count: GLsizei, instancecount: GLsizei): Unit
    fun glDrawElementsInstanced(mode: GLenum, count: GLsizei, type: GLenum, indices: IntSize, instancecount: GLsizei)
    fun glVertexAttribDivisor(index: GLuint, divisor: GLuint)
    fun glRenderbufferStorageMultisample(target: GLenum, samples: GLint, internalformat: GLenum, width: GLsizei, height: GLsizei)

    companion object {
        const val DEPTH_BUFFER_BIT: Int = 0x0100
        const val STENCIL_BUFFER_BIT: Int = 0x0400
        const val COLOR_BUFFER_BIT: Int = 0x4000
        const val FALSE: Int = 0x0000
        const val TRUE: Int = 0x0001
        const val POINTS: Int = 0x0000
        const val LINES: Int = 0x0001
        const val LINE_LOOP: Int = 0x0002
        const val LINE_STRIP: Int = 0x0003
        const val TRIANGLES: Int = 0x0004
        const val TRIANGLE_STRIP: Int = 0x0005
        const val TRIANGLE_FAN: Int = 0x0006
        const val ZERO: Int = 0x0000
        const val ONE: Int = 0x0001
        const val SRC_COLOR: Int = 0x0300
        const val ONE_MINUS_SRC_COLOR: Int = 0x0301
        const val SRC_ALPHA: Int = 0x0302
        const val ONE_MINUS_SRC_ALPHA: Int = 0x0303
        const val DST_ALPHA: Int = 0x0304
        const val ONE_MINUS_DST_ALPHA: Int = 0x0305
        const val DST_COLOR: Int = 0x0306
        const val ONE_MINUS_DST_COLOR: Int = 0x0307
        const val SRC_ALPHA_SATURATE: Int = 0x0308
        const val FUNC_ADD: Int = 0x8006
        const val BLEND_EQUATION: Int = 0x8009
        const val BLEND_EQUATION_RGB: Int = 0x8009
        const val BLEND_EQUATION_ALPHA: Int = 0x883D
        const val FUNC_SUBTRACT: Int = 0x800A
        const val FUNC_REVERSE_SUBTRACT: Int = 0x800B
        const val BLEND_DST_RGB: Int = 0x80C8
        const val BLEND_SRC_RGB: Int = 0x80C9
        const val BLEND_DST_ALPHA: Int = 0x80CA
        const val BLEND_SRC_ALPHA: Int = 0x80CB
        const val CONSTANT_COLOR: Int = 0x8001
        const val ONE_MINUS_CONSTANT_COLOR: Int = 0x8002
        const val CONSTANT_ALPHA: Int = 0x8003
        const val ONE_MINUS_CONSTANT_ALPHA: Int = 0x8004
        const val BLEND_COLOR: Int = 0x8005
        const val ARRAY_BUFFER: Int = 0x8892
        const val ELEMENT_ARRAY_BUFFER: Int = 0x8893
        const val ARRAY_BUFFER_BINDING: Int = 0x8894
        const val ELEMENT_ARRAY_BUFFER_BINDING: Int = 0x8895
        const val STREAM_DRAW: Int = 0x88E0
        const val STATIC_DRAW: Int = 0x88E4
        const val DYNAMIC_DRAW: Int = 0x88E8
        const val BUFFER_SIZE: Int = 0x8764
        const val BUFFER_USAGE: Int = 0x8765
        const val CURRENT_VERTEX_ATTRIB: Int = 0x8626
        const val FRONT: Int = 0x0404
        const val BACK: Int = 0x0405
        const val FRONT_AND_BACK: Int = 0x0408
        const val TEXTURE_2D: Int = 0x0DE1
        const val CULL_FACE: Int = 0x0B44
        const val BLEND: Int = 0x0BE2
        const val DITHER: Int = 0x0BD0
        const val STENCIL_TEST: Int = 0x0B90
        const val DEPTH_TEST: Int = 0x0B71
        const val SCISSOR_TEST: Int = 0x0C11
        const val POLYGON_OFFSET_FILL: Int = 0x8037
        const val SAMPLE_ALPHA_TO_COVERAGE: Int = 0x809E
        const val SAMPLE_COVERAGE: Int = 0x80A0
        const val NO_ERROR: Int = 0x0000 // 0
        const val INVALID_ENUM: Int = 0x0500 // 1280
        const val INVALID_VALUE: Int = 0x0501 // 1281
        const val INVALID_OPERATION: Int = 0x0502 // 1282
        const val OUT_OF_MEMORY: Int = 0x0505 // 1283
        const val CW: Int = 0x0900
        const val CCW: Int = 0x0901
        const val LINE_WIDTH: Int = 0x0B21
        const val ALIASED_POINT_SIZE_RANGE: Int = 0x846D
        const val ALIASED_LINE_WIDTH_RANGE: Int = 0x846E
        const val CULL_FACE_MODE: Int = 0x0B45
        const val FRONT_FACE: Int = 0x0B46
        const val DEPTH_RANGE: Int = 0x0B70
        const val DEPTH_WRITEMASK: Int = 0x0B72
        const val DEPTH_CLEAR_VALUE: Int = 0x0B73
        const val DEPTH_FUNC: Int = 0x0B74
        const val STENCIL_CLEAR_VALUE: Int = 0x0B91
        const val STENCIL_FUNC: Int = 0x0B92
        const val STENCIL_FAIL: Int = 0x0B94
        const val STENCIL_PASS_DEPTH_FAIL: Int = 0x0B95
        const val STENCIL_PASS_DEPTH_PASS: Int = 0x0B96
        const val STENCIL_REF: Int = 0x0B97
        const val STENCIL_VALUE_MASK: Int = 0x0B93
        const val STENCIL_WRITEMASK: Int = 0x0B98
        const val STENCIL_BACK_FUNC: Int = 0x8800
        const val STENCIL_BACK_FAIL: Int = 0x8801
        const val STENCIL_BACK_PASS_DEPTH_FAIL: Int = 0x8802
        const val STENCIL_BACK_PASS_DEPTH_PASS: Int = 0x8803
        const val STENCIL_BACK_REF: Int = 0x8CA3
        const val STENCIL_BACK_VALUE_MASK: Int = 0x8CA4
        const val STENCIL_BACK_WRITEMASK: Int = 0x8CA5
        const val VIEWPORT: Int = 0x0BA2
        const val SCISSOR_BOX: Int = 0x0C10
        const val COLOR_CLEAR_VALUE: Int = 0x0C22
        const val COLOR_WRITEMASK: Int = 0x0C23
        const val UNPACK_ALIGNMENT: Int = 0x0CF5
        const val PACK_ALIGNMENT: Int = 0x0D05
        const val MAX_TEXTURE_SIZE: Int = 0x0D33
        const val MAX_VIEWPORT_DIMS: Int = 0x0D3A
        const val SUBPIXEL_BITS: Int = 0x0D50
        const val RED_BITS: Int = 0x0D52
        const val GREEN_BITS: Int = 0x0D53
        const val BLUE_BITS: Int = 0x0D54
        const val ALPHA_BITS: Int = 0x0D55
        const val DEPTH_BITS: Int = 0x0D56
        const val STENCIL_BITS: Int = 0x0D57
        const val POLYGON_OFFSET_UNITS: Int = 0x2A00
        const val POLYGON_OFFSET_FACTOR: Int = 0x8038
        const val TEXTURE_BINDING_2D: Int = 0x8069
        const val SAMPLE_BUFFERS: Int = 0x80A8
        const val SAMPLES: Int = 0x80A9
        const val SAMPLE_COVERAGE_VALUE: Int = 0x80AA
        const val SAMPLE_COVERAGE_INVERT: Int = 0x80AB
        const val NUM_COMPRESSED_TEXTURE_FORMATS: Int = 0x86A2
        const val COMPRESSED_TEXTURE_FORMATS: Int = 0x86A3
        const val DONT_CARE: Int = 0x1100
        const val FASTEST: Int = 0x1101
        const val NICEST: Int = 0x1102
        const val GENERATE_MIPMAP_HINT: Int = 0x8192
        const val BYTE: Int = 0x1400
        const val UNSIGNED_BYTE: Int = 0x1401
        const val SHORT: Int = 0x1402
        const val UNSIGNED_SHORT: Int = 0x1403
        const val INT: Int = 0x1404
        const val UNSIGNED_INT: Int = 0x1405
        const val FLOAT: Int = 0x1406
        const val FIXED: Int = 0x140C
        const val DEPTH_COMPONENT: Int = 0x1902
        const val ALPHA: Int = 0x1906
        const val RGB: Int = 0x1907
        const val RGBA: Int = 0x1908
        const val LUMINANCE: Int = 0x1909
        const val LUMINANCE_ALPHA: Int = 0x190A
        const val UNSIGNED_SHORT_4_4_4_4: Int = 0x8033
        const val UNSIGNED_SHORT_5_5_5_1: Int = 0x8034
        const val UNSIGNED_SHORT_5_6_5: Int = 0x8363
        const val FRAGMENT_SHADER: Int = 0x8B30
        const val VERTEX_SHADER: Int = 0x8B31
        const val MAX_VERTEX_ATTRIBS: Int = 0x8869
        const val MAX_VERTEX_UNIFORM_VECTORS: Int = 0x8DFB
        const val MAX_VARYING_VECTORS: Int = 0x8DFC
        const val MAX_COMBINED_TEXTURE_IMAGE_UNITS: Int = 0x8B4D
        const val MAX_VERTEX_TEXTURE_IMAGE_UNITS: Int = 0x8B4C
        const val MAX_TEXTURE_IMAGE_UNITS: Int = 0x8872
        const val MAX_FRAGMENT_UNIFORM_VECTORS: Int = 0x8DFD
        const val SHADER_TYPE: Int = 0x8B4F
        const val DELETE_STATUS: Int = 0x8B80
        const val LINK_STATUS: Int = 0x8B82
        const val VALIDATE_STATUS: Int = 0x8B83
        const val ATTACHED_SHADERS: Int = 0x8B85
        const val ACTIVE_UNIFORMS: Int = 0x8B86
        const val ACTIVE_UNIFORM_MAX_LENGTH: Int = 0x8B87
        const val ACTIVE_ATTRIBUTES: Int = 0x8B89
        const val ACTIVE_ATTRIBUTE_MAX_LENGTH: Int = 0x8B8A
        const val SHADING_LANGUAGE_VERSION: Int = 0x8B8C
        const val CURRENT_PROGRAM: Int = 0x8B8D
        const val NEVER: Int = 0x0200
        const val LESS: Int = 0x0201
        const val EQUAL: Int = 0x0202
        const val LEQUAL: Int = 0x0203
        const val GREATER: Int = 0x0204
        const val NOTEQUAL: Int = 0x0205
        const val GEQUAL: Int = 0x0206
        const val ALWAYS: Int = 0x0207
        const val KEEP: Int = 0x1E00
        const val REPLACE: Int = 0x1E01
        const val INCR: Int = 0x1E02
        const val DECR: Int = 0x1E03
        const val INVERT: Int = 0x150A
        const val INCR_WRAP: Int = 0x8507
        const val DECR_WRAP: Int = 0x8508
        const val VENDOR: Int = 0x1F00
        const val RENDERER: Int = 0x1F01
        const val VERSION: Int = 0x1F02
        const val EXTENSIONS: Int = 0x1F03
        const val NEAREST: Int = 0x2600
        const val LINEAR: Int = 0x2601
        const val NEAREST_MIPMAP_NEAREST: Int = 0x2700
        const val LINEAR_MIPMAP_NEAREST: Int = 0x2701
        const val NEAREST_MIPMAP_LINEAR: Int = 0x2702
        const val LINEAR_MIPMAP_LINEAR: Int = 0x2703
        const val TEXTURE_MAG_FILTER: Int = 0x2800
        const val TEXTURE_MIN_FILTER: Int = 0x2801
        const val TEXTURE_WRAP_S: Int = 0x2802
        const val TEXTURE_WRAP_T: Int = 0x2803
        const val TEXTURE: Int = 0x1702
        const val TEXTURE_CUBE_MAP: Int = 0x8513
        const val TEXTURE_BINDING_CUBE_MAP: Int = 0x8514
        const val TEXTURE_CUBE_MAP_POSITIVE_X: Int = 0x8515
        const val TEXTURE_CUBE_MAP_NEGATIVE_X: Int = 0x8516
        const val TEXTURE_CUBE_MAP_POSITIVE_Y: Int = 0x8517
        const val TEXTURE_CUBE_MAP_NEGATIVE_Y: Int = 0x8518
        const val TEXTURE_CUBE_MAP_POSITIVE_Z: Int = 0x8519
        const val TEXTURE_CUBE_MAP_NEGATIVE_Z: Int = 0x851A
        const val MAX_CUBE_MAP_TEXTURE_SIZE: Int = 0x851C
        const val TEXTURE0: Int = 0x84C0
        const val TEXTURE1: Int = 0x84C1
        const val TEXTURE2: Int = 0x84C2
        const val TEXTURE3: Int = 0x84C3
        const val TEXTURE4: Int = 0x84C4
        const val TEXTURE5: Int = 0x84C5
        const val TEXTURE6: Int = 0x84C6
        const val TEXTURE7: Int = 0x84C7
        const val TEXTURE8: Int = 0x84C8
        const val TEXTURE9: Int = 0x84C9
        const val TEXTURE10: Int = 0x84CA
        const val TEXTURE11: Int = 0x84CB
        const val TEXTURE12: Int = 0x84CC
        const val TEXTURE13: Int = 0x84CD
        const val TEXTURE14: Int = 0x84CE
        const val TEXTURE15: Int = 0x84CF
        const val TEXTURE16: Int = 0x84D0
        const val TEXTURE17: Int = 0x84D1
        const val TEXTURE18: Int = 0x84D2
        const val TEXTURE19: Int = 0x84D3
        const val TEXTURE20: Int = 0x84D4
        const val TEXTURE21: Int = 0x84D5
        const val TEXTURE22: Int = 0x84D6
        const val TEXTURE23: Int = 0x84D7
        const val TEXTURE24: Int = 0x84D8
        const val TEXTURE25: Int = 0x84D9
        const val TEXTURE26: Int = 0x84DA
        const val TEXTURE27: Int = 0x84DB
        const val TEXTURE28: Int = 0x84DC
        const val TEXTURE29: Int = 0x84DD
        const val TEXTURE30: Int = 0x84DE
        const val TEXTURE31: Int = 0x84DF
        const val ACTIVE_TEXTURE: Int = 0x84E0
        const val REPEAT: Int = 0x2901
        const val CLAMP_TO_EDGE: Int = 0x812F
        const val MIRRORED_REPEAT: Int = 0x8370
        const val FLOAT_VEC2: Int = 0x8B50
        const val FLOAT_VEC3: Int = 0x8B51
        const val FLOAT_VEC4: Int = 0x8B52
        const val INT_VEC2: Int = 0x8B53
        const val INT_VEC3: Int = 0x8B54
        const val INT_VEC4: Int = 0x8B55
        const val BOOL: Int = 0x8B56
        const val BOOL_VEC2: Int = 0x8B57
        const val BOOL_VEC3: Int = 0x8B58
        const val BOOL_VEC4: Int = 0x8B59
        const val FLOAT_MAT2: Int = 0x8B5A
        const val FLOAT_MAT3: Int = 0x8B5B
        const val FLOAT_MAT4: Int = 0x8B5C
        const val SAMPLER_2D: Int = 0x8B5E
        const val SAMPLER_CUBE: Int = 0x8B60
        const val VERTEX_ATTRIB_ARRAY_ENABLED: Int = 0x8622
        const val VERTEX_ATTRIB_ARRAY_SIZE: Int = 0x8623
        const val VERTEX_ATTRIB_ARRAY_STRIDE: Int = 0x8624
        const val VERTEX_ATTRIB_ARRAY_TYPE: Int = 0x8625
        const val VERTEX_ATTRIB_ARRAY_NORMALIZED: Int = 0x886A
        const val VERTEX_ATTRIB_ARRAY_POINTER: Int = 0x8645
        const val VERTEX_ATTRIB_ARRAY_BUFFER_BINDING: Int = 0x889F
        const val IMPLEMENTATION_COLOR_READ_TYPE: Int = 0x8B9A
        const val IMPLEMENTATION_COLOR_READ_FORMAT: Int = 0x8B9B
        const val COMPILE_STATUS: Int = 0x8B81
        const val INFO_LOG_LENGTH: Int = 0x8B84
        const val SHADER_SOURCE_LENGTH: Int = 0x8B88
        const val SHADER_COMPILER: Int = 0x8DFA
        const val SHADER_BINARY_FORMATS: Int = 0x8DF8
        const val NUM_SHADER_BINARY_FORMATS: Int = 0x8DF9
        const val LOW_FLOAT: Int = 0x8DF0
        const val MEDIUM_FLOAT: Int = 0x8DF1
        const val HIGH_FLOAT: Int = 0x8DF2
        const val LOW_INT: Int = 0x8DF3
        const val MEDIUM_INT: Int = 0x8DF4
        const val HIGH_INT: Int = 0x8DF5
        const val FRAMEBUFFER: Int = 0x8D40
        const val RENDERBUFFER: Int = 0x8D41
        const val RGBA4: Int = 0x8056
        const val RGB5_A1: Int = 0x8057
        const val RGB565: Int = 0x8D62
        const val DEPTH_COMPONENT16: Int = 0x81A5
        const val STENCIL_INDEX8: Int = 0x8D48
        const val RENDERBUFFER_WIDTH: Int = 0x8D42
        const val RENDERBUFFER_HEIGHT: Int = 0x8D43
        const val RENDERBUFFER_INTERNAL_FORMAT: Int = 0x8D44
        const val RENDERBUFFER_RED_SIZE: Int = 0x8D50
        const val RENDERBUFFER_GREEN_SIZE: Int = 0x8D51
        const val RENDERBUFFER_BLUE_SIZE: Int = 0x8D52
        const val RENDERBUFFER_ALPHA_SIZE: Int = 0x8D53
        const val RENDERBUFFER_DEPTH_SIZE: Int = 0x8D54
        const val RENDERBUFFER_STENCIL_SIZE: Int = 0x8D55
        const val FRAMEBUFFER_ATTACHMENT_OBJECT_TYPE: Int = 0x8CD0
        const val FRAMEBUFFER_ATTACHMENT_OBJECT_NAME: Int = 0x8CD1
        const val FRAMEBUFFER_ATTACHMENT_TEXTURE_LEVEL: Int = 0x8CD2
        const val FRAMEBUFFER_ATTACHMENT_TEXTURE_CUBE_MAP_FACE: Int = 0x8CD3
        const val COLOR_ATTACHMENT0: Int = 0x8CE0
        const val DEPTH_ATTACHMENT: Int = 0x8D00
        const val STENCIL_ATTACHMENT: Int = 0x8D20
        const val NONE: Int = 0x0000
        const val FRAMEBUFFER_COMPLETE: Int = 0x8CD5
        const val FRAMEBUFFER_INCOMPLETE_ATTACHMENT: Int = 0x8CD6
        const val FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT: Int = 0x8CD7
        const val FRAMEBUFFER_INCOMPLETE_DIMENSIONS: Int = 0x8CD9
        const val FRAMEBUFFER_UNSUPPORTED: Int = 0x8CDD
        const val FRAMEBUFFER_BINDING: Int = 0x8CA6
        const val RENDERBUFFER_BINDING: Int = 0x8CA7
        const val MAX_RENDERBUFFER_SIZE: Int = 0x84E8
        const val INVALID_FRAMEBUFFER_OPERATION: Int = 0x0506 // 1286
    }
}

/*
    fun glActiveTexture(texture: Int)
    fun glAttachShader(program: Int, shader: Int)
    fun glBindAttribLocation(program: Int, index: Int, name: String)
    fun glBindBuffer(target: Int, buffer: Int)
    fun glBindFramebuffer(target: Int, framebuffer: Int)
    fun glBindRenderbuffer(target: Int, renderbuffer: Int)
    fun glBindTexture(target: Int, texture: Int)
    fun glBlendColor(red: Float, green: Float, blue: Float, alpha: Float)
    fun glBlendEquation(mode: Int)
    fun glBlendEquationSeparate(modeRGB: Int, modeAlpha: Int)
    fun glBlendFunc(sfactor: Int, dfactor: Int)
    fun glBlendFuncSeparate(sfactorRGB: Int, dfactorRGB: Int, sfactorAlpha: Int, dfactorAlpha: Int)
    fun glBufferData(target: Int, size: IntSize, data: VoidPtr, usage: Int)
    fun glBufferSubData(target: Int, offset: IntSize, size: IntSize, data: VoidPtr)
    fun glCheckFramebufferStatus(target: Int): Int
    fun glClear(mask: Int)
    fun glClearColor(red: Float, green: Float, blue: Float, alpha: Float)
    fun glClearDepth(d: Double)
    fun glClearStencil(s: Int)
    fun glColorMask(red: Boolean, green: Boolean, blue: Boolean, alpha: Boolean)
    fun glCompileShader(shader: Int)
    fun glCompressedTexImage2D(
        target: Int,
        level: Int,
        internalformat: Int,
        width: Int,
        height: Int,
        border: Int,
        imageSize: Int,
        data: VoidPtr
    )

    fun glCompressedTexSubImage2D(
        target: Int,
        level: Int,
        xoffset: Int,
        yoffset: Int,
        width: Int,
        height: Int,
        format: Int,
        imageSize: Int,
        data: VoidPtr
    )

    fun glCopyTexImage2D(
        target: Int,
        level: Int,
        internalformat: Int,
        x: Int,
        y: Int,
        width: Int,
        height: Int,
        border: Int
    )

    fun glCopyTexSubImage2D(
        target: Int,
        level: Int,
        xoffset: Int,
        yoffset: Int,
        x: Int,
        y: Int,
        width: Int,
        height: Int
    )

    fun glCreateProgram(): Int
    fun glCreateShader(type: Int): Int
    fun glCullFace(mode: Int)
    fun glDeleteBuffers(n: Int, items: IntPtr)
    fun glDeleteFramebuffers(n: Int, items: IntPtr)
    fun glDeleteProgram(program: Int)
    fun glDeleteRenderbuffers(n: Int, items: IntPtr)
    fun glDeleteShader(shader: Int)
    fun glDeleteTextures(n: Int, items: IntPtr)
    fun glDepthFunc(func: Int)
    fun glDepthMask(flag: Boolean)
    fun glDepthRange(n: Double, f: Double)
    fun glDetachShader(program: Int, shader: Int)
    fun glDisable(cap: Int)
    fun glDisableVertexAttribArray(index: Int)
    fun glDrawArrays(mode: Int, first: Int, count: Int)
    fun glDrawElements(mode: Int, count: Int, type: Int, indices: IntSize)
    fun glEnable(cap: Int)
    fun glEnableVertexAttribArray(index: Int)
    fun glFinish()
    fun glFlush()
    fun glFramebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: Int)
    fun glFramebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: Int, level: Int)
    fun glFrontFace(mode: Int)
    fun glGenBuffers(n: Int, buffers: IntPtr)
    fun glGenerateMipmap(target: Int)
    fun glGenFramebuffers(n: Int, framebuffers: IntPtr)
    fun glGenRenderbuffers(n: Int, renderbuffers: IntPtr)
    fun glGenTextures(n: Int, textures: IntPtr)
    fun glGetActiveAttrib(
        program: Int,
        index: Int,
        bufSize: Int,
        length: IntPtr,
        size: IntPtr,
        type: IntPtr,
        name: VoidPtr
    )

    fun glGetActiveUniform(
        program: Int,
        index: Int,
        bufSize: Int,
        length: IntPtr,
        size: IntPtr,
        type: IntPtr,
        name: VoidPtr
    )

    fun glGetAttachedShaders(program: Int, maxCount: Int, count: IntPtr, shaders: IntPtr)
    fun glGetAttribLocation(program: Int, name: String): Int
    fun glGetUniformLocation(program: Int, name: String): Int
    fun glGetBooleanv(pname: Int, data: VoidPtr)
    fun glGetBufferParameteriv(target: Int, pname: Int, params: IntPtr)
    fun glGetError(): Int
    fun glGetFloatv(pname: Int, data: FloatPtr)
    fun glGetFramebufferAttachmentParameteriv(target: Int, attachment: Int, pname: Int, params: IntPtr)
    fun glGetIntegerv(pname: Int, data: IntPtr)
    fun glGetProgramInfoLog(program: Int, bufSize: Int, length: IntPtr, infoLog: VoidPtr)
    fun glGetRenderbufferParameteriv(target: Int, pname: Int, params: IntPtr)
    fun glGetProgramiv(program: Int, pname: Int, params: IntPtr)
    fun glGetShaderiv(shader: Int, pname: Int, params: IntPtr)
    fun glGetShaderInfoLog(shader: Int, bufSize: Int, length: IntPtr, infoLog: VoidPtr)
    fun glGetShaderPrecisionFormat(shadertype: Int, precisiontype: Int, range: IntPtr, precision: IntPtr)
    fun glGetShaderSource(shader: Int, bufSize: Int, length: IntPtr, source: VoidPtr)
    fun glGetString(name: Int): String
    fun glGetTexParameterfv(target: Int, pname: Int, params: FloatPtr)
    fun glGetTexParameteriv(target: Int, pname: Int, params: IntPtr)
    fun glGetUniformfv(program: Int, location: Int, params: FloatPtr)
    fun glGetUniformiv(program: Int, location: Int, params: IntPtr)
    fun glGetVertexAttribfv(index: Int, pname: Int, params: FloatPtr)
    fun glGetVertexAttribiv(index: Int, pname: Int, params: IntPtr)
    fun glGetVertexAttribPointerv(index: Int, pname: Int, pointer: FBuffer)
    fun glHint(target: Int, mode: Int)
    fun glIsBuffer(buffer: Int): Boolean
    fun glIsEnabled(cap: Int): Boolean
    fun glIsFramebuffer(framebuffer: Int): Boolean
    fun glIsProgram(program: Int): Boolean
    fun glIsRenderbuffer(renderbuffer: Int): Boolean
    fun glIsShader(shader: Int): Boolean
    fun glIsTexture(texture: Int): Boolean
    fun glLineWidth(width: Float)
    fun glLinkProgram(program: Int)
    fun glPixelStorei(pname: Int, param: Int)
    fun glPolygonOffset(factor: Float, units: Float)
    fun glReadPixels(x: Int, y: Int, width: Int, height: Int, format: Int, type: Int, pixels: VoidPtr)
    fun glReleaseShaderCompiler()
    fun glRenderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int)
    fun glSampleCoverage(value: Float, invert: Boolean)
    fun glScissor(x: Int, y: Int, width: Int, height: Int)
    fun glShaderBinary(count: Int, shaders: IntPtr, binaryformat: Int, binary: VoidPtr, length: Int)
    fun glShaderSource(shader: Int, count: IntSize, string: Array<String>, length: IntArray?)
    fun glStencilFunc(func: Int, ref: Int, mask: Int)
    fun glStencilFuncSeparate(face: Int, func: Int, ref: Int, mask: Int)
    fun glStencilMask(mask: Int)
    fun glStencilMaskSeparate(face: Int, mask: Int)
    fun glStencilOp(fail: Int, zfail: Int, zpass: Int)
    fun glStencilOpSeparate(face: Int, sfail: Int, dpfail: Int, dppass: Int)
    fun glTexImage2D(
        target: Int,
        level: Int,
        internalformat: Int,
        width: Int,
        height: Int,
        border: Int,
        format: Int,
        type: Int,
        pixels: VoidPtr?
    )

    fun glTexImage2D(target: Int, level: Int, internalformat: Int, format: Int, type: Int, data: NativeImage)
    fun glTexParameterf(target: Int, pname: Int, param: Float)
    fun glTexParameterfv(target: Int, pname: Int, params: FloatPtr)
    fun glTexParameteri(target: Int, pname: Int, param: Int)
    fun glTexParameteriv(target: Int, pname: Int, params: IntPtr)
    fun glTexSubImage2D(
        target: Int,
        level: Int,
        xoffset: Int,
        yoffset: Int,
        width: Int,
        height: Int,
        format: Int,
        type: Int,
        pixels: VoidPtr
    )

    fun glUniform1f(location: Int, v0: Float)
    fun glUniform1fv(location: Int, count: Int, value: FloatPtr)
    fun glUniform1i(location: Int, v0: Int)
    fun glUniform1iv(location: Int, count: Int, value: IntPtr)
    fun glUniform2f(location: Int, v0: Float, v1: Float)
    fun glUniform2fv(location: Int, count: Int, value: FloatPtr)
    fun glUniform2i(location: Int, v0: Int, v1: Int)
    fun glUniform2iv(location: Int, count: Int, value: IntPtr)
    fun glUniform3f(location: Int, v0: Float, v1: Float, v2: Float)
    fun glUniform3fv(location: Int, count: Int, value: FloatPtr)
    fun glUniform3i(location: Int, v0: Int, v1: Int, v2: Int)
    fun glUniform3iv(location: Int, count: Int, value: IntPtr)
    fun glUniform4f(location: Int, v0: Float, v1: Float, v2: Float, v3: Float)
    fun glUniform4fv(location: Int, count: Int, value: FloatPtr)
    fun glUniform4i(location: Int, v0: Int, v1: Int, v2: Int, v3: Int)
    fun glUniform4iv(location: Int, count: Int, value: IntPtr)
    fun glUniformMatrix2fv(location: Int, count: Int, transpose: Boolean, value: FloatPtr)
    fun glUniformMatrix3fv(location: Int, count: Int, transpose: Boolean, value: FloatPtr)
    fun glUniformMatrix4fv(location: Int, count: Int, transpose: Boolean, value: FloatPtr)
    fun glUseProgram(program: Int)
    fun glValidateProgram(program: Int)
    fun glVertexAttrib1f(index: Int, x: Float)
    fun glVertexAttrib1fv(index: Int, v: FloatPtr)
    fun glVertexAttrib2f(index: Int, x: Float, y: Float)
    fun glVertexAttrib2fv(index: Int, v: FloatPtr)
    fun glVertexAttrib3f(index: Int, x: Float, y: Float, z: Float)
    fun glVertexAttrib3fv(index: Int, v: FloatPtr)
    fun glVertexAttrib4f(index: Int, x: Float, y: Float, z: Float, w: Float)
    fun glVertexAttrib4fv(index: Int, v: FloatPtr)
    fun glVertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, pointer: IntSize)
    fun glViewport(x: Int, y: Int, width: Int, height: Int)
 */

/*

    fun glActiveTexture(texture: Int)
    fun glAttachShader(program: Int, shader: Int)
    fun glBindAttribLocation(program: Int, index: Int, name: String)
    fun glBindBuffer(target: Int, buffer: Int)
    fun glBindFramebuffer(target: Int, framebuffer: Int)
    fun glBindRenderbuffer(target: Int, renderbuffer: Int)
    fun glBindTexture(target: Int, texture: Int)
    fun glBlendColor(red: Float, green: Float, blue: Float, alpha: Float)
    fun glBlendEquation(mode: Int)
    fun glBlendEquationSeparate(modeRGB: Int, modeAlpha: Int)
    fun glBlendFunc(sfactor: Int, dfactor: Int)
    fun glBlendFuncSeparate(sfactorRGB: Int, dfactorRGB: Int, sfactorAlpha: Int, dfactorAlpha: Int)
    fun glBufferData(target: Int, size: IntSize, data: VoidPtr, usage: Int)
    fun glBufferSubData(target: Int, offset: IntSize, size: IntSize, data: VoidPtr)
    fun glCheckFramebufferStatus(target: Int): Int
    fun glClear(mask: Int)
    fun glClearColor(red: Float, green: Float, blue: Float, alpha: Float)
    fun glClearDepth(d: Double)
    fun glClearStencil(s: Int)
    fun glColorMask(red: Boolean, green: Boolean, blue: Boolean, alpha: Boolean)
    fun glCompileShader(shader: Int)
    fun glCompressedTexImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, imageSize: Int, data: VoidPtr)
    fun glCompressedTexSubImage2D(target: Int, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int, format: Int, imageSize: Int, data: VoidPtr)
    fun glCopyTexImage2D(target: Int, level: Int, internalformat: Int, x: Int, y: Int, width: Int, height: Int, border: Int)
    fun glCopyTexSubImage2D(target: Int, level: Int, xoffset: Int, yoffset: Int, x: Int, y: Int, width: Int, height: Int)
    fun glCreateProgram(): Int
    fun glCreateShader(type: Int): Int
    fun glCullFace(mode: Int)
    fun glDeleteBuffers(n: Int, items: IntPtr)
    fun glDeleteFramebuffers(n: Int, items: IntPtr)
    fun glDeleteProgram(program: Int)
    fun glDeleteRenderbuffers(n: Int, items: IntPtr)
    fun glDeleteShader(shader: Int)
    fun glDeleteTextures(n: Int, items: IntPtr)
    fun glDepthFunc(func: Int)
    fun glDepthMask(flag: Boolean)
    fun glDepthRange(n: Double, f: Double)
    fun glDetachShader(program: Int, shader: Int)
    fun glDisable(cap: Int)
    fun glDisableVertexAttribArray(index: Int)
    fun glDrawArrays(mode: Int, first: Int, count: Int)
    fun glDrawElements(mode: Int, count: Int, type: Int, indices: IntSize)
    fun glEnable(cap: Int)
    fun glEnableVertexAttribArray(index: Int)
    fun glFinish()
    fun glFlush()
    fun glFramebufferRenderbuffer(target: Int, attachment: Int, renderbuffertarget: Int, renderbuffer: Int)
    fun glFramebufferTexture2D(target: Int, attachment: Int, textarget: Int, texture: Int, level: Int)
    fun glFrontFace(mode: Int)
    fun glGenBuffers(n: Int, buffers: IntPtr)
    fun glGenerateMipmap(target: Int)
    fun glGenFramebuffers(n: Int, framebuffers: IntPtr)
    fun glGenRenderbuffers(n: Int, renderbuffers: IntPtr)
    fun glGenTextures(n: Int, textures: IntPtr)
    fun glGetActiveAttrib(program: Int, index: Int, bufSize: Int, length: IntPtr, size: IntPtr, type: IntPtr, name: VoidPtr)
    fun glGetActiveUniform(program: Int, index: Int, bufSize: Int, length: IntPtr, size: IntPtr, type: IntPtr, name: VoidPtr)
    fun glGetAttachedShaders(program: Int, maxCount: Int, count: IntPtr, shaders: IntPtr)
    fun glGetAttribLocation(program: Int, name: String): Int
    fun glGetUniformLocation(program: Int, name: String): Int
    fun glGetBooleanv(pname: Int, data: VoidPtr)
    fun glGetBufferParameteriv(target: Int, pname: Int, params: IntPtr)
    fun glGetError(): Int
    fun glGetFloatv(pname: Int, data: FloatPtr)
    fun glGetFramebufferAttachmentParameteriv(target: Int, attachment: Int, pname: Int, params: IntPtr)
    fun glGetIntegerv(pname: Int, data: IntPtr)
    fun glGetProgramInfoLog(program: Int, bufSize: Int, length: IntPtr, infoLog: VoidPtr)
    fun glGetRenderbufferParameteriv(target: Int, pname: Int, params: IntPtr)
    fun glGetProgramiv(program: Int, pname: Int, params: IntPtr)
    fun glGetShaderiv(shader: Int, pname: Int, params: IntPtr)
    fun glGetShaderInfoLog(shader: Int, bufSize: Int, length: IntPtr, infoLog: VoidPtr)
    fun glGetShaderPrecisionFormat(shadertype: Int, precisiontype: Int, range: IntPtr, precision: IntPtr)
    fun glGetShaderSource(shader: Int, bufSize: Int, length: IntPtr, source: VoidPtr)
    fun glGetString(name: Int): String?
    fun glGetTexParameterfv(target: Int, pname: Int, params: FloatPtr)
    fun glGetTexParameteriv(target: Int, pname: Int, params: IntPtr)
    fun glGetUniformfv(program: Int, location: Int, params: FloatPtr)
    fun glGetUniformiv(program: Int, location: Int, params: IntPtr)
    fun glGetVertexAttribfv(index: Int, pname: Int, params: FloatPtr)
    fun glGetVertexAttribiv(index: Int, pname: Int, params: IntPtr)
    fun glGetVertexAttribPointerv(index: Int, pname: Int, pointer: FBuffer)
    fun glHint(target: Int, mode: Int)
    fun glIsBuffer(buffer: Int): Boolean
    fun glIsEnabled(cap: Int): Boolean
    fun glIsFramebuffer(framebuffer: Int): Boolean
    fun glIsProgram(program: Int): Boolean
    fun glIsRenderbuffer(renderbuffer: Int): Boolean
    fun glIsShader(shader: Int): Boolean
    fun glIsTexture(texture: Int): Boolean
    fun glLineWidth(width: Float)
    fun glLinkProgram(program: Int)
    fun glPixelStorei(pname: Int, param: Int)
    fun glPolygonOffset(factor: Float, units: Float)
    fun glReadPixels(x: Int, y: Int, width: Int, height: Int, format: Int, type: Int, pixels: VoidPtr)
    fun glReleaseShaderCompiler()
    fun glRenderbufferStorage(target: Int, internalformat: Int, width: Int, height: Int)
    fun glSampleCoverage(value: Float, invert: Boolean)
    fun glScissor(x: Int, y: Int, width: Int, height: Int)
    fun glShaderBinary(count: Int, shaders: IntPtr, binaryformat: Int, binary: VoidPtr, length: Int)
    fun glShaderSource(shader: Int, count: IntSize, string: Array<String>, length: IntArray?)
    fun glStencilFunc(func: Int, ref: Int, mask: Int)
    fun glStencilFuncSeparate(face: Int, func: Int, ref: Int, mask: Int)
    fun glStencilMask(mask: Int)
    fun glStencilMaskSeparate(face: Int, mask: Int)
    fun glStencilOp(fail: Int, zfail: Int, zpass: Int)
    fun glStencilOpSeparate(face: Int, sfail: Int, dpfail: Int, dppass: Int)
    fun glTexImage2D(target: Int, level: Int, internalformat: Int, width: Int, height: Int, border: Int, format: Int, type: Int, pixels: VoidPtr?)
    fun glTexImage2D(target: Int, level: Int, internalformat: Int, format: Int, type: Int, data: NativeImage)
    fun glTexParameterf(target: Int, pname: Int, param: Float)
    fun glTexParameterfv(target: Int, pname: Int, params: FloatPtr)
    fun glTexParameteri(target: Int, pname: Int, param: Int)
    fun glTexParameteriv(target: Int, pname: Int, params: IntPtr)
    fun glTexSubImage2D(target: Int, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int, format: Int, type: Int, pixels: VoidPtr)
    fun glUniform1f(location: Int, v0: Float)
    fun glUniform1fv(location: Int, count: Int, value: FloatPtr)
    fun glUniform1i(location: Int, v0: Int)
    fun glUniform1iv(location: Int, count: Int, value: IntPtr)
    fun glUniform2f(location: Int, v0: Float, v1: Float)
    fun glUniform2fv(location: Int, count: Int, value: FloatPtr)
    fun glUniform2i(location: Int, v0: Int, v1: Int)
    fun glUniform2iv(location: Int, count: Int, value: IntPtr)
    fun glUniform3f(location: Int, v0: Float, v1: Float, v2: Float)
    fun glUniform3fv(location: Int, count: Int, value: FloatPtr)
    fun glUniform3i(location: Int, v0: Int, v1: Int, v2: Int)
    fun glUniform3iv(location: Int, count: Int, value: IntPtr)
    fun glUniform4f(location: Int, v0: Float, v1: Float, v2: Float, v3: Float)
    fun glUniform4fv(location: Int, count: Int, value: FloatPtr)
    fun glUniform4i(location: Int, v0: Int, v1: Int, v2: Int, v3: Int)
    fun glUniform4iv(location: Int, count: Int, value: IntPtr)
    fun glUniformMatrix2fv(location: Int, count: Int, transpose: Boolean, value: FloatPtr)
    fun glUniformMatrix3fv(location: Int, count: Int, transpose: Boolean, value: FloatPtr)
    fun glUniformMatrix4fv(location: Int, count: Int, transpose: Boolean, value: FloatPtr)
    fun glUseProgram(program: Int)
    fun glValidateProgram(program: Int)
    fun glVertexAttrib1f(index: Int, x: Float)
    fun glVertexAttrib1fv(index: Int, v: FloatPtr)
    fun glVertexAttrib2f(index: Int, x: Float, y: Float)
    fun glVertexAttrib2fv(index: Int, v: FloatPtr)
    fun glVertexAttrib3f(index: Int, x: Float, y: Float, z: Float)
    fun glVertexAttrib3fv(index: Int, v: FloatPtr)
    fun glVertexAttrib4f(index: Int, x: Float, y: Float, z: Float, w: Float)
    fun glVertexAttrib4fv(index: Int, v: FloatPtr)
    fun glVertexAttribPointer(index: Int, size: Int, type: Int, normalized: Boolean, stride: Int, pointer: IntSize)
    fun glViewport(x: Int, y: Int, width: Int, height: Int)

 */
