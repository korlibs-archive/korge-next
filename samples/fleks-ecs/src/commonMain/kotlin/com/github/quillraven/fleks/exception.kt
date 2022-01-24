package com.github.quillraven.fleks

import kotlin.reflect.KClass
import kotlin.reflect.KType

abstract class FleksException(message: String) : RuntimeException(message)

class FleksSystemAlreadyAddedException(system: KClass<*>) :
    FleksException("System ${system.simpleName} is already part of the ${WorldConfiguration::class.simpleName}")

class FleksSystemCreationException(injectType: KType) :
    FleksException("Cannot create system. Injection object of type $injectType cannot be found. Did you add all necessary injectables?")

class FleksNoSuchSystemException(system: KClass<*>) :
    FleksException("There is no system of type ${system.simpleName} in the world")

class FleksInjectableAlreadyAddedException(type: KType) :
    FleksException("Injectable with name $type is already part of the ${WorldConfiguration::class.simpleName}")

class FleksInjectableWithoutNameException :
    FleksException("Injectables must be registered with a non-null name")

class FleksMissingNoArgsComponentConstructorException(component: KClass<*>) :
    FleksException("Component ${component.simpleName} is missing a no-args constructor")

class FleksNoSuchComponentException(entity: Entity, component: String) :
    FleksException("Entity $entity has no component of type $component")

class FleksComponentListenerAlreadyAddedException(listener: KClass<out ComponentListener<*>>) :
    FleksException("ComponentListener ${listener.simpleName} is already part of the ${WorldConfiguration::class.simpleName}")

class FleksUnusedInjectablesException(unused: List<KClass<*>>) :
    FleksException("There are unused injectables of following types: ${unused.map { it.simpleName }}")

class FleksReflectionException(type: KType, details: String) :
    FleksException("Cannot create $type.\nDetails: $details")
