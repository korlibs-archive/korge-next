package com.soywiz.korim.bitmap

import com.soywiz.korma.geom.*
import kotlin.test.*

class BitmapSliceTest {
    @Test
    fun test() {
        val bmp = Bitmap32(64, 64)
        assertEquals("Rectangle(x=0, y=0, width=32, height=32)", bmp.sliceWithSize(0, 0, 32, 32).bounds.toString())
        assertEquals("Rectangle(x=32, y=32, width=32, height=32)", bmp.sliceWithSize(32, 32, 32, 32).bounds.toString())
        assertEquals("Rectangle(x=48, y=48, width=16, height=16)", bmp.sliceWithSize(48, 48, 32, 32).bounds.toString())
        //assertEquals("Rectangle(x=48, y=48, width=-16, height=-16)", bmp.sliceWithBounds(48, 48, 32, 32).bounds.toString()) // Allow invalid bounds
        assertEquals("Rectangle(x=48, y=48, width=0, height=0)", bmp.sliceWithBounds(48, 48, 32, 32).bounds.toString())
        assertEquals("Rectangle(x=24, y=24, width=24, height=24)", bmp.sliceWithSize(16, 16, 32, 32).sliceWithSize(8, 8, 40, 40).bounds.toString())
    }

    @Test
    fun testBmpSize() {
        val slice = Bitmap32(128, 64).sliceWithSize(24, 16, 31, 17)
        assertEquals(
            """
                bmpSize=128,64
                coords=24,16,55,33
                size=31,17
                area=527
                trimmed=false
                frameOffset=0,0,31,17
            """.trimIndent(),
            """
                bmpSize=${slice.bmpWidth},${slice.bmpHeight}
                coords=${slice.left},${slice.top},${slice.right},${slice.bottom}
                size=${slice.width},${slice.height}
                area=${slice.area}
                trimmed=${slice.trimmed}
                frameOffset=${slice.frameOffsetX},${slice.frameOffsetY},${slice.frameWidth},${slice.frameHeight}
            """.trimIndent()
        )
    }

    @Test
    fun testRotate() {
        val bmp = Bitmap32(128, 64).slice()
        val bmp2 = bmp.rotatedRight()
        assertEquals("128x64", bmp.sizeString)
        assertEquals("64x128", bmp2.sizeString)
    }

    /*
    @Test
    fun testTransform() {
        val bmp = Bitmap32(108, 192).slice()
        val affine = Matrix(0.0, 1.0, -1.0, 0.0, 0.0, 0.0)
        val transform = Matrix3D.fromRows(
            0f, 1f, 0f, -1f,
            -1f, 0f, 0f, 1f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f,
        )
        //val coords = UntransformedSizeBmpCoordsWithInstance(bmp.tran∂sformed(transform))
        val coords1 = UntransformedSizeBmpCoordsWithInstance(bmp)
        val coords2 = UntransformedSizeBmpCoordsWithInstance(bmp.transformed(affine))
        val coords3 = UntransformedSizeBmpCoordsWithInstance(bmp.transformed(transform))
        val coords4 = UntransformedSizeBmpCoordsWithInstance(bmp.transformed(affine.toMatrix3D()))
        //val coords5 = UntransformedSizeBmpCoordsWithInstance(bmp.transformed(affine.toMatrix3D()).normalized())
        println("coords1=$coords1")
        println("coords2=$coords2")
        println("coords3=$coords3")
        println("coords4=$coords4")
        //println("coords5=$coords5")
    }
    */

    /*
videoFrame.transform: CGAffineTransform(a: 0.0, b: 1.0, c: -1.0, d: 0.0, tx: 0.0, ty: 0.0)
VIDEO FRAME: UntransformedSizeBmpCoords(width=1080, height=1920, baseCoords=BmpCoordsWithInstance(base=NativeImage(1080, 1920), tl_x=-1.0, tl_y=1.0, tr_x=-1.0, tr_y=0.0, br_x=0.0, br_y=0.0, bl_x=0.0, bl_y=1.0, name=null))
  :: NativeImage(1080, 1920)
  :: Matrix3D(
  [ 0, 1, 0, -1 ],
  [ -1, 0, 0, 1 ],
  [ 0, 0, 1, 0 ],
  [ 0, 0, 0, 1 ],
)
"[PLAYER STATE]paused"
     */
}
