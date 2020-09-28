package com.soywiz.korge.animate.serialization

import com.soywiz.kds.*
import com.soywiz.kds.iterators.*
import com.soywiz.klock.*
import com.soywiz.kmem.*
import com.soywiz.korau.sound.*
import com.soywiz.korge.animate.*
import com.soywiz.korge.render.*
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.*
import com.soywiz.korim.format.*
import com.soywiz.korio.file.*
import com.soywiz.korio.lang.*
import com.soywiz.korio.serialization.json.*
import com.soywiz.korio.stream.*
import com.soywiz.korma.geom.*
import com.soywiz.korma.geom.vector.*
import kotlin.coroutines.*

suspend fun VfsFile.readAnimation(views: Views): AnLibrary {
    val file = this
    val bytes = this.readBytes()
    views.animateLibraryLoaders.fastForEach { libraryLoader ->
        val library = libraryLoader.invoke(bytes.openFastStream(), views.injector)?.loader?.invoke(file, bytes.openFastStream(), views)
        if (library != null) return library
    }
    error("Unsupported format for $file. Did you miss installing something? Like: views.registerSwfLoading()")
}

suspend fun VfsFile.readAni(context: AnLibrary.Context, content: FastByteArrayInputStream? = null): AnLibrary {
	val file = this
	return AnLibraryDeserializer.read(
        content ?: FastByteArrayInputStream(this.readBytes()),
        context.copy(coroutineContext = coroutineContext),
		externalReaders = AnLibraryDeserializer.ExternalReaders(
			atlasReader = { index ->
				file.withExtension("ani.$index.png").readBitmapOptimized(context.imageFormats)
			},
			readSound = { index ->
				file.withExtension("ani.$index.mp3").readSound()
			}
		))
}

object AnLibraryDeserializer {
	class ExternalReaders(
		val atlasReader: suspend (index: Int) -> Bitmap,
		val readSound: suspend (index: Int) -> Sound
	)

	suspend fun read(s: ByteArray, context: AnLibrary.Context, externalReaders: ExternalReaders): AnLibrary =
		FastByteArrayInputStream(s).readLibrary(context, externalReaders)

	suspend fun read(s: SyncStream, context: AnLibrary.Context, externalReaders: ExternalReaders): AnLibrary =
		FastByteArrayInputStream(s.readAll()).readLibrary(context, externalReaders)

	suspend fun read(s: FastByteArrayInputStream, context: AnLibrary.Context, externalReaders: ExternalReaders): AnLibrary =
		s.readLibrary(context, externalReaders)

	private suspend fun FastByteArrayInputStream.readLibrary(
        context: AnLibrary.Context,
		externalReaders: ExternalReaders
	): AnLibrary {
		val magic = readStringz(8)
		//AnLibrary(views)
		if (magic != AniFile.MAGIC) invalidOp("Not a ${AniFile.MAGIC} file")
		if (readU_VL() != AniFile.VERSION) invalidOp("Just supported ${AniFile.MAGIC} version ${AniFile.VERSION}")
		val msPerFrame = readU_VL()
		val width = readU_VL()
		val height = readU_VL()
		val fileFlags = readU_VL()
		val mipmaps = fileFlags.extract(0)
		val smoothInterpolation = !fileFlags.extract(1)

		val library = AnLibrary(context, width, height, 1000.0 / msPerFrame).apply {
			this.defaultSmoothing = smoothInterpolation
		}

		val strings = arrayOf<String?>(null) + (1 until readU_VL()).map { readStringVL() }

		val atlases = (0 until readU_VL()).map { index ->
			//val format = readU_VL()
			//val width = readU_VL()
			//val height = readU_VL()
			//val size = readU_VL()
			//val data = readBytes(size)
			val bmp = externalReaders.atlasReader(index)
			bmp to bmp.slice()
		}

		val sounds = (0 until readU_VL()).map { index ->
			externalReaders.readSound(index)
		}

		val symbols = (0 until readU_VL()).map {
			readSymbol(strings, atlases, sounds)
		}

		symbols.fastForEach { symbol ->
			library.addSymbol(symbol)
		}
		library.processSymbolNames()

		return library
	}

