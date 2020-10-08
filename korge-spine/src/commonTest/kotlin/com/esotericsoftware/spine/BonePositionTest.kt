package com.esotericsoftware.spine

import com.esotericsoftware.spine.Animation.MixBlend
import com.esotericsoftware.spine.Animation.MixDirection
import com.esotericsoftware.spine.attachments.*
import com.soywiz.korio.async.suspendTest
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korio.util.OS
import kotlin.test.Test


class BonePositionTest {
    @Test
    fun test() = suspendTest({ !OS.isJs }) {
        val resource = resourcesVfs["spineboy/spineboy-ess.json"]
        val json = SkeletonJson(AttachmentLoaderTest())
        val skeletonData = json.readSkeletonData(resource)
        val skeleton = Skeleton(skeletonData)
        val bone = skeleton.findBone("gun-tip")
        val fps = 1 / 15f
        for (animation in skeletonData.animations) {
            var time = 0f
            while (time < animation.duration) {
                animation.apply(skeleton, time, time, false, null, 1f, MixBlend.first, MixDirection.`in`)
                skeleton.updateWorldTransform()
                println(animation.name + "," + bone!!.worldX + "," + bone.worldY + "," + bone.worldRotationX)
                time += fps
            }
        }
    }
}

class AttachmentLoaderTest: AttachmentLoader {
    override fun newRegionAttachment(skin: Skin, name: String, path: String): RegionAttachment? {
        return null
    }

    override fun newMeshAttachment(skin: Skin, name: String, path: String): MeshAttachment? {
        return null
    }

    override fun newBoundingBoxAttachment(skin: Skin, name: String): BoundingBoxAttachment? {
        return null
    }

    override fun newClippingAttachment(skin: Skin, name: String): ClippingAttachment? {
        return null
    }

    override fun newPathAttachment(skin: Skin, name: String): PathAttachment? {
        return null
    }

    override fun newPointAttachment(skin: Skin, name: String): PointAttachment? {
        return null
    }
}
