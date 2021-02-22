package com.soywiz.korge.view

import com.soywiz.kds.*
import com.soywiz.klock.*
import com.soywiz.kmem.umod
import com.soywiz.korim.atlas.*
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.bitmap.BmpSlice
import com.soywiz.korim.bitmap.sliceWithSize

class SpriteAnimation constructor(
    val sprites: List<BmpSlice>,
    val regionDetails: List<AtlasInfo.Region>,
    val defaultTimePerFrame: TimeSpan = TimeSpan.NIL
) {
    companion object {
        operator fun invoke(
            spriteMap: Bitmap,
            spriteWidth: Int = 16,
            spriteHeight: Int = 16,
            marginTop: Int = 0,
            marginLeft: Int = 0,
            columns: Int = 1,
            rows: Int = 1,
            offsetBetweenColumns: Int = 0,
            offsetBetweenRows: Int = 0
        ): SpriteAnimation {
            return SpriteAnimation(
                FastArrayList<BmpSlice>().apply {
                    for (row in 0 until rows) {
                        for (col in 0 until columns) {
                            add(
                                spriteMap.sliceWithSize(
                                    marginLeft + (spriteWidth + offsetBetweenColumns) * col,
                                    marginTop + (spriteHeight + offsetBetweenRows) * row,
                                    spriteWidth,
                                    spriteHeight,
                                    name = "slice$size"
                                )
                            )
                        }
                    }
                },
                FastArrayList()
            )
        }
    }

    val spriteStackSize: Int get() = sprites.size
    val size: Int get() = sprites.size
    val firstSprite: BmpSlice get() = sprites[0]
    fun getSprite(index: Int): BmpSlice = sprites[index umod sprites.size]
    fun getRegionDetail(index: Int): AtlasInfo.Region? {
        return when {
            regionDetails.isEmpty() -> null
            else -> regionDetails[index umod regionDetails.size]
        }
    }

    operator fun get(index: Int) = getSprite(index)
}

fun Atlas.getSpriteAnimation(prefix: String = "", defaultTimePerFrame: TimeSpan = TimeSpan.NIL): SpriteAnimation {
    val entries = this.entries.filter { it.filename.startsWith(prefix) }
    return SpriteAnimation(
        entries.map { it.slice }.toFastList(),
        entries.map { it.info }.toFastList(),
        defaultTimePerFrame
    )
}

fun Atlas.getSpriteAnimation(regex: Regex, defaultTimePerFrame: TimeSpan = TimeSpan.NIL): SpriteAnimation {
    val entries = this.entries.filter { regex.matches(it.filename) }
    return SpriteAnimation(
        entries.map { it.slice }.toFastList(),
        entries.map { it.info }.toFastList(),
        defaultTimePerFrame
    )
}
