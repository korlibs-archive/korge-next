package com.soywiz.korge3d

import com.soywiz.kds.floatArrayListOf
import com.soywiz.korag.AG
import com.soywiz.korag.shader.VertexLayout
import com.soywiz.korge3d.internal.toFBuffer
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korio.util.buildList
import com.soywiz.korma.geom.Vector3D

@Korge3DExperimental
class MeshBuilder3D {
    val layout = VertexLayout(buildList {
        add(Shaders3D.a_pos)
        add(Shaders3D.a_norm)
        add(Shaders3D.a_tex)
    })

    operator fun invoke(callback: MeshBuilder3D.() -> Unit): Mesh3D = this.apply(callback).build()

    companion object {
        operator fun invoke(callback: MeshBuilder3D.() -> Unit): Mesh3D = MeshBuilder3D().apply(callback).build()
    }

    val data = floatArrayListOf()
    private var _material: Material3D? = null

    fun material(
        emission: Material3D.Light = Material3D.LightColor(Colors.BLACK),
        //default with some ambient so that we see something if material not set
        ambient: Material3D.Light = Material3D.LightColor(RGBA(0x2, 0x2, 0x2, 0xFF)),
        diffuse: Material3D.Light = Material3D.LightColor(Colors.BLACK),
        specular: Material3D.Light = Material3D.LightColor(Colors.BLACK),
        shininess: Float = .5f,
        indexOfRefraction: Float = 1f
    ) {
        _material = Material3D(emission, ambient, diffuse, specular, shininess, indexOfRefraction)
    }

    fun vertex(pos: Vector3D, normal: Vector3D, texcoords: Vector3D) {
        vertex(pos.x, pos.y, pos.z, normal.x, normal.y, normal.z, texcoords.x, texcoords.y)
    }

    fun vertex(
        px: Float,
        py: Float,
        pz: Float,
        nx: Float = 0f,
        ny: Float = 0f,
        nz: Float = 1f,
        u: Float = 0f,
        v: Float = 0f
    ) {
        data.add(px)
        data.add(py)
        data.add(pz)
        data.add(nx)
        data.add(ny)
        data.add(nz)
        data.add(u)
        data.add(v)
    }

    fun faceTriangle(v1: Vector3D, v2: Vector3D, v3: Vector3D) {
        vertex(v1.x, v1.y, v1.z)
        vertex(v2.x, v2.y, v2.z)
        vertex(v3.x, v3.y, v3.z)
    }

    fun faceRectangle(v1: Vector3D, v2: Vector3D, v3: Vector3D, v4: Vector3D) {
        faceTriangle(v1, v2, v3)
        faceTriangle(v3, v4, v1)
    }

    fun pyramidTriangleBase() {
        TODO()
    }

    fun pyramidRectangleBase() {
        TODO()
    }

    fun prismTriangle() {
        TODO()
    }

    fun cuboid(width: Float, height: Float, depth: Float) {
        val hx = width / 2f
        val hy = height / 2f
        val hz = depth / 2f

        // front face, clockwise
        val v1 = Vector3D(-hx, +hy, -hz)
        val v2 = Vector3D(+hx, +hy, -hz)
        val v3 = Vector3D(+hx, -hy, -hz)
        val v4 = Vector3D(-hx, -hy, -hz)

        // back face, clockwise
        val v5 = Vector3D(-hx, +hy, +hz)
        val v6 = Vector3D(+hx, +hy, +hz)
        val v7 = Vector3D(+hx, -hy, +hz)
        val v8 = Vector3D(-hx, -hy, +hz)

        faceRectangle(v1,v2,v3,v4) //front
        faceRectangle(v2,v6,v7,v3) // right
        faceRectangle(v5,v6,v7,v8) // back
        faceRectangle(v1,v4,v8,v5) // left
        faceRectangle(v1,v5,v6,v2) // top
        faceRectangle(v3,v7,v8,v4) // bottom
    }

    fun cube(size: Float = 1f) = cuboid(size, size, size)

    fun build(): Mesh3D = Mesh3D(
        data.toFBuffer(),
        layout,
        null,
        AG.DrawType.TRIANGLES,
        true,
        maxWeights = 0,
        skin = null,
        material = _material
    )
}
