package com.soywiz.korge.particle

import com.soywiz.kds.*
import com.soywiz.klock.*
import com.soywiz.korag.*
import com.soywiz.korim.bitmap.*
import com.soywiz.korim.color.*
import com.soywiz.korma.geom.*

//e: java.lang.UnsupportedOperationException: Class literal annotation arguments are not yet supported: Factory
//@AsyncFactoryClass(ParticleEmitter.Factory::class)
class ParticleEmitter() {
	enum class Type(val index: Int) {
        GRAVITY(0), RADIAL(1)
    }

    var textureName: String? = null
	var texture: BmpSlice? = null
	var sourcePosition = Point()
	var sourcePositionVariance = Point()
	var speed = 100.0
	var speedVariance = 30.0
	var lifeSpan = 2.0
	var lifespanVariance = 1.9
	var angle: Angle = 270.0.degrees
	var angleVariance: Angle = 360.0.degrees
	var gravity = Point()
	var radialAcceleration = 0.0
	var tangentialAcceleration = 0.0
	var radialAccelVariance = 0.0
	var tangentialAccelVariance = 0.0
	var startColor = RGBAf(1f, 1f, 1f, 1f)
	var startColorVariance = RGBAf(0f, 0f, 0f, 0f)
	var endColor = RGBAf(1f, 1f, 1f, 0f)
	var endColorVariance = RGBAf(0f, 0f, 0f, 0f)
	var maxParticles = 500
	var startSize = 70.0
	var startSizeVariance = 50.0
	var endSize = 10.0
	var endSizeVariance = 5.0
	var duration = -1.0
	var emitterType = Type.GRAVITY
	var maxRadius = 0.0
	var maxRadiusVariance = 0.0
	var minRadius = 0.0
	var minRadiusVariance = 0.0
	var rotatePerSecond = 0.0.degrees
	var rotatePerSecondVariance = 0.0.degrees
    var blendFuncSource = AG.BlendFactor.SOURCE_ALPHA
    var blendFuncDestination = AG.BlendFactor.ONE
	var rotationStart = 0.0.degrees
	var rotationStartVariance = 0.0.degrees
	var rotationEnd = 0.0.degrees
	var rotationEndVariance = 0.0.degrees

	fun create(x: Double = 0.0, y: Double = 0.0, time: TimeSpan = TimeSpan.NIL): ParticleEmitterView =
		ParticleEmitterView(this, IPoint(x, y)).apply {
			this.timeUntilStop = time
		}

    companion object {
        val blendFactorMap = mapOf(
            0 to AG.BlendFactor.ZERO,
            1 to AG.BlendFactor.ONE,
            0x300 to AG.BlendFactor.SOURCE_COLOR,
            0x301 to AG.BlendFactor.ONE_MINUS_SOURCE_COLOR,
            0x302 to AG.BlendFactor.SOURCE_ALPHA,
            0x303 to AG.BlendFactor.ONE_MINUS_SOURCE_ALPHA,
            0x304 to AG.BlendFactor.DESTINATION_ALPHA,
            0x305 to AG.BlendFactor.ONE_MINUS_DESTINATION_ALPHA,
            0x306 to AG.BlendFactor.DESTINATION_COLOR,
            0x307 to AG.BlendFactor.ONE_MINUS_DESTINATION_COLOR,
        )
        val blendFactorMapReversed = blendFactorMap.flip()
        val typeMap = Type.values().associateBy { it.index }
        val typeMapReversed = typeMap.flip()
    }

}
