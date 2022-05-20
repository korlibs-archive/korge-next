package com.soywiz.korge.tests

import com.soywiz.kds.TGenPriorityQueue
import com.soywiz.klock.DateTime
import com.soywiz.klock.PerformanceCounter
import com.soywiz.klock.TimeProvider
import com.soywiz.klock.TimeSpan
import com.soywiz.klock.milliseconds
import com.soywiz.klock.seconds
import com.soywiz.korag.AG
import com.soywiz.korag.log.DummyAG
import com.soywiz.korag.log.LogAG
import com.soywiz.korag.log.LogBaseAG
import com.soywiz.korev.Key
import com.soywiz.korev.KeyEvent
import com.soywiz.korev.MouseButton
import com.soywiz.korev.MouseEvent
import com.soywiz.korev.dispatch
import com.soywiz.korge.Korge
import com.soywiz.korge.input.MouseEvents
import com.soywiz.korge.input.mouse
import com.soywiz.korge.internal.DefaultViewport
import com.soywiz.korge.render.RenderContext
import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.scene.sceneContainer
import com.soywiz.korge.view.GameWindowLog
import com.soywiz.korge.view.Stage
import com.soywiz.korge.view.View
import com.soywiz.korge.view.ViewsLog
import com.soywiz.korgw.GameWindowCoroutineDispatcher
import com.soywiz.korinject.AsyncInjector
import com.soywiz.korio.async.DEFAULT_SUSPEND_TEST_TIMEOUT
import com.soywiz.korio.async.launchImmediately
import com.soywiz.korio.async.suspendTest
import com.soywiz.korio.async.withTimeout
import com.soywiz.korio.lang.WChar
import com.soywiz.korio.lang.WString
import com.soywiz.korio.lang.forEachCodePoint
import com.soywiz.korio.util.OS
import com.soywiz.korma.geom.Anchor
import com.soywiz.korma.geom.IPoint
import com.soywiz.korma.geom.Rectangle
import com.soywiz.korma.geom.ScaleMode
import com.soywiz.korma.geom.SizeInt
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.delay
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmName

open class ViewsForTesting(
    val frameTime: TimeSpan = 10.milliseconds,
    val windowSize: SizeInt = SizeInt(DefaultViewport.WIDTH, DefaultViewport.HEIGHT),
    val virtualSize: SizeInt = SizeInt(windowSize.size.clone()),
    val defaultDevicePixelRatio: Double = 1.0,
    val log: Boolean = false,
) {
	val startTime = DateTime(0.0)
	var time = startTime
	val elapsed get() = time - startTime
    var devicePixelRatio = defaultDevicePixelRatio

	val timeProvider = object : TimeProvider {
        override fun now(): DateTime = time
    }
	val dispatcher = FastGameWindowCoroutineDispatcher()
    class TestGameWindow(initialSize: SizeInt, val dispatcher: FastGameWindowCoroutineDispatcher) : GameWindowLog() {
        override var width: Int = initialSize.width
        override var height: Int = initialSize.height
        override val coroutineDispatcher = dispatcher
    }
    open fun filterLogDraw(str: String, kind: LogBaseAG.Kind): Boolean {
        return kind != LogBaseAG.Kind.SHADER
    }

	val gameWindow = TestGameWindow(windowSize, dispatcher)
    val ag: AG by lazy { createAg() }

    open fun createAg(): AG {
        return object : LogAG(windowSize.width, windowSize.height) {
            override val devicePixelRatio: Double get() = this@ViewsForTesting.devicePixelRatio
            override fun log(str: String, kind: Kind) {
                if (this@ViewsForTesting.log && filterLogDraw(str, kind)) {
                    super.log(str, kind)
                }
            }
            override fun toString(): String = "ViewsForTesting.LogAG"
        }
    }

	val viewsLog by lazy { ViewsLog(gameWindow, ag = ag, gameWindow = gameWindow, timeProvider = timeProvider).also { viewsLog ->
        viewsLog.views.virtualWidth = virtualSize.width
        viewsLog.views.virtualHeight = virtualSize.height
        viewsLog.views.resized(windowSize.width, windowSize.height)
    } }
	val injector get() = viewsLog.injector
    val logAgOrNull get() = ag as? LogAG?
    val logAg get() = logAgOrNull ?: error("Must call ViewsForTesting(log = true) to access logAg")
    val dummyAg get() = ag as? DummyAG?
	val input get() = viewsLog.input
	val views get() = viewsLog.views
    val stage get() = views.stage
	val stats get() = views.stats
	val mouse: IPoint get() = input.mouse

    fun resizeGameWindow(width: Int, height: Int, scaleMode: ScaleMode = views.scaleMode, scaleAnchor: Anchor = views.scaleAnchor) {
        logAgOrNull?.backWidth = width
        logAgOrNull?.backHeight = height
        dummyAg?.backWidth = width
        dummyAg?.backHeight = height
        gameWindow.width = width
        gameWindow.height = height
        views.scaleAnchor = scaleAnchor
        views.scaleMode = scaleMode
        gameWindow.dispatchReshapeEvent(0, 0, width, height)
    }

    suspend fun <T> deferred(block: suspend (CompletableDeferred<T>) -> Unit): T {
        val deferred = CompletableDeferred<T>()
        block(deferred)
        return deferred.await()
    }

    @JvmName("deferredUnit")
    suspend fun deferred(block: suspend CompletableDeferred<Unit>.() -> Unit) = deferred<Unit>(block)

    suspend inline fun mouseMoveAndClickTo(x: Number, y: Number, button: MouseButton = MouseButton.LEFT) {
        mouseMoveTo(x, y)
        mouseClick(button)
    }

    suspend fun mouseMoveTo(point: IPoint) = mouseMoveTo(point.x, point.y)

    /**
     * x, y in global/virtual coordinates
     */
    suspend fun mouseMoveTo(x: Int, y: Int) {
        val pos = views.globalToWindowMatrix.transform(x, y)
        gameWindow.dispatch(MouseEvent(type = MouseEvent.Type.MOVE, id = 0, x = pos.x.toInt(), y = pos.y.toInt()))
        //views.update(frameTime)
        simulateFrame(count = 2)
    }

    suspend fun mouseMoveTo(x: Number, y: Number) = mouseMoveTo(x.toInt(), y.toInt())

    private var mouseButtons = 0

    suspend fun mouseDown(button: MouseButton = MouseButton.LEFT) {
        mouseEvent(MouseEvent.Type.DOWN, button, false)
		simulateFrame(count = 2)
	}

	suspend fun mouseUp(button: MouseButton = MouseButton.LEFT) {
        mouseEvent(MouseEvent.Type.UP, button, false)
        simulateFrame(count = 2)
	}

    suspend fun mouseClick(button: MouseButton = MouseButton.LEFT) {
        mouseDown(button)
        simulateFrame(count = 2)
        mouseUp(button)
        //mouseEvent(MouseEvent.Type.CLICK, button, false)
        //simulateFrame(count = 2)
    }

    private fun mouseEvent(type: MouseEvent.Type, button: MouseButton, set: Boolean?) {
        mouseButtons = when (set) {
            true -> mouseButtons or (1 shl button.id)
            false -> mouseButtons and (1 shl button.id).inv()
            else -> mouseButtons
        }
        gameWindow.dispatch(
            MouseEvent(
                type = type,
                id = 0,
                x = views.windowMouseX.toInt(),
                y = views.windowMouseY.toInt(),
                button = button,
                buttons = mouseButtons
            )
        )
    }

    suspend fun keyType(chars: WString, shift: Boolean = false, ctrl: Boolean = false) {
        chars.forEachCodePoint { _, codePoint, _ -> keyType(WChar(codePoint), shift, ctrl) }
    }
    suspend fun keyType(chars: String, shift: Boolean = false, ctrl: Boolean = false) {
        chars.forEachCodePoint { _, codePoint, _ -> keyType(WChar(codePoint), shift, ctrl) }
    }

    suspend fun keyType(char: Char, shift: Boolean = false, ctrl: Boolean = false) = keyType(WChar(char.code), shift, ctrl)

    suspend fun keyType(char: WChar, shift: Boolean = false, ctrl: Boolean = false) {
        gameWindow.dispatch(
            KeyEvent(
                type = KeyEvent.Type.TYPE,
                id = 0, key = Key.NONE, keyCode = char.toInt(), character = char.toInt().toChar(),
                shift = shift, ctrl = ctrl, alt = false, meta = false
            )
        )
        simulateFrame(count = 2)
    }

    suspend fun keyDownThenUp(key: Key) {
        keyDown(key)
        keyUp(key)
    }

    suspend fun keyDown(key: Key) {
        keyEvent(KeyEvent.Type.DOWN, key)
        simulateFrame(count = 2)
    }

    suspend fun keyUp(key: Key) {
        keyEvent(KeyEvent.Type.UP, key)
        simulateFrame(count = 2)
    }

    private fun keyEvent(type: KeyEvent.Type, key: Key, keyCode: Int = 0) {
        gameWindow.dispatch(
            KeyEvent(
                type = type,
                id = 0,
                key = key,
                keyCode = keyCode,
                shift = false,
                ctrl = false,
                alt = false,
                meta = false
            )
        )
    }
    val View.viewMouse: MouseEvents get() {
        this.mouse.views = views
        return this.mouse
    }

	suspend fun View.simulateClick() {
        viewMouse.click(viewMouse)
		simulateFrame()
	}

	suspend fun View.simulateOver() {
        viewMouse.over(viewMouse)
		simulateFrame()
	}

	suspend fun View.simulateOut() {
        viewMouse.out(viewMouse)
		simulateFrame()
	}

	suspend fun View.isVisibleToUser(): Boolean {
		if (!this.visible) return false
		if (this.alpha <= 0.0) return false
		val bounds = this.getGlobalBounds()
		if (bounds.area <= 0.0) return false
		val module = injector.get<Module>()
		val visibleBounds = Rectangle(0, 0, module.windowSize.width, module.windowSize.height)
		if (!bounds.intersects(visibleBounds)) return false
		return true
	}

    fun viewsTest(
        timeout: TimeSpan? = DEFAULT_SUSPEND_TEST_TIMEOUT,
        frameTime: TimeSpan = this.frameTime,
        cond: () -> Boolean = { OS.isJvm && !OS.isAndroid },
        //devicePixelRatio: Double = defaultDevicePixelRatio,
        block: suspend Stage.() -> Unit
    ): Unit = suspendTest(timeout = timeout, cond = cond) {
        viewsLog.init()
        this@ViewsForTesting.devicePixelRatio = devicePixelRatio
        //suspendTest(timeout = timeout, cond = { !OS.isAndroid && !OS.isJs && !OS.isNative }) {
        Korge.prepareViewsBase(views, gameWindow, fixedSizeStep = frameTime)

		injector.mapInstance<Module>(object : Module() {
			override val title = "KorgeViewsForTesting"
			override val size = this@ViewsForTesting.windowSize
			override val windowSize = this@ViewsForTesting.windowSize
		})

		var completed = false
		var completedException: Throwable? = null

		this@ViewsForTesting.dispatcher.dispatch(coroutineContext, Runnable {
			launchImmediately(views.coroutineContext + dispatcher) {
				try {
                    block(views.stage)
				} catch (e: Throwable) {
					completedException = e
				} finally {
					completed = true
				}
			}
		})

        //println("[a0]")
		withTimeout(timeout ?: TimeSpan.NIL) {
            //println("[a1]")
			while (!completed) {
                //println("FRAME")
				simulateFrame()
				dispatcher.executePending(1.seconds)
			}

            //println("[a2]")
			if (completedException != null) throw completedException!!
		}
        //println("[a3]")
	}

    @Suppress("UNCHECKED_CAST")
    inline fun <reified S : Scene> sceneTest(
        module: Module? = null,
        crossinline mappingsForTest: AsyncInjector.() -> Unit = {},
        timeout: TimeSpan? = DEFAULT_SUSPEND_TEST_TIMEOUT,
        frameTime: TimeSpan = this.frameTime,
        crossinline block: suspend S.() -> Unit
    ): Unit =
        viewsTest(timeout, frameTime) {
            module?.apply {
                injector.configure()
            }

            injector.mappingsForTest()

            val container = sceneContainer(views)
            container.changeTo<S>()

            with(container.currentScene as S) {
                block()
            }
        }


    private var simulatedFrames = 0
    private var lastDelay = PerformanceCounter.reference
	private suspend fun simulateFrame(count: Int = 1) {
		repeat(count) {
            //println("SIMULATE: $frameTime")
            time += frameTime
            gameWindow.dispatchRenderEvent()
            simulatedFrames++
            val now = PerformanceCounter.reference
            val elapsedSinceLastDelay = now - lastDelay
            if (elapsedSinceLastDelay >= 1.seconds) {
                lastDelay = now
                delay(1)
            }
		}
	}

    suspend fun delayFrame() {
        simulateFrame()
    }

    class TimedTask2(val time: DateTime, val continuation: CancellableContinuation<Unit>?, val callback: Runnable?) {
        var exception: Throwable? = null
        override fun toString(): String = "${time.unixMillisLong}"
    }

    inner class FastGameWindowCoroutineDispatcher : GameWindowCoroutineDispatcher() {
		val hasMore get() = timedTasks2.isNotEmpty() || hasTasks()

		override fun now() = time.unixMillisDouble.milliseconds

        private val timedTasks2 = TGenPriorityQueue<TimedTask2> { a, b -> a.time.compareTo(b.time) }

        override fun invokeOnTimeout(timeMillis: Long, block: Runnable, context: CoroutineContext): DisposableHandle {
            //println("invokeOnTimeout: $timeMillis")
            val task = TimedTask2(time + timeMillis.toDouble().milliseconds, null, block)
            lock { timedTasks2.add(task) }
            return object : DisposableHandle {
                override fun dispose() {
                    lock { timedTasks2.remove(task) }
                }
            }
        }

        override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
            //println("scheduleResumeAfterDelay: $timeMillis")
            val task = TimedTask2(time + timeMillis.toDouble().milliseconds, continuation, null)
            continuation.invokeOnCancellation {
                task.exception = it
            }
            lock { timedTasks2.add(task) }
        }

		override fun toString(): String = "FastGameWindowCoroutineDispatcher"
	}

    inline fun <T : AG> testRenderContext(ag: T, block: (RenderContext) -> Unit): T {
        val ctx = RenderContext(ag, views)
        block(ctx)
        ctx.flush()
        return ag
    }
}
