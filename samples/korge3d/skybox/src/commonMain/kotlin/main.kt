import com.soywiz.korge.Korge
import com.soywiz.korge3d.Korge3DExperimental
import com.soywiz.korge3d.cubeMapFromResourceDirectory
import com.soywiz.korge3d.scene3D
import com.soywiz.korge3d.skyBox

@Korge3DExperimental
suspend fun main() = Korge {

    scene3D {
        skyBox( cubeMapFromResourceDirectory("skybox", "jpg") )
    }

}
