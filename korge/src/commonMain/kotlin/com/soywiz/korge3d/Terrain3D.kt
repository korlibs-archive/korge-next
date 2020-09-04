package com.soywiz.korge3d

import com.soywiz.korma.geom.Vector3D

interface HeightMap {
    operator fun get(x: Float, z: Float): Float
}

class HeightMapConstant(val height: Float) : HeightMap {
    override fun get(x: Float, z: Float): Float = height
}

@Korge3DExperimental
class Terrain3D : View3D() {

    var maxX: Float = 10f
    var maxZ: Float = 10f
    var stepX = 1f
    var stepZ = 1f

    var heightMap: HeightMap = HeightMapConstant(0f)

    private val meshBuilder3D = MeshBuilder3D()
    private var mesh = meshBuilder3D.build()

    fun calcNormal(x: Float, z: Float) : Vector3D {
        val hl =  heightMap[x-1,z]
        val hr = heightMap[x+1,z]
        val hf = heightMap[x,z+1]
        val hb = heightMap[x,z-1]
        val v = Vector3D(hl-hr, 2f, hb-hf).normalize()
        return v
    }

    fun updateVertices() {
        meshBuilder3D.reset()
        val v1 = Vector3D()
        val v2 = Vector3D()
        val v3 = Vector3D()
        var z = 0f
        while(z < maxZ) {
            var x = 0f
            while(x < maxX) {
                val n = calcNormal(x,z)

                meshBuilder3D.faceTriangle(v1,v2,v3,n.x,n.y,n.z)
                x += stepX
            }
            z+=stepZ
        }
        mesh = meshBuilder3D.build()
    }

    override fun render(ctx: RenderContext3D) {
        val ag = ctx.ag
        val indexBuffer = ag.createIndexBuffer()
        ctx.dynamicVertexBufferPool.alloc { vertexBuffer ->
            vertexBuffer.upload(mesh.vertexBuffer)
            indexBuffer.upload(mesh.indexArray)
        }
    }

}
