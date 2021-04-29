package com.soywiz.korge.view.fast

import com.soywiz.korim.bitmap.BmpSlice
import com.soywiz.korim.color.Colors
import kotlin.math.cos
import kotlin.math.sin

// @TODO: inline is used because on Kotlin/Native there is a performance problem with EnterFrame/LeaveFrame, so each non-inline function or property call is costly
// @TODO: https://kotlinlang.slack.com/archives/C3SGXARS6/p1619349974244300
open class FastSprite(tex: BmpSlice) {
    var x0: Float = 0f
    var y0: Float = 0f
    var x1: Float = 0f
    var y1: Float = 0f
    var x2: Float = 0f
    var y2: Float = 0f
    var x3: Float = 0f
    var y3: Float = 0f

    var ax: Float = 0f
    var ay: Float = 0f

    // cos(rotation) value
    var cr: Float = 1f

    // sin(rotation) value
    var sr: Float = 0f

    var container: FastSpriteContainer? = null
        internal set

    @PublishedApi internal var dirtyX = true
    @PublishedApi internal var dirtyY = true

    inline fun calcVerticesIfRequired() {
        if (!dirtyX && !dirtyY) return
        if (useRotation) {
            updateXY0123()
        } else {
            if (dirtyX) {
                updateX01()
            }
            if (dirtyY) {
                updateY01()
            }
        }
        dirtyX = false
        dirtyY = false
    }

    @PublishedApi internal var _useRotation: Boolean = false

    inline var useRotation: Boolean
        get() = _useRotation
        set(value) {
            if (_useRotation != value) {
                _useRotation = value
                forceUpdate()
            }
        }

    /**
     * Allows FastSpriteContainer to recalculate a FastSprite if added to a FastSpriteContainer using rotation
     */
    @PublishedApi
    internal inline fun forceUpdate() {
        if (useRotation) {
            cr = cos(rotationRadiansf)
            sr = sin(rotationRadiansf)
        }
        updateSize()
    }

    /**
     * Updates based on rotation
     */
    @PublishedApi
    internal inline fun updateXY0123() {
        // top left
        var px = -ax * scaleXf
        var py = -ay * scaleYf
        x0 = px * cr - py * sr + xf
        y0 = py * cr + px * sr + yf

        // top right
        px = (-ax + width) * scaleXf
        py = -ay * scaleYf
        x1 = px * cr - py * sr + xf
        y1 = py * cr + px * sr + yf


        // bottom right
        px = (-ax + width) * scaleXf
        py = (-ay + height) * scaleYf
        x2 = px * cr - py * sr + xf
        y2 = py * cr + px * sr + yf

        // bottom left
        px = -ax * scaleXf
        py = (-ay + height) * scaleYf
        x3 = px * cr - py * sr + xf
        y3 = py * cr + px * sr + yf
    }

    /**
     * Updates x without rotation
     */
    @PublishedApi
    internal inline fun updateX01() {
        x0 = xf - ax * scaleXf
        x1 = x0 + width * scaleXf
    }

    /**
     * Updates y without rotation
     */
    @PublishedApi internal inline fun updateY01() {
        y0 = yf - ay * scaleYf
        y1 = y0 + height * scaleYf
    }

    @PublishedApi internal inline fun updateXSize() {
        ax = width * anchorXf
        dirtyX = true
    }

    @PublishedApi internal inline fun updateYSize() {
        ay = height * anchorYf
        dirtyY = true
    }


    @PublishedApi internal inline fun updateSize() {
        updateXSize()
        updateYSize()
    }

    @PublishedApi internal var _xf: Float = 0f
    @PublishedApi internal var _yf: Float = 0f
    @PublishedApi internal var _anchorXf: Float = .5f
    @PublishedApi internal var _anchorYf: Float = .5f
    @PublishedApi internal var _scaleXf: Float = 1f
    @PublishedApi internal var _scaleYf: Float = 1f
    @PublishedApi internal var _rotationRadiansf: Float = 0f

    inline var xf: Float
        get() = _xf
        set(value) {
            if (_xf != value) {
                _xf = value
                dirtyX = true
            }
        }
    inline var yf: Float
        get() = _yf
        set(value) {
            if (_yf != value) {
                _yf = value
                dirtyY = true
            }
        }
    inline var anchorXf: Float
        get() = _anchorXf
        set(value) {
            if (_anchorXf != value) {
                _anchorXf = value
                updateXSize()
            }
        }
    inline var anchorYf: Float
        get() = _anchorYf
        set(value) {
            if (_anchorYf != value) {
                _anchorYf = value
                updateYSize()
            }
        }
    inline var scaleXf: Float
        get() = _scaleXf
        set(value) {
            if (_scaleXf != value) {
                _scaleXf = value
                updateXSize()
            }
        }
    inline var scaleYf: Float
        get() = _scaleYf
        set(value) {
            if (_scaleYf != value) {
                _scaleYf = value
                updateYSize()
            }
        }
    inline var rotationRadiansf: Float
        get() = _rotationRadiansf
        set(value) {
            if (_rotationRadiansf != value) {
                _rotationRadiansf = value
                forceUpdate()
            }
        }

    var color = Colors.WHITE
    var visible: Boolean = true

    inline val tx0: Float get() = tex.tl_x
    inline val ty0: Float get() = tex.tl_y
    inline val tx1: Float get() = tex.br_x
    inline val ty1: Float get() = tex.br_y
    inline val width: Float get() = tex.width.toFloat()
    inline val height: Float get() = tex.height.toFloat()

    @PublishedApi internal var _tex: BmpSlice = tex

    inline var tex: BmpSlice
        get() = _tex
        set(value) {
            if (_tex !== value) {
                _tex = value
                updateSize()
            }
        }

    inline fun scale(value: Float) {
        scaleXf = value
        scaleYf = value
    }

    init {
        updateSize()
    }

    override fun toString(): String {
        return "FastSprite(x0=$x0, y0=$y0, x1=$x1, y1=$y1, x2=$x2, y2=$y2, x3=$x3, y3=$y3, ax=$ax, ay=$ay, cr=$cr, sr=$sr, container=$container, useRotation=$useRotation, xf=$xf, yf=$yf, anchorXf=$anchorXf, anchorYf=$anchorYf, scaleXf=$scaleXf, scaleYf=$scaleYf, rotationRadiansf=$rotationRadiansf, color=$color, visible=$visible, tex=$tex)"
    }

}

inline var FastSprite.x: Double
    get() = xf.toDouble()
    set(value) {
        xf = value.toFloat()
    }

inline var FastSprite.y: Double
    get() = yf.toDouble()
    set(value) {
        yf = value.toFloat()
    }

inline var FastSprite.anchorX: Double
    get() = anchorXf.toDouble()
    set(value) {
        anchorXf = value.toFloat()
    }

inline var FastSprite.anchorY: Double
    get() = anchorYf.toDouble()
    set(value) {
        anchorYf = value.toFloat()
    }

inline var FastSprite.scaleX: Double
    get() = scaleXf.toDouble()
    set(value) {
        scaleXf = value.toFloat()
    }
inline var FastSprite.scaleY: Double
    get() = scaleYf.toDouble()
    set(value) {
        scaleYf = value.toFloat()
    }

inline fun FastSprite.scale(value: Double) {
    scale(value.toFloat())
}

inline var FastSprite.rotation: Double
    get() = rotationRadiansf.toDouble()
    set(value) {
        rotationRadiansf = value.toFloat()
    }

inline var FastSprite.alpha
    get() = color.af
    set(value) {
        color = color.withAf(value)
    }
