import com.soywiz.kds.doubleArrayListOf
import com.soywiz.klock.*
import com.soywiz.klogger.*
import com.soywiz.korev.*
import com.soywiz.korge.annotations.*
import com.soywiz.korge.input.*
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.view.*
import com.soywiz.korge.view.vector.*
import com.soywiz.korgw.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.*
import com.soywiz.korim.font.*
import com.soywiz.korim.format.*
import com.soywiz.korim.paint.*
import com.soywiz.korim.text.*
import com.soywiz.korim.vector.*
import com.soywiz.korim.vector.format.*
import com.soywiz.korio.file.std.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.bezier.StrokePointsMode
import com.soywiz.korma.geom.bezier.toStrokePointsList
import com.soywiz.korma.geom.shape.buildVectorPath
import com.soywiz.korma.geom.vector.*
import com.soywiz.korma.geom.vector.StrokeInfo

suspend fun Stage.mainGpuVectorRendering3() {
    /*
    gpuShapeView({
        keep {
            translate(100, 100)
            fill(Colors.WHITE) {
                rect(-10, -10, 120, 120)
                rectHole(40, 40, 80, 80)
            }
        }
    }) {
        rotation = 5.degrees
        //debugDrawOnlyAntialiasedBorder = true
        keys {
            down(Key.N0) { antialiased = !antialiased }
            down(Key.N1) { debugDrawOnlyAntialiasedBorder = !debugDrawOnlyAntialiasedBorder }
        }
    }
    */

    val strokeInfo = StrokeInfo(thickness = 10.0, join = LineJoin.MITER)
    for (points in buildVectorPath { rect(0, 0, 100, 100) }.toCurves().toStrokePointsList(strokeInfo, generateDebug = true, mode = StrokePointsMode.SCALABLE_POS_NORMAL_WIDTH)) {
        val vector = points.vector
        vector.fastForEachGeneric {
            println("Vector: " + vector.vectorToString(it))
        }
    }

    gpuShapeView({
        keep {
            translate(100, 100)
            stroke(Colors.RED, strokeInfo) {
            //stroke(Colors.RED, lineWidth = 10.0, lineJoin = LineJoin.ROUND) {
            //stroke(Colors.RED, lineWidth = 10.0) {
            //fill(Colors.RED) {
                rect(0, 0, 100, 100)
                //rectHole(40, 40, 80, 80)
            }
        }
    }) {
        //rotation = 5.degrees
        keys {
            down(Key.N0) { antialiased = !antialiased }
        }
    }
}

suspend fun Stage.mainGpuVectorRendering2() {
    val mainStrokePaint = LinearGradientPaint(0, 0, 0, 300).addColorStop(0.0, Colors.GREEN).addColorStop(0.5, Colors.RED).addColorStop(1.0, Colors.BLUE)
    val secondaryStrokePaint = Colors.GREEN.withAd(0.5)

    //circle(128.0, fill = Colors.RED).xy(200, 200).also { it.antialiased = false }
    //roundRect(300, 300, 64, fill = mainStrokePaint).xy(200, 200).also { it.antialiased = true }

    //return

    lateinit var shape: GpuShapeView

    container {
        //xy(0, 0)
        xy(300, 300)
        rotation = 30.degrees
        //val shape = graphics({
        shape = gpuShapeView({
            //val lineWidth = 6.12123231 * 2
            val lineWidth = 12.0
            val width = 300.0
            val height = 300.0
            //rotation = 180.degrees
            this.stroke(mainStrokePaint, lineWidth = lineWidth, lineJoin = LineJoin.MITER, lineCap = LineCap.BUTT) {
            //this.fill(mainStrokePaint) {
                this.rect(lineWidth / 2, lineWidth / 2, width, height)
                this.rect(lineWidth / 2 + 32, lineWidth / 2 + 32, width - 64, height - 64)
            }
            //this.fill(secondaryStrokePaint) {
            //    this.rect(600, 50, 300, 200)
            //}
        }) {
            xy(-150, -150)
            keys {
                down(Key.N0) { antialiased = !antialiased }
                down(Key.A) { antialiased = !antialiased }
            }
        }
        keys {
            downFrame(Key.N1) { rotation = 15.degrees * 0 }
            downFrame(Key.N2) { rotation = 15.degrees * 1 }
            downFrame(Key.N3) { rotation = 15.degrees * 2 }
            downFrame(Key.N4) { rotation = 15.degrees * 3 }
            downFrame(Key.N5) { rotation = 15.degrees * 4 }
            downFrame(Key.N6) { rotation = 15.degrees * 5 }
            downFrame(Key.N7) { rotation = 15.degrees * 6 }
            downFrame(Key.N8) { rotation = 15.degrees * 7 }
            downFrame(Key.N9) { rotation = 180.degrees }
            downFrame(Key.LEFT) { rotation -= 1.degrees }
            downFrame(Key.RIGHT) { rotation += 1.degrees }
            up(Key.Q) { gameWindow.quality = if (gameWindow.quality == GameWindow.Quality.PERFORMANCE) GameWindow.Quality.QUALITY else GameWindow.Quality.PERFORMANCE }
        }
    }

    gamepad {
        connected { println("CONNECTED gamepad=${it}") }
        disconnected { println("DISCONNECTED gamepad=${it}") }
        button { playerId, pressed, button, value ->
            if (pressed && button == GameButton.START) {
                shape.antialiased = !shape.antialiased
            }
            println("BUTTON: $playerId, $pressed, button=$button, value=$value")
        }
        stick { playerId, stick, x, y ->
            if (stick == GameStick.LEFT) {
                rotation += x.degrees
            }
        }
        updatedGamepad {
            shape.rotation += it.ly.degrees
        }
    }
}

