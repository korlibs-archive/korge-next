package com.soywiz.korlibs.plugin.gradle

import org.gradle.api.*
import kotlin.reflect.*

val org.gradle.api.Project.`kotlin`: org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension get() =
    (this as org.gradle.api.plugins.ExtensionAware).extensions.getByName("kotlin") as org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun <T : Any, U : NamedDomainObjectCollection<T>> U.getting(configuration: T.() -> Unit) =
    NamedDomainObjectCollectionDelegateProvider.of(this, configuration)

fun <T : Any> NamedDomainObjectContainer<T>.creating(configuration: T.() -> Unit) =
    NamedDomainObjectContainerCreatingDelegateProvider.of(this, configuration)

class ExistingDomainObjectDelegate<T>
private constructor(
    internal val delegate: T
) {
    companion object {
        fun <T> of(delegate: T) =
            ExistingDomainObjectDelegate(delegate)
    }
}

class NamedDomainObjectContainerCreatingDelegateProvider<T : Any>
private constructor(
    internal val container: NamedDomainObjectContainer<T>,
    internal val configuration: (T.() -> Unit)? = null
) {
    companion object {
        fun <T : Any> of(container: NamedDomainObjectContainer<T>, configuration: (T.() -> Unit)? = null) =
            NamedDomainObjectContainerCreatingDelegateProvider(container, configuration)
    }


    operator fun getValue(thisRef: Nothing?, property: kotlin.reflect.KProperty<*>): T {
        //return provideDelegate(thisRef, property)
        return container.create(property.name) as T
    }

    //operator fun provideDelegate(thisRef: Any?, property: KProperty<*>) = ExistingDomainObjectDelegate.of(
    //    when (configuration) {
    //        null -> container.create(property.name)
    //        else -> container.create(property.name, configuration)
    //    }
    //)
}

class NamedDomainObjectCollectionDelegateProvider<T>
private constructor(
    internal val collection: NamedDomainObjectCollection<T>,
    internal val configuration: (T.() -> Unit)?
) {
    companion object {
        fun <T> of(
            collection: NamedDomainObjectCollection<T>,
            configuration: (T.() -> Unit)? = null
        ) =
            NamedDomainObjectCollectionDelegateProvider(collection, configuration)
    }

    operator fun getValue(thisRef: Nothing?, property: kotlin.reflect.KProperty<*>): T {
        //return provideDelegate(thisRef, property)
        return collection.getByName(property.name)
    }

    //operator fun provideDelegate(thisRef: Any?, property: kotlin.reflect.KProperty<*>) = ExistingDomainObjectDelegate.of(
    //    when (configuration) {
    //        null -> collection.getByName(property.name)
    //        else -> collection.getByName(property.name, configuration)
    //    }
    //)
}
