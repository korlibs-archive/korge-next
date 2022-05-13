package com.soywiz.korge3d

import com.soywiz.korge.resources.ResourceProcessor
import com.soywiz.korge3d.format.ColladaParser
import com.soywiz.korge3d.format.writeKs3d
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.serialization.xml.readXml

open class ColladaResourceProcessor : ResourceProcessor("dae") {
	companion object : ColladaResourceProcessor()

	override val version: Int = 1
	override val outputExtension: String = "ks3d"

	override suspend fun processInternal(inputFile: VfsFile, outputFile: VfsFile) {
		val library = ColladaParser.parse(inputFile.readXml())
		outputFile.writeKs3d(library)
	}
}