	private fun FastByteArrayInputStream.readSymbol(
		strings: Array<String?>,
		atlases: List<Pair<Bitmap, BitmapSlice<Bitmap>>>,
		sounds: List<Sound>
	): AnSymbol {
		val symbolId = readU_VL()
		val symbolName = strings[readU_VL()]
		val type = readU_VL()
		val symbol: AnSymbol = when (type) {
			AniFile.SYMBOL_TYPE_EMPTY -> AnSymbolEmpty
			AniFile.SYMBOL_TYPE_SOUND -> {
				val soundId = readU_VL()
				AnSymbolSound(symbolId, symbolName, sounds[soundId], null)
			}
			AniFile.SYMBOL_TYPE_TEXT -> {
				val initialText = strings[readU_VL()]
				val bounds = readRect()
				AnTextFieldSymbol(symbolId, symbolName, initialText ?: "", bounds)
			}
			AniFile.SYMBOL_TYPE_SHAPE -> {
				val scale = readF32LE().toDouble()
				val bitmapId = readU_VL()
				val atlas = atlases[bitmapId]
				val textureBounds = readIRect()
				val bounds = readRect()
				val bitmap = atlas.first
				val texture = atlas.second

				val path: VectorPath? = when (readU_VL()) {
					0 -> null
					1 -> {
						val cmds = IntArray(readU_VL())
						for (n in 0 until cmds.size) cmds[n] = readU8()

						val data = DoubleArray(readU_VL())
						for (n in 0 until data.size) data[n] = readF32LE().toDouble()

						//val cmds = (0 until readU_VL()).map { readU8() }.toIntArray()
						//val data = (0 until readU_VL()).map { readF32LE().toDouble() }.toDoubleArray()
						VectorPath(IntArrayList(*cmds), DoubleArrayList(*data))
					}
					else -> null
				}
				AnSymbolShape(
					id = symbolId,
					name = symbolName,
					bounds = bounds,
					textureWithBitmap = TextureWithBitmapSlice(
						texture = texture.slice(textureBounds),
						bitmapSlice = bitmap.slice(textureBounds),
						scale = scale,
						bounds = bounds
					),
					path = path
				)
			}
			AniFile.SYMBOL_TYPE_MORPH_SHAPE -> {
				val nframes = readU_VL()
				val texturesWithBitmap = Timed<TextureWithBitmapSlice>(nframes)
				for (n in 0 until nframes) {
					val ratio1000 = readU_VL().milliseconds
					val scale = readF32LE().toDouble()
					val bitmapId = readU_VL()
					val bounds = readRect()
					val textureBounds = readIRect()
					val atlas = atlases[bitmapId]
					val bitmap = atlas.first
					val texture = atlas.second

					texturesWithBitmap.add(
						ratio1000, TextureWithBitmapSlice(
							texture = texture.slice(textureBounds),
							bitmapSlice = bitmap.slice(textureBounds),
							scale = scale,
							bounds = bounds
						)
					)
				}
				AnSymbolMorphShape(
					id = symbolId,
					name = symbolName,
					bounds = Rectangle(),
					texturesWithBitmap = texturesWithBitmap,
					path = null
				)
			}
			AniFile.SYMBOL_TYPE_BITMAP -> {
				AnSymbolBitmap(symbolId, symbolName, Bitmap32(1, 1))
			}
			AniFile.SYMBOL_TYPE_MOVIE_CLIP -> {
				readMovieClip(symbolId, symbolName, strings)
			}
			else -> TODO("Type: $type")
		}
		return symbol
	}

