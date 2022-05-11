import com.soywiz.korge.GLCanvasKorge
import com.soywiz.korge.GLCanvasWithKorge
import com.soywiz.korge.Korge
import com.soywiz.korge.input.onClick
import com.soywiz.korge.jvmEnsureAddOpens
import com.soywiz.korge.ui.UIButton
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiVerticalStack
import com.soywiz.korge.view.descendantsWith
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korma.geom.Anchor
import com.soywiz.korma.geom.SizeInt
import java.awt.Component
import java.awt.GridLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JFrame
import javax.swing.SwingUtilities
import kotlin.coroutines.EmptyCoroutineContext

object AwtSandboxSample {
    @JvmStatic
    fun main(args: Array<String>) {
        jvmEnsureAddOpens()

        val frame = JFrame()
        frame.isVisible = false
        frame.ignoreRepaint = true
        //background = Color.black
        frame.setBounds(0, 0, 640, 480)
        frame.setLocationRelativeTo(null)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.isVisible = true

        val components = arrayListOf<Component>()

        for (n in 0 until 9) {
            if (n == 4) {
                components.add(frame.add(GLCanvasWithKorge(Korge.Config(virtualSize = SizeInt(128, 128), scaleAnchor = Anchor.TOP_LEFT)) {
                    val jbutton0 = components.first() as JButton
                    uiVerticalStack {
                        uiButton("${views.devicePixelRatio}")
                        uiButton("HELLO") {
                            name = "helloButton"
                            onClick {
                                SwingUtilities.invokeLater {
                                    jbutton0.text = "hello!"
                                }
                            }
                        }
                        uiButton("WORLD")
                    }
                }))
            } else {
                components.add(frame.add(JButton("$n").also {
                    it.addActionListener {
                        val canvas = components.filterIsInstance<GLCanvasWithKorge>().first()
                        canvas.korge.launchInContext {
                            (stage.findViewByName("helloButton") as UIButton).text = "YAY! $n"
                        }
                    }
                }))
            }
        }
        frame.layout = GridLayout(3, 3, 0, 0)
        frame.validate()
    }
}
