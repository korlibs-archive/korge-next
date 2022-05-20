package com.soywiz.korma.triangle.pathfind

import com.soywiz.korma.geom.IPoint
import com.soywiz.korma.geom.triangle.Edge
import com.soywiz.korma.geom.triangle.Triangle
import com.soywiz.korma.geom.triangle.containsPoint
import com.soywiz.korma.geom.triangle.pointInsideTriangle
import com.soywiz.korma.internal.niceStr
import kotlin.collections.ArrayList
import kotlin.collections.Iterable
import kotlin.collections.LinkedHashMap
import kotlin.collections.arrayListOf
import kotlin.collections.getOrPut
import kotlin.collections.hashMapOf
import kotlin.collections.joinToString
import kotlin.collections.listOf
import kotlin.collections.plusAssign
import kotlin.collections.set
import kotlin.math.hypot

fun Iterable<Triangle>.spatialMesh() = SpatialMesh(this)

class SpatialMesh {
    private var mapTriangleToSpatialNode = hashMapOf<Triangle, Node>()
    var nodes = arrayListOf<Node>()

    @Suppress("ConvertSecondaryConstructorToPrimary")
    constructor(triangles: Iterable<Triangle>) {
        for (triangle in triangles) {
            val node = getNodeFromTriangle(triangle)
            if (node != null) nodes.add(node)
        }

        // Compute neighborhoods
        for (node in nodes) {
            for (edge in node.edges) {
                for (rnode in edge.nodes) {
                    if (rnode !== node) {
                        node.neighbors += rnode
                    }
                }
            }
        }
    }

    fun spatialNodeFromPoint(x: Double, y: Double): Node {
        for (node in nodes) {
            if (node.triangle!!.pointInsideTriangle(x, y)) return node
        }
        throw Error("Point2d not inside triangles")
    }

    fun spatialNodeFromPoint(point: IPoint): Node = spatialNodeFromPoint(point.x, point.y)

    fun getNodeAt(point: IPoint): Node? {
        for (node in nodes) if (node.triangle!!.containsPoint(point)) return node
        return null
    }

    fun getNodeFromTriangle(triangle: Triangle?): Node? {
        if (triangle === null) return null

        if (!mapTriangleToSpatialNode.containsKey(triangle)) {
            val tp0 = triangle.p0
            val tp1 = triangle.p1
            val tp2 = triangle.p2
            val sn = Node(
                x = ((tp0.x + tp1.x + tp2.x) / 3).toInt().toDouble(),
                y = ((tp0.y + tp1.y + tp2.y) / 3).toInt().toDouble(),
                z = 0.0,
                triangle = triangle,
                G = 0,
                H = 0
            )
            mapTriangleToSpatialNode[triangle] = sn
        }
        return mapTriangleToSpatialNode[triangle]
    }

    fun getNodeEdge(p0: IPoint, p1: IPoint): NodeEdge {
        val edge = Edge(p0, p1)
        return nodeEdges.getOrPut(edge) { NodeEdge(edge) }
    }

    private val nodeEdges = LinkedHashMap<Edge, NodeEdge>()
    class NodeEdge(val edge: Edge) {
        val nodes = arrayListOf<Node>()
    }

    inner class Node(
        val x: Double,
        val y: Double,
        val z: Double,
        val triangle: Triangle,
        var G: Int = 0, // Cost
        var H: Int = 0, // Heuristic
        var parent: Node? = null,
        var closed: Boolean = false
    ) {
        val neighbors: ArrayList<Node> = ArrayList()

        // Edges
        val e0 = getNodeEdge(triangle.p0, triangle.p1).also { it.nodes += this }
        val e1 = getNodeEdge(triangle.p1, triangle.p2).also { it.nodes += this }
        val e2 = getNodeEdge(triangle.p2, triangle.p0).also { it.nodes += this }

        val edges = listOf(e0, e1, e2)

        val F: Int get() = G + H // F = G + H

        fun distanceToSpatialNode(that: Node): Int = hypot(this.x - that.x, this.y - that.y).toInt()

        override fun toString(): String = "SpatialNode(${x.niceStr}, ${y.niceStr})"
    }

    override fun toString() = "SpatialMesh(" + nodes.joinToString(",") + ")"
}