	private fun FastByteArrayInputStream.readMovieClip(
		symbolId: Int,
		symbolName: String?,
		strings: Array<String?>
	): AnSymbolMovieClip {
		val mcFlags = readU8()

		val totalDepths = readU_VL()
		val totalFrames = readU_VL()
		val totalTime = readU_VL().milliseconds
		val totalUids = readU_VL()
		val uidsToCharacterIds = (0 until totalUids).map {
			val charId = readU_VL()
			val extraPropsString = readStringVL()
			val extraProps =
				if (extraPropsString.isEmpty()) LinkedHashMap<String, String>() else Json.parse(extraPropsString) as MutableMap<String, String>
			//val extraProps = LinkedHashMap<String, String>()
			AnSymbolUidDef(charId, extraProps)
		}.toTypedArray()
		val mc = AnSymbolMovieClip(symbolId, symbolName, AnSymbolLimits(totalDepths, totalFrames, totalUids, totalTime))

		if (mcFlags.extract(0)) {
			mc.ninePatch = readRect()
		}

		val symbolStates = (0 until readU_VL()).map {
			val ss = AnSymbolMovieClipSubTimeline(totalDepths)
			//ss.name = strings[readU_VL()] ?: ""
			ss.totalTime = readU_VL().microseconds
			val stateFlags = readU8()
			ss.nextStatePlay = stateFlags.extract(0)
			ss.nextState = strings[readU_VL()]

			val numberOfActionFrames = readU_VL()
			var lastFrameTime = 0.milliseconds
			for (n in 0 until numberOfActionFrames) {
				val deltaTime = readU_VL().milliseconds
				val timeInMs = lastFrameTime + deltaTime
				lastFrameTime = timeInMs
				val actions = (0 until readU_VL()).map {
					val action = readU8()
					when (action) {
						0 -> {
							AnPlaySoundAction(readU_VL())
						}
						1 -> {
							AnEventAction(strings[readU_VL()] ?: "")
						}
						else -> TODO()
					}
				}
				actions.fastForEach { action ->
					ss.actions.add(timeInMs * 1000, action)
				}
			}

			for (depth in 0 until totalDepths) {
				val timeline = ss.timelines[depth]
				var lastUid = -1
				var lastName: String? = null
				var lastColorTransform = ColorTransform()
				var lastMatrix = Matrix()
				var lastClipDepth = -1
				var lastRatio = 0.0
				var lastFrameTime = 0.milliseconds
				var lastBlendMode = BlendMode.INHERIT
				for (frameIndex in 0 until readU_VL()) {
					val deltaFrameTime = readU_VL().milliseconds
					val frameTime = lastFrameTime + deltaFrameTime
					lastFrameTime = frameTime
					val flags = readU_VL()
					val hasUid = flags.extract(0)
					val hasName = flags.extract(1)
					val hasColorTransform = flags.extract(2)
					val hasMatrix = flags.extract(3)
					val hasClipDepth = flags.extract(4)
					val hasRatio = flags.extract(5)
					val hasAlpha = flags.extract(6)
					val hasBlendMode = flags.extract(7)

					if (hasUid) lastUid = readU_VL()
					if (hasClipDepth) lastClipDepth = readS16LE()
					if (hasName) lastName = strings[readU_VL()]
					if (hasAlpha) {
						val ct = lastColorTransform.copy()
						ct.mA = readU8().toDouble() / 255.0
						lastColorTransform = ct
					} else if (hasColorTransform) {
						val ct = lastColorTransform.copy()
						val ctFlags = readU8()
						if (ctFlags.extract(0)) ct.mR = readU8().toDouble() / 255.0
						if (ctFlags.extract(1)) ct.mG = readU8().toDouble() / 255.0
						if (ctFlags.extract(2)) ct.mB = readU8().toDouble() / 255.0
						if (ctFlags.extract(3)) ct.mA = readU8().toDouble() / 255.0
						if (ctFlags.extract(4)) ct.aR = readS8() * 2
						if (ctFlags.extract(5)) ct.aG = readS8() * 2
						if (ctFlags.extract(6)) ct.aB = readS8() * 2
						if (ctFlags.extract(7)) ct.aR = readS8() * 2
						//println(ct)
						lastColorTransform = ct
					}
					if (hasMatrix) {
						val lm = lastMatrix.copy()
						val matrixFlags = readU8()
						if (matrixFlags.extract(0)) lm.a = readS_VL().toDouble() / 16384.0
						if (matrixFlags.extract(1)) lm.b = readS_VL().toDouble() / 16384.0
						if (matrixFlags.extract(2)) lm.c = readS_VL().toDouble() / 16384.0
						if (matrixFlags.extract(3)) lm.d = readS_VL().toDouble() / 16384.0
						if (matrixFlags.extract(4)) lm.tx = readS_VL().toDouble() / 20.0
						if (matrixFlags.extract(5)) lm.ty = readS_VL().toDouble() / 20.0
						lastMatrix = lm
					}
					if (hasRatio) lastRatio = readU8().toDouble() / 255.0
					if (hasBlendMode) {
						lastBlendMode = BlendMode.BY_ORDINAL[readU8()] ?: BlendMode.INHERIT
					}
					timeline.add(
						frameTime, AnSymbolTimelineFrame(
							depth = depth,
							uid = lastUid,
							transform = lastMatrix,
							name = lastName,
							colorTransform = lastColorTransform,
							blendMode = lastBlendMode,
							ratio = lastRatio,
							clipDepth = lastClipDepth
						)
					)
				}
			}
			ss
		}

		for (n in 0 until uidsToCharacterIds.size) mc.uidInfo[n] = uidsToCharacterIds[n]
		mc.states += (0 until readU_VL()).map {
			val name = strings[readU_VL()] ?: ""
			val startTime = readU_VL().milliseconds
			val stateIndex = readU_VL()
			symbolStates[stateIndex].actions.add(startTime, AnEventAction(name))
			//println("$startTime, $name")
			name to AnSymbolMovieClipState(name, symbolStates[stateIndex], startTime = startTime)
		}.toMap()

		return mc
	}

	fun FastByteArrayInputStream.readRect() =
		Rectangle(x = readS_VL() / 20.0, y = readS_VL() / 20.0, width = readS_VL() / 20.0, height = readS_VL() / 20.0)

	fun FastByteArrayInputStream.readIRect() =
		RectangleInt(x = readS_VL(), y = readS_VL(), width = readS_VL(), height = readS_VL())
}
