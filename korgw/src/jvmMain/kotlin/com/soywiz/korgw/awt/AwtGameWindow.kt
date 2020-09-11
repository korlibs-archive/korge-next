package com.soywiz.korgw.awt

import com.soywiz.korgw.internal.MicroDynamic
import com.soywiz.korgw.platform.*
import com.soywiz.korim.awt.toAwt
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korio.util.OS
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.Rectangle
import java.awt.*
import java.awt.Toolkit.getDefaultToolkit
import java.awt.event.*
import javax.swing.*


class AwtGameWindow( checkGl: Boolean,  logGl:Boolean) : BaseAwtGameWindow() {
    override val ag: AwtAg = AwtAg(this, checkGl, logGl)

    /*
    fun JFrame.isFullScreen(): Boolean {
        try {
            awtGetPeer()
        } catch (e: Throwable) {
            e.printStackTrace()
            return false
        }
    }
    */

    override var ctx: BaseOpenglContext? = null
    //val frame = Window(Frame("Korgw"))
    val classLoader = this.javaClass.classLoader

    override fun ensureContext() {
        if (ctx == null) {
            ctx = glContextFromComponent(frame)
        }
    }

    //private var currentInFullScreen = false

    val frame: JFrame = object : JFrame("Korgw") {
        val frame = this
        //val frame = object : Frame("Korgw") {
        init {
            isVisible = false
            ignoreRepaint = true
            setBounds(0, 0, 640, 480)
            val frame = this
            frame.setLocationRelativeTo(null)
            //val dim = getDefaultToolkit().screenSize
            //frame.setLocation(dim.width / 2 - frame.size.width / 2, dim.height / 2 - frame.size.height / 2)

            if (OS.isMac) {
                //rootPane.putClientProperty("apple.awt.fullscreenable", true)

                /*
                val listener = Proxy.newProxyInstance(classLoader, arrayOf(Class.forName("com.apple.eawt.FullScreenListener"))) { proxy, method, args ->
                    //println("INVOKED: $proxy, $method")
                    when (method.name) {
                        "windowEnteredFullScreen" -> {
                            currentInFullScreen = true
                        }
                        "windowExitedFullScreen" -> {
                            currentInFullScreen = false
                        }
                    }
                    null
                }
                */

                MicroDynamic {
                    getClass("com.apple.eawt.FullScreenUtilities").invoke("setWindowCanFullScreen", frame, true)
                    //getClass("com.apple.eawt.FullScreenUtilities").invoke("addFullScreenListenerTo", frame, listener)
                }

                // @TODO: This one owns the whole screen in a bad way
                //val env = GraphicsEnvironment.getLocalGraphicsEnvironment()
                //val device = env.defaultScreenDevice
                //device.fullScreenWindow = this
            }
        }

        override fun paintComponents(g: Graphics?) {

        }


        // https://stackoverflow.com/questions/52108178/swing-animation-still-stutter-when-i-use-toolkit-getdefaulttoolkit-sync
        // https://www.oracle.com/java/technologies/painting.html
        // https://docs.oracle.com/javase/tutorial/extra/fullscreen/rendering.html
        // https://docs.oracle.com/javase/tutorial/extra/fullscreen/doublebuf.html
        override fun paint(g: Graphics) {
            framePaint(g)
        }
    }

    override var title: String
        get() = frame.title
        set(value) = run { frame.title = value }
    override var icon: Bitmap? = null
        set(value) {
            field = value
            val awtImage = value?.toAwt()
            if (awtImage != null) {
                kotlin.runCatching {
                    MicroDynamic {
                        getClass("java.awt.Taskbar").invoke("getTaskbar").invoke("setIconImage", awtImage)
                    }
                }
                frame.iconImage = awtImage
            }
        }

    val debugFrame = JFrame("Debug").apply {
        this.defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
        this.setSize(256, 256)
        //focusableWindowState = false
    }

    override val debugComponent: Any? = debugFrame

    private fun synchronizeDebugFrameCoordinates() {
        val frameBounds = RectangleInt(frame.location.x, frame.location.y, frame.size.width, frame.size.height)
        debugFrame.setLocation(frameBounds.right, frameBounds.top)
        debugFrame.setSize(debugFrame.width.coerceAtLeast(64), frameBounds.height)
    }

    init {
        onDebugChanged.add {
            debugFrame.isVisible = it
            synchronizeDebugFrameCoordinates()
            if (debugFrame.isVisible) {
                frame.isVisible = false
                frame.isVisible = true
            }
        }
    }

    override var fullscreen: Boolean
        get() = frame.rootPane.bounds == frame.bounds
        set(value) {
            //println("fullscreen: $fullscreen -> $value")
            if (fullscreen != value) {
                if (OS.isMac) {
                    //println("TOGGLING!")
                    queue {
                        MicroDynamic {
                            //println("INVOKE!: ${getClass("com.apple.eawt.Application").invoke("getApplication")}")
                            getClass("com.apple.eawt.Application").invoke("getApplication").invoke("requestToggleFullScreen", frame)
                        }
                    }
                }
            }
        }

    override fun setSize(width: Int, height: Int) {
        contentComponent.setSize(width, height)
        contentComponent.preferredSize = Dimension(width, height)
        frame.pack()
        val component = this.component
        //val dim = Toolkit.getDefaultToolkit().screenSize
        //component.setLocation(dim.width / 2 - component.size.width / 2, dim.height / 2 - component.size.height / 2)
        if (component is Window) {
            component.setLocationRelativeTo(null)
        }
        //component.setlo
    }

    override val component: Component get() = frame
    override val contentComponent: Component get() = frame.contentPane

    override fun loopInitialization() {
        frame.addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) {
                debugFrame.isVisible = false
                debugFrame.dispose()
                running = false
            }
        })
        frame.addComponentListener(object : ComponentAdapter() {
            override fun componentMoved(e: ComponentEvent?) {
                synchronizeDebugFrameCoordinates()
            }

            override fun componentResized(e: ComponentEvent?) {
                synchronizeDebugFrameCoordinates()
            }
        })
    }

    override fun frameDispose() {
        frame.dispose()
    }
}

