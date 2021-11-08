package com.soywiz.kdynlib

import org.gradle.api.*
import org.gradle.api.provider.*
import org.jetbrains.kotlin.gradle.plugin.*

@Suppress("unused")
class KDynLibGradlePlugin : KotlinCompilerPluginSupportPlugin {
    override fun apply(target: Project) {
        //println("KDynLibGradlePlugin.apply")
    }

    override fun getCompilerPluginId(): String {
        //println("KDynLibGradlePlugin.getCompilerPluginId")
        return "com.soywiz.kdynlib"
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        //println("KDynLibGradlePlugin.isApplicable: $kotlinCompilation")
        return true
        //return kotlinCompilation.platformType == KotlinPlatformType.js
    }

    override fun getPluginArtifact(): SubpluginArtifact {
        //println("KDynLibGradlePlugin.getPluginArtifact")
        return SubpluginArtifact(
            groupId = "com.soywiz.korlibs.kdynlib.irplugin",
            artifactId = "kdynlib-gradle-plugin",
            version = "2.0.0.999" // @TODO: Fix this
        )
    }

    override fun getPluginArtifactForNative(): SubpluginArtifact = getPluginArtifact()

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        //println("KDynLibGradlePlugin.applyToCompilation")
        kotlinCompilation.dependencies {
        }
        val project = kotlinCompilation.target.project
        return project.provider { listOf(SubpluginOption("targetName", kotlinCompilation.target.name)) }
    }
}
