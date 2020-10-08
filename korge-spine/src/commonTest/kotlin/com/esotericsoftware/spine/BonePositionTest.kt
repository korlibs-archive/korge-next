package com.esotericsoftware.spine

import com.esotericsoftware.spine.Animation.MixBlend
import com.esotericsoftware.spine.Animation.MixDirection
import com.esotericsoftware.spine.attachments.*
import com.soywiz.korio.async.suspendTest
import com.soywiz.korio.file.std.resourcesVfs
import com.soywiz.korio.util.OS
import kotlin.test.Test
import kotlin.test.assertEquals


class BonePositionTest {
    @Test
    fun test() = suspendTest({ !OS.isJs }) {
        var testResult = resourcesVfs["BonePositionTest.txt"].readString().lines()

        val resource = resourcesVfs["spineboy/spineboy-ess.json"]
        val json = SkeletonJson(object : AttachmentLoader { })
        val skeletonData = json.readSkeletonData(resource)
        val skeleton = Skeleton(skeletonData)
        val bone = skeleton.findBone("gun-tip")
        val fps = 1 / 15f
        var line = 0
        for (animation in skeletonData.animations) {
            var time = 0f
            while (time < animation.duration) {
                animation.apply(skeleton, time, time, false, null, 1f, MixBlend.first, MixDirection.`in`)
                skeleton.updateWorldTransform()
                assertEquals(testResult[line++],"${animation.name},${bone!!.worldX},${bone.worldY},${bone.worldRotationX}")
                println("${animation.name},${bone!!.worldX},${bone.worldY},${bone.worldRotationX}")
                time += fps
            }
        }
    }
}

