package com.soywiz.korui.react

import com.soywiz.kds.Extra
import com.soywiz.korio.async.Signal
import com.soywiz.korio.async.invoke
import com.soywiz.korui.UiApplication
import com.soywiz.korui.UiComponent
import com.soywiz.korui.UiContainer
import kotlin.collections.LinkedHashMap
import kotlin.collections.MutableMap
import kotlin.collections.getOrPut
import kotlin.collections.set
import kotlin.math.min
import kotlin.native.concurrent.ThreadLocal
import kotlin.reflect.KProperty

@ThreadLocal
var UiComponent.reactUid by Extra.PropertyThis<UiComponent, Any?> { null }

fun UiContainer.react(gen: UiContainerWithReactState.() -> Unit): UiContainer {
    val reactCC = ReactUiMyContainer(this.app, gen)
    this.addChild(reactCC.holder)
    return reactCC.holder
}

class UiContainerWithReactState(val states: ReactUiMyContainer) : UiContainer(states.app) {
    fun <T> state(initial: () -> T): ReactState<T> = ReactState(states, initial)
}

class ReactUiMyContainer(
    val app: UiApplication,
    val gen: UiContainerWithReactState.() -> Unit
) {
    val changed = Signal<Unit>()
    val data = LinkedHashMap<String, Any?>()
    val holder = UiContainerWithReactState(this)
    val holderTemp = UiContainerWithReactState(this)
    var currentUids = ReactUids()

    init {
        holder.removeChildren()
        gen(holder)
        currentUids.generate(holder)

        changed.add {
            //println("CHANGED STATE!")
            holderTemp.removeChildren()
            gen(holderTemp)
            val tempUids = ReactUids()
            tempUids.generate(holderTemp)
            sync(holder, currentUids, holderTemp, tempUids)
        }
    }

    private fun sync(current: UiContainer, cuids: ReactUids, next: UiContainer, nuids: ReactUids) {
        val minSize = min(current.size, next.size)
        for (n in 0 until minSize) {
            val cchild = current[n]
            val nchild = next[n]
            if (cchild.reactUid == nchild.reactUid) {
                cchild.copyFrom(nchild)
                if (cchild is UiContainer) {
                    sync(cchild, cuids, nchild as UiContainer, nuids)
                }
            } else {
                current.replaceChildAt(n, nchild)
            }
        }
        for (n in minSize until next.size) {
            current.addChild(next.getChildAt(n))
        }
    }
}

class ReactUids {
    val uidToComponent = LinkedHashMap<Any?, UiComponent>()
    var lastUid = 0
    fun generate(component: UiComponent): ReactUids {
        //println("GENERATE")
        generateInternal(component)
        return this
    }
    fun generateInternal(component: UiComponent) {
        if (component.reactUid == null) {
            component.reactUid = component::class.simpleName + ":" + (lastUid++)
            //println(" - ${component.reactUid}")
        } else {
            lastUid++
        }
        uidToComponent[component.reactUid] = component
        if (component is UiContainer) {
            component.forEachChild {
                generateInternal(it)
            }
        }
    }
}

class ReactState<T>(val states: ReactUiMyContainer, val initial: () -> T) {
    operator fun getValue(obj: Any?, property: KProperty<*>): T = (states.data as MutableMap<String, T>).getOrPut(property.name) { initial() }
    operator fun setValue(obj: Any?, property: KProperty<*>, value: T) {
        //if (value != getValue(obj, property)) {
        run {
            states.data[property.name] = value
            states.changed()
        }
    }
}
