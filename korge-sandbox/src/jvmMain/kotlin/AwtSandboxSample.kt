import com.soywiz.korge.GLCanvasWithKorge
import com.soywiz.korge.Korge
import com.soywiz.korge.jvmEnsureAddOpens
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiVerticalStack
import com.soywiz.korma.geom.Anchor
import com.soywiz.korma.geom.SizeInt
import java.awt.GridLayout
import javax.swing.JButton
import javax.swing.JFrame

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
        for (n in 0 until 9) {
            if (n == 4) {
                frame.add(GLCanvasWithKorge(Korge.Config(virtualSize = SizeInt(128, 128), scaleAnchor = Anchor.TOP_LEFT)) {
                    uiVerticalStack {
                        uiButton("${views.devicePixelRatio}")
                        uiButton("HELLO")
                        uiButton("WORLD")
                    }
                })
            } else {
                frame.add(JButton("$n"))
            }
        }
        frame.layout = GridLayout(3, 3, 0, 0)
        frame.validate()
    }
}
