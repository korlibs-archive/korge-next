package com.soywiz.korge.gradle

import com.soywiz.korge.gradle.targets.ios.iosDeployExt
import com.soywiz.korge.gradle.util.get
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class IosDeployTest : AbstractGradleIntegrationTest() {
    @Test
    fun testInstall() = createTempDirectory { tempDir ->
        project.korgeCacheDir = tempDir
        val commandLog = arrayListOf<String>()
        project.defineExecResult("git", "clone", "https://github.com/korlibs/ios-deploy.git", project.korgeCacheDir["ios-deploy"].absolutePath, result = {
            commandLog += "clone"
            File(it.commandLine.last(), ".git").mkdirs()
            TestableExecResult("")
        })
        project.defineExecResult("xcodebuild", "-target", "ios-deploy") {
            commandLog += "xcodebuild"
            it.workingDir["build/Release"].also { it.mkdirs() }["ios-deploy"].writeText("")
            TestableExecResult("")
        }
        assertEquals(project.iosDeployExt.isInstalled, false)
        assertEquals("", commandLog.joinToString(", "))
        run {
            project.iosDeployExt.installIfRequired()
        }
        assertEquals(project.iosDeployExt.isInstalled, true)
        assertEquals("clone, xcodebuild", commandLog.joinToString(", "))
    }
}
