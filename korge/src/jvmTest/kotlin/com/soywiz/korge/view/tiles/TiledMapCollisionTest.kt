package com.soywiz.korge.view.tiles

import com.soywiz.korge.tiled.*
import com.soywiz.korio.async.*
import com.soywiz.korio.file.std.*
import kotlin.test.*

class TiledMapCollisionTest {
    @Test
    fun test() = suspendTest {
        val tiledMap = resourcesVfs["tilecollision/untitled.tmx"].readTiledMap()
        val tiledMapView = TiledMapView(tiledMap)
        assertEquals(TileSetCollisionType.ALL, tiledMapView.pixelHitTest(-16, -16), "outside bounds")
        assertEquals(TileSetCollisionType.NONE, tiledMapView.pixelHitTest(16, 16), "empty tile")
        assertEquals(TileSetCollisionType.ALL, tiledMapView.pixelHitTest(48, 16), "block tile")
    }
}
