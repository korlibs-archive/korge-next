package com.soywiz.korte

import com.soywiz.korte.internal.*
import com.soywiz.korte.util.*

open class Templates(
    var root: NewTemplateProvider,
    var includes: NewTemplateProvider = root,
    var layouts: NewTemplateProvider = root,
    val config: TemplateConfig = TemplateConfig(),
    var cache: Boolean = true
) {
    @PublishedApi
    internal val tcache = AsyncCache()

    fun invalidateCache() {
        tcache.invalidateAll()
    }

    @PublishedApi
    internal suspend fun cache(name: String, callback: suspend () -> Template): Template = when {
		// @TODO: Kotlin 1.4-M3 regression bug
        //cache -> tcache(name) { callback() }
		cache -> tcache.call(name) { callback() }
        else -> callback()
    }

	// @TODO: Kotlin 1.4-M3 regression bug
	//> Task :korte:compileKotlinJvm FAILED
	//e: /home/soywiz/projects/korlibs/korge-next/korte/src/commonMain/kotlin/com/soywiz/korte/Templates.kt: (37, 4): Suspend function 'invoke' should be called only from a coroutine or another suspend function
	internal class Demo() {
		suspend operator fun <T> invoke(name: String, block: suspend () -> T): T {
			TODO()
		}
	}

	suspend fun demo(callback: suspend () -> Unit) = when {
		true -> {
			val demo = Demo()
			demo("test") { callback() }
		}
		else -> TODO()
	}

    open suspend fun getInclude(name: String): Template = cache("include/$name") {
        Template(name, this@Templates, includes.newGetSure(name), config).init()
    }

    open suspend fun getLayout(name: String): Template = cache("layout/$name") {
        Template(name, this@Templates, layouts.newGetSure(name), config).init()
    }

    open suspend fun get(name: String): Template = cache("base/$name") {
        Template(name, this@Templates, root.newGetSure(name), config).init()
    }

    suspend fun render(name: String, vararg args: Pair<String, Any?>): String = get(name).invoke(*args)
    suspend fun render(name: String, args: Any?): String {
        val template = get(name)
        val renderered = template(args)
        return renderered
    }
    suspend fun prender(name: String, vararg args: Pair<String, Any?>): AsyncTextWriterContainer =
        get(name).prender(*args)

    suspend fun prender(name: String, args: Any?): AsyncTextWriterContainer = get(name).prender(args)
}
