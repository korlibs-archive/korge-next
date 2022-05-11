package com.soywiz.korim.atlas

import com.soywiz.kds.*
import com.soywiz.korim.bitmap.BmpCoords
import com.soywiz.korim.bitmap.createBmpCoords
import com.soywiz.korio.dynamic.*
import com.soywiz.korio.serialization.json.*
import com.soywiz.korio.serialization.xml.*
import com.soywiz.korma.geom.*

//e: java.lang.UnsupportedOperationException: Class literal annotation arguments are not yet supported: Factory
//@AsyncFactoryClass(AtlasInfo.Factory::class)
data class AtlasInfo(
    val meta: Meta = Meta(),
    val pages: List<Page> = listOf()
) {
    val frames = pages.flatMap { it.regions }
    val framesMap = frames.associateBy { it.name }

    constructor(
        frames: List<Region>,
        meta: Meta,
    ) : this(
        meta, listOf(
            Page(
                meta.image,
                meta.size,
                meta.format,
                true, true,
                false, false, frames
            )
        )
    )

    data class Rect(val x: Int, val y: Int, val w: Int, val h: Int) {
        val rect get() = Rectangle(x, y, w, h)
    }

    data class Size(val width: Int, val height: Int) {
        val size get() = com.soywiz.korma.geom.Size(width, height)
    }

    data class Meta(
        val app: String = "app",
        val format: String = "format",
        val image: String = "image",
        val scale: Double = 1.0,
        val size: Size = Size(1, 1),
        val version: String = VERSION,
        val frameTags: List<FrameTag> = listOf(),
        val layers: List<Layer> = listOf(),
        val slices: List<Slice> = listOf()
    ) {
        companion object {
            val VERSION = "1.0.0"
        }
    }

    data class FrameTag(
        val name: String = "",
        val from: Int = 0,
        val to: Int = 0,
        val direction: String = ""
    )

    data class Layer(
        val name: String = "",
        val opacity: Int = 255,
        val blendMode: String = ""
    )

    data class Slice(
        val name: String = "slice",
        val color: String = "#0000ffff",
        val keys: List<Key>
    )

    data class Key(
        val frame: Int = 0,
        val bounds: Rect
    )

    data class Page(
        val fileName: String,
        var size: Size,
        var format: String,
        var filterMin: Boolean,
        var filterMag: Boolean,
        var repeatX: Boolean,
        var repeatY: Boolean,
        val regions: List<Region>
    )

    data class Region(
        val name: String,
        val frame: Rect,
        val rotated: Boolean,
        val sourceSize: Size,
        val spriteSourceSize: Rect,
        val trimmed: Boolean,
        val orig: Size = Size(0, 0),
        val offset: Point = Point(),
        val bmpCoords: BmpCoords? = null,
    ) {
        // @TODO: Rename to path or name
        val filename get() = name

        fun applyRotation() = if (rotated) {
            this.copy(
                frame = frame.copy(w = frame.h, h = frame.w),
                spriteSourceSize = spriteSourceSize.copy(
                    x = spriteSourceSize.y,
                    y = spriteSourceSize.x,
                    w = spriteSourceSize.h,
                    h = spriteSourceSize.w
                )
            )
        } else {
            this
        }
    }

    val app: String get() = meta.app
    val format: String get() = meta.format
    val image: String get() = meta.image
    val scale: Double get() = meta.scale
    val size: Size get() = meta.size
    val version: String get() = meta.version

    companion object {
        private fun Any?.toRect() = KDynamic(this) { Rect(it["x"].int, it["y"].int, it["w"].int, it["h"].int) }
        private fun Any?.toSize() = KDynamic(this) { Size(it["w"].int, it["h"].int) }
        private fun KDynamic.createEntry(name: String, it: Any?): Region {
            val rotated = it["rotated"].bool
            return Region(
                name = name,
                frame = it["frame"].toRect(),
                rotated = rotated,
                sourceSize = it["sourceSize"].toSize(),
                spriteSourceSize = it["spriteSourceSize"].toRect(),
                trimmed = it["trimmed"].bool
            )
        }

        // @TODO: kotlinx-serialization?
        fun loadJsonSpriter(json: String): AtlasInfo {
            val info = KDynamic(Json.parse(json)) {
                AtlasInfo(
                    frames = it["frames"].let { frames ->
                        when (frames) {
                            // Hash-based
                            is Map<*, *> -> frames.keys.map { createEntry(it.str, frames[it.str]) }
                            // Array-based
                            else -> frames.list.map {
                                createEntry(
                                    it["name"]?.str ?: it["filename"]?.str ?: "unknown",
                                    it
                                )
                            }
                        }
                    },
                    meta = it["meta"].let {
                        Meta(
                            app = it["app"].str,
                            format = it["format"].str,
                            image = it["image"].str,
                            scale = it["scale"].double,
                            size = it["size"].toSize(),
                            version = it["version"].str,
                            frameTags = it["frameTags"].let { frameTags ->
                                if (frameTags is List<*>) frameTags.list.map {
                                    FrameTag(
                                        name = it["name"].str,
                                        from = it["from"].int,
                                        to = it["to"].int,
                                        direction = it["direction"].str
                                    )
                                }
                                else listOf()
                            },
                            layers = it["layers"].let { layers ->
                                if (layers is List<*>) layers.list.map {
                                    Layer(
                                        name = it["name"].str,
                                        opacity = it["opacity"].int,
                                        blendMode = it["blendMode"].str
                                    )
                                }
                                else listOf()
                            },
                            slices = it["slices"].let { slices ->
                                if (slices is List<*>) slices.list.map {
                                    Slice(
                                        name = it["name"].str,
                                        color = it["color"].str,
                                        keys = it["keys"].let { keys ->
                                            if (keys is List<*>) keys.list.map {
                                                Key(
                                                    frame = it["frame"].int,
                                                    bounds = it["bounds"].toRect()
                                                )
                                            }
                                            else listOf()
                                        }
                                    )
                                }
                                else listOf()
                            }
                        )
                    }
                )
            }
            val w = info.size.width.toFloat()
            val h = info.size.height.toFloat()
            return info.copy(pages = info.pages.map { it.copy(regions = it.regions.map { r ->
                if (r.rotated) {
                    val f = r.frame
                    r.copy(bmpCoords = createBmpCoords(
                        tl_x = (f.x + f.h) / w, tl_y = f.y / h,
                        tr_x = (f.x + f.h) / w, tr_y = (f.y + f.w) / h,
                        br_x = f.x / w, br_y = (f.y + f.w) / h,
                        bl_x = f.x / w, bl_y = f.y / h
                    ),
                        rotated = false
                    )
                } else {
                    r
                }
            }) })
        }

        fun loadXml(content: String): AtlasInfo {
            val xml = Xml(content)
            val imagePath = xml.str("imagePath")
            val size = Size(xml.int("width", -1), xml.int("height", -1))
            val w = size.width.toFloat()
            val h = size.height.toFloat()

            return AtlasInfo(
                (xml.children("SubTexture") + xml.children("sprite")).map {
                    val rotated = it.boolean("rotated", false)
                    val rect = Rect(
                        it.int("x"),
                        it.int("y"),
                        if (rotated) {
                            it.intNull("height") ?: it.int("w")
                        } else {
                            it.intNull("width") ?: it.int("h")
                        },
                        if (rotated) {
                            it.intNull("width") ?: it.int("w")
                        } else {
                            it.intNull("height") ?: it.int("h")
                        }
                    )
                    val offRect = Rect(
                        it.int("frameX") * -1,
                        it.int("frameY") * -1,
                        rect.w,
                        rect.h
                    )
                    Region(
                        name = it.strNull("name") ?: it.str("n"),
                        frame = rect,
                        rotated = false,
                        sourceSize = Size(
                            it.int("frameWidth", rect.w),
                            it.int("frameHeight", rect.h)
                        ),
                        spriteSourceSize = offRect,
                        trimmed = it.hasAttribute("frameX"),
                        bmpCoords = if (rotated) {
                            createBmpCoords(
                                tl_x = (rect.x + rect.h) / w, tl_y = rect.y / h,
                                tr_x = (rect.x + rect.h) / w, tr_y = (rect.y + rect.w) / h,
                                br_x = rect.x / w, br_y = (rect.y + rect.w) / h,
                                bl_x = rect.x / w, bl_y = rect.y / h
                           )
                        } else {
                            null
                        }
                    )
                }, Meta(
                    app = "Unknown",
                    format = "xml",
                    image = imagePath,
                    scale = 1.0,
                    size = size,
                    version = "1.0"
                )
            )
        }

        fun loadText(content: String): AtlasInfo {
            val r = ListReader(content.lines())
            var pageImage: Any? = null

            fun String.point(): Point {
                val list = this.split(',', limit = 2)
                return Point(list.first().trim().toInt(), list.last().trim().toInt())
            }

            fun String.size(): Size = point().let { Size(it.x.toInt(), it.y.toInt()) }

            fun String.keyValue(): Pair<String, String> {
                val list = this.split(':', limit = 2)
                return list.first().trim().toLowerCase() to list.last().trim()
            }

            fun String.filter(): Boolean {
                return when (this.toLowerCase()) {
                    "nearest" -> false
                    "linear" -> true
                    "mipmap" -> true
                    "mipmapnearestnearest" -> false
                    "mipmaplinearnearest" -> false
                    "mipmapnearestlinear" -> false
                    "mipmaplinearlinear" -> false
                    else -> false
                }
            }

            var currentEntryList = arrayListOf<Region>()
            val pages = arrayListOf<Page>()

            var w = 1f
            var h = 1f
            while (r.hasMore) {
                val line = r.read().trim()
                if (line.isEmpty()) {
                    if (r.eof) break

                    val fileName = r.read().trim()
                    var size = Size(0, 0)
                    var format = "rgba8888"
                    var filterMin = false
                    var filterMag = false
                    var repeatX = false
                    var repeatY = false
                    w = 1f
                    h = 1f
                    while (r.hasMore && r.peek().contains(':')) {
                        val (key, value) = r.read().trim().keyValue()
                        when (key) {
                            "size" -> {
                                size = value.size()
                                w = size.width.toFloat()
                                h = size.height.toFloat()
                            }
                            "format" -> format = value
                            "filter" -> {
                                val filter = value.split(",").map { it.trim().toLowerCase() }
                                filterMin = filter.first().filter()
                                filterMag = filter.last().filter()
                            }
                            "repeat" -> {
                                repeatX = value.contains('x')
                                repeatY = value.contains('y')
                            }
                        }
                    }
                    currentEntryList = arrayListOf<Region>()
                    pages.add(Page(fileName, size, format, filterMin, filterMag, repeatX, repeatY, currentEntryList))
                } else {
                    val name = line
                    var rotate = false
                    var xy = Point()
                    var size = Size(0, 0)
                    var orig = Size(0, 0)
                    var offset = Point()
                    while (r.hasMore && r.peek().contains(':')) {
                        val (key, value) = r.read().trim().keyValue()
                        when (key) {
                            "rotate" -> rotate = value.toBoolean()
                            "xy" -> xy = value.point()
                            "size" -> size = value.size()
                            "orig" -> orig = value.size()
                            "offset" -> offset = value.point()
                        }
                    }
                    val rect = Rect(xy.x.toInt(), xy.y.toInt(), size.width, size.height)
                    val spriteSourceSize = Rect(offset.x.toInt(), offset.y.toInt(), size.width, size.height)

                    currentEntryList.add(Region(name, rect, false, orig, spriteSourceSize, orig != size || (offset.x != 0.0 && offset.y != 0.0), orig, offset,
                        bmpCoords = if (rotate) {
                            createBmpCoords(
                                tl_x = rect.x / w, tl_y = (rect.y + rect.w) / h,
                                tr_x = rect.x / w, tr_y = rect.y / h,
                                br_x = (rect.x + rect.h) / w, br_y = rect.y / h,
                                bl_x = (rect.x + rect.h) / w, bl_y = (rect.y + rect.w) / h
                            )
                        } else {
                            null
                        }
                    ))
                }
            }
            val firstPage = pages.first()
            return AtlasInfo(Meta("unknown", firstPage.format, firstPage.fileName, 1.0, firstPage.size, "1.0"), pages)
        }
    }
}