@OptIn(KorgeExperimental::class)
suspend fun Stage.mainGpuVectorRendering() {

    //return

    /*
    gpuShapeView({
        //val paint = createLinearGradient(200, 200, 400, 400).add(0.0, Colors.BLUE.withAd(0.9)).add(1.0, Colors.WHITE.withAd(0.7))
        val paint = Colors.WHITE.withAd(0.7)
        //stroke(paint, lineWidth = 10.0, lineCap = LineCap.BUTT, lineJoin = LineJoin.ROUND) {
        //stroke(paint, lineWidth = 10.0, lineCap = LineCap.SQUARE, lineJoin = LineJoin.ROUND) {
        //stroke(paint, lineWidth = 10.0, lineCap = LineCap.ROUND, lineJoin = LineJoin.ROUND) {
        //stroke(paint, lineWidth = 10.0, lineCap = LineCap.ROUND, lineJoin = LineJoin.BEVEL) {
        stroke(paint, lineWidth = 10.0, lineCap = LineCap.ROUND, lineJoin = LineJoin.ROUND) {
        //stroke(paint, lineWidth = 10.0, lineCap = LineCap.ROUND, lineJoin = LineJoin.BEVEL) {
            moveTo(100, 100)
            //quadTo(400, 200, 400, 400)
            lineTo(400, 400)
            lineTo(200, 500)
            lineTo(500, 500)
            lineTo(200, 700)
            //lineTo(100, 140)
            //lineTo(100, 100)
            close()

            moveTo(800, 600)
            //quadTo(400, 200, 400, 400)
            lineTo(900, 600)
            lineTo(900, 400)
            //lineTo(100, 140)
            //lineTo(100, 100)
            close()

            moveTo(800, 100)
            lineTo(800, 110)

            moveTo(750, 100)
            lineTo(750, 110)
        }
    }) {
        keys {
            down(Key.N0) { antialiased = !antialiased }
            down(Key.A) { antialiased = !antialiased }
        }
    }
    */

    //circle(6.0, Colors.RED).anchor(Anchor.CENTER).xy(100, 100)
        //.xy(40, 0)
        //.scale(1.1)
        //.rotation(15.degrees)
    //return

    //return

    Console.log("[1]")
    val korgeBitmap = resourcesVfs["korge.png"].readBitmap()//.mipmaps()
    Console.log("[2]")
    val tigerSvg = measureTime({ resourcesVfs["Ghostscript_Tiger.svg"].readSVG() }) {
        println("Elapsed $it")
    }
    Console.log("[3]")
    //AudioData(44100, AudioSamples(1, 1024)).toSound().play()

    val PAINT_TIGER = true
    val PAINT_SHAPES = true
    val PAINT_BITMAP = true
    //val PAINT_BITMAP = false
    val PAINT_TEXT = true
    val PAINT_LINEAR_GRADIENT = true
    val PAINT_RADIAL_GRADIENT = true

    val tigerShape = tigerSvg.toShape()
    val tigerRender = tigerSvg
    //val tigerRender = tigerShape

    fun Context2d.buildGraphics(kind: String) {
        if (PAINT_TIGER) {
            keep {
                scale(0.5)
                draw(tigerRender)
            }
        }
        if (PAINT_SHAPES) {
            keep {
                translate(100, 200)
                fill(Colors.BLUE) {
                    rect(-10, -10, 120, 120)
                    rectHole(40, 40, 80, 80)
                }
                fill(Colors.YELLOW) {
                    this.circle(100, 100, 40)
                    //rect(-100, -100, 500, 500)
                    //rectHole(40, 40, 320, 320)
                }
                fill(Colors.RED) {
                    regularPolygon(6, 30.0, x = 100.0, y = 100.0)
                    //rect(-100, -100, 500, 500)
                    //rectHole(40, 40, 320, 320)
                }
                stroke(Colors.GREEN, StrokeInfo(thickness = 5.0, startCap = LineCap.ROUND, endCap = LineCap.ROUND, dash = doubleArrayListOf(15.0, 10.0), dashOffset = 8.0)) {
                    regularPolygon(6, 30.0, x = 100.0, y = 100.0)
                }
            }
        }
        keep {
            translate(100, 20)
            scale(2.0)
            if (PAINT_BITMAP) {
                globalAlpha = 0.75
                fillStyle = BitmapPaint(
                    korgeBitmap,
                    Matrix().translate(50, 50).scale(0.125),
                    cycleX = CycleMethod.REPEAT,
                    cycleY = CycleMethod.REPEAT
                )
                fillRect(0.0, 0.0, 100.0, 100.0)
            }

            if (PAINT_LINEAR_GRADIENT) {
                globalAlpha = 0.9
                fillStyle =
                    //createLinearGradient(150.0, 0.0, 200.0, 50.0)
                    createLinearGradient(0.0, 0.0, 100.0, 100.0, transform = Matrix().scale(0.5).pretranslate(300, 0))
                        //.addColorStop(0.0, Colors.BLACK).addColorStop(1.0, Colors.WHITE)
                        .addColorStop(0.0, Colors.RED).addColorStop(0.5, Colors.GREEN).addColorStop(1.0, Colors.BLUE)
                clip({
                    circle(150, 50, 50)
                }, {
                    fillRect(100.0, 0.0, 100.0, 100.0)
                })
            }
            if (PAINT_RADIAL_GRADIENT) {
                globalAlpha = 0.9
                fillStyle =
                    createRadialGradient(150,150,30, 130,180,70)
                        .addColorStop(0.0, Colors.RED).addColorStop(0.5, Colors.GREEN).addColorStop(1.0, Colors.BLUE)
                fillRect(100.0, 100.0, 100.0, 100.0)
            }
            if (PAINT_RADIAL_GRADIENT) {
                globalAlpha = 0.9
                fillStyle =
                    createSweepGradient(175, 100)
                        .addColorStop(0.0, Colors.RED).addColorStop(0.5, Colors.PURPLE).addColorStop(1.0, Colors.YELLOW)
                fillRect(150.0, 75.0, 50.0, 50.0)
            }
        }
        if (PAINT_TEXT) {
            keep {
                font = DefaultTtfFont
                fontSize = 16.0
                fillStyle = Colors.WHITE
                alignment = TextAlignment.TOP_LEFT
                fillText("HELLO WORLD ($kind)", 0.0, 16.0)
            }
        }
    }

    buildShape { buildGraphics("only shape") }
    for (n in 0 until 2) {
        NativeImage(512, 512).context2d { buildGraphics("KOTLIN") }
        Bitmap32(512, 512).context2d { buildGraphics("KOTLIN") }
    }

    measureTime({
        buildShape { buildGraphics("only shape") }
    }) {
        println("BUILD SHAPE: $it")
    }

    val gpuTigger = measureTime({
        gpuShapeView({ buildGraphics("GPU") }) {
            xy(40, 0)
            scale(1.1)
            rotation(15.degrees)
            keys {
                down(Key.N0) { antialiased = !antialiased }
                down(Key.A) { antialiased = !antialiased }
                down(Key.N9) { debugDrawOnlyAntialiasedBorder = !debugDrawOnlyAntialiasedBorder }
            }
        }
    }) {
        println("GPU SHAPE: $it")
    }
    measureTime({
        image(NativeImage(512, 512).context2d { buildGraphics("NATIVE") }).xy(550, 0)
    }) {
        println("CONTEXT2D NATIVE: $it")
    }

    measureTime({
        image(Bitmap32(512, 512).context2d { buildGraphics("KOTLIN") }).xy(550, 370)
    }) {
        println("CONTEXT2D BITMAP: $it")
    }

    gamepad {
        connected { println("CONNECTED gamepad=${it}") }
        disconnected { println("DISCONNECTED gamepad=${it}") }
        button { playerId, pressed, button, value ->
            if (pressed && button == GameButton.START) {
                //shape.antialiased = !shape.antialiased
                gpuTigger.antialiased = !gpuTigger.antialiased
                //println("shape.antialiased=${shape.antialiased}")
            }
            println("BUTTON: $playerId, $pressed, button=$button, value=$value")
        }
        stick { playerId, stick, x, y ->
            println("STICK: $playerId, stick=$stick, x=$x, y=$y")
            if (stick == GameStick.LEFT) {
                rotation += x.degrees
            }
        }
        updatedGamepad {
            //println("updatedGamepad: $it")
            rotation += it.lx.degrees
            //shape.rotation += it.ly.degrees
        }
    }

    uiButton("HELLO").xy(400, 400).scale(4.0)

    //while (true) Bitmap32(512, 512).context2d { buildGraphics("KOTLIN") }
}
