package samples

import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.image
import com.soywiz.korge.view.xy
import com.soywiz.korim.atlas.Atlas
import com.soywiz.korim.atlas.AtlasInfo
import com.soywiz.korim.bitmap.sliceWithSize
import com.soywiz.korim.format.readBitmap
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korio.lang.Charsets
import com.soywiz.korio.serialization.xml.Xml

class MainRotatedAtlas : Scene() {
    override suspend fun Container.sceneMain() {
        var atlas = parseAtlas("test.xml")
        var x = 0.0
        for (i in 1..7) {
            image(atlas["$i"]).xy(x, 0.0).also {
                x += it.width
            }
        }

        atlas = parseAtlas("test.xml", true)
        x = 0.0
        for (i in 1..7) {
            image(atlas["$i"]).xy(x, 100.0).also {
                x += it.width
            }
        }
    }

    suspend fun parseAtlas(atlasFile: String, applyRotation: Boolean = false): Atlas {
        val xmlContent = resourcesVfs[atlasFile].readString(Charsets.UTF8)
        val xml = Xml(xmlContent)
        val imagePath = xml.str("imagePath")

        val atlasImg = resourcesVfs[imagePath].readBitmap().sliceWithSize(0, 0, xml.int("width"), xml.int("height"))
        val atlasRegions = mutableListOf<AtlasInfo.Region>()

        xml.children("SubTexture").forEach {
            val name = it.str("name", "")
            if (name.isEmpty()) {
                return@forEach
            }
            val rotated = it.boolean("rotated", false)
            val texFrame = AtlasInfo.Rect(
                it.int("x"),
                it.int("y"),
                it.int("width"),
                it.int("height")
            )
            val trimmed = it.hasAttribute("frameX")
            val imageFrame =
                if (trimmed) AtlasInfo.Rect(
                    it.int("frameX") * -1,
                    it.int("frameY") * -1,
                    it.int("frameWidth"),
                    it.int("frameHeight")
                )
                else texFrame

            val region = AtlasInfo.Region(
                name = name,
                frame = texFrame,
                rotated = rotated,
                sourceSize = AtlasInfo.Size(imageFrame.w, imageFrame.h),
                spriteSourceSize = imageFrame,
                orig = AtlasInfo.Size(imageFrame.w, imageFrame.h),
                trimmed = trimmed
            )

            atlasRegions += if (applyRotation) region.applyRotation() else region
        }

        val atlasInfo = AtlasInfo(
            frames = atlasRegions,
            meta = AtlasInfo.Meta(
                app = "Sparrow/Starling",
                format = "RGBA8888",
                image = imagePath,
                scale = 1.0,
                size = AtlasInfo.Size(xml.int("width"), xml.int("height")),
                version = AtlasInfo.Meta.VERSION
            )
        )
        return Atlas(atlasImg, atlasInfo)
    }
}
