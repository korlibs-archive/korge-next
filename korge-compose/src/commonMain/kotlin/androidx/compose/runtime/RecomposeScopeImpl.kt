/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.runtime

import androidx.compose.runtime.collection.IdentityArrayIntMap
import androidx.compose.runtime.collection.IdentityArrayMap
import androidx.compose.runtime.collection.IdentityArraySet

/**
 * Represents a recomposable scope or section of the composition hierarchy. Can be used to
 * manually invalidate the scope to schedule it for recomposition.
 */
interface RecomposeScope {
    /**
     * Invalidate the corresponding scope, requesting the composer recompose this scope.
     *
     * This method is thread safe.
     */
    fun invalidate()
}

/**
 * Used by internal tooling to identify a composable in a scope.
 */
@InternalComposeApi
interface InternalRecomposeScope {
    /**
     * A value that identifies a Group independently of movement caused by recompositions.
     */
    val identity: Any?
}

private const val UsedFlag = 0x01
private const val DefaultsInScopeFlag = 0x02
private const val DefaultsInvalidFlag = 0x04
private const val RequiresRecomposeFlag = 0x08
private const val SkippedFlag = 0x10
private const val RereadingFlag = 0x20

/**
 * A RecomposeScope is created for a region of the composition that can be recomposed independently
 * of the rest of the composition. The composer will position the slot table to the location
 * stored in [anchor] and call [block] when recomposition is requested. It is created by
 * [Composer.startRestartGroup] and is used to track how to restart the group.
 */
@OptIn(InternalComposeApi::class)
internal class RecomposeScopeImpl(
    var composition: CompositionImpl?
) : ScopeUpdateScope, RecomposeScope, InternalRecomposeScope {

    private var flags: Int = 0

    /**
     * An anchor to the location in the slot table that start the group associated with this
     * recompose scope.
     */
    var anchor: Anchor? = null

    override val identity: Any?
        get() = anchor

    /**
     * Return whether the scope is valid. A scope becomes invalid when the slots it updates are
     * removed from the slot table. For example, if the scope is in the then clause of an if
     * statement that later becomes false.
     */
    val valid: Boolean get() = composition != null && anchor?.valid ?: false

    val canRecompose: Boolean get() = block != null

    /**
     * Used is set when the [RecomposeScopeImpl] is used by, for example, [currentRecomposeScope].
     * This is used as the result of [Composer.endRestartGroup] and indicates whether the lambda
     * that is stored in [block] will be used.
     */
    var used: Boolean
        get() = flags and UsedFlag != 0
        set(value) {
            if (value) {
                flags = flags or UsedFlag
            } else {
                flags = flags and UsedFlag.inv()
            }
        }

    /**
     * Set to true when the there are function default calculations in the scope. These are
     * treated as a special case to avoid having to create a special scope for them. If these
     * change the this scope needs to be recomposed but the default values can be skipped if they
     * where not invalidated.
     */
    var defaultsInScope: Boolean
        get() = flags and DefaultsInScopeFlag != 0
        set(value) {
            if (value) {
                flags = flags or DefaultsInScopeFlag
            } else {
                flags = flags and DefaultsInScopeFlag.inv()
            }
        }

    /**
     * Tracks whether any of the calculations in the default values were changed. See
     * [defaultsInScope] for details.
     */
    var defaultsInvalid: Boolean
        get() = flags and DefaultsInvalidFlag != 0
        set(value) {
            if (value) {
                flags = flags or DefaultsInvalidFlag
            } else {
                flags = flags and DefaultsInvalidFlag.inv()
            }
        }

    /**
     * Tracks whether the scope was invalidated directly but was recomposed because the caller
     * was recomposed. This ensures that a scope invalidated directly will recompose even if its
     * parameters are the same as the previous recomposition.
     */
    var requiresRecompose: Boolean
        get() = flags and RequiresRecomposeFlag != 0
        set(value) {
            if (value) {
                flags = flags or RequiresRecomposeFlag
            } else {
                flags = flags and RequiresRecomposeFlag.inv()
            }
        }

    /**
     * The lambda to call to restart the scopes composition.
     */
    private var block: ((Composer, Int) -> Unit)? = null

    /**
     * Restart the scope's composition. It is an error if [block] was not updated. The code
     * generated by the compiler ensures that when the recompose scope is used then [block] will
     * be set but it might occur if the compiler is out-of-date (or ahead of the runtime) or
     * incorrect direct calls to [Composer.startRestartGroup] and [Composer.endRestartGroup].
     */
    fun compose(composer: Composer) {
        block?.invoke(composer, 1) ?: error("Invalid restart scope")
    }

    /**
     * Invalidate the group which will cause [composition] to request this scope be recomposed,
     * and an [InvalidationResult] will be returned.
     */
    fun invalidateForResult(value: Any?): InvalidationResult =
        composition?.invalidate(this, value) ?: InvalidationResult.IGNORED

    /**
     * Invalidate the group which will cause [composition] to request this scope be recomposed.
     *
     * Unlike [invalidateForResult], this method is thread safe and calls the thread safe
     * invalidate on the composer.
     */
    override fun invalidate() {
        composition?.invalidate(this, null)
    }

    /**
     * Update [block]. The scope is returned by [Composer.endRestartGroup] when [used] is true
     * and implements [ScopeUpdateScope].
     */
    override fun updateScope(block: (Composer, Int) -> Unit) { this.block = block }

    private var currentToken = 0
    private var trackedInstances: IdentityArrayIntMap? = null
    private var trackedDependencies: IdentityArrayMap<DerivedState<*>, Any?>? = null
    private var rereading: Boolean
        get() = flags and RereadingFlag != 0
        set(value) {
            if (value) {
                flags = flags or RereadingFlag
            } else {
                flags = flags and RereadingFlag.inv()
            }
        }

    /**
     * Indicates whether the scope was skipped (e.g. [scopeSkipped] was called.
     */
    internal var skipped: Boolean
        get() = flags and SkippedFlag != 0
        private set(value) {
            if (value) {
                flags = flags or SkippedFlag
            } else {
                flags = flags and SkippedFlag.inv()
            }
        }

    /**
     * Called when composition start composing into this scope. The [token] is a value that is
     * unique everytime this is called. This is currently the snapshot id but that shouldn't be
     * relied on.
     */
    fun start(token: Int) {
        currentToken = token
        skipped = false
    }

    fun scopeSkipped() {
        skipped = true
    }

    /**
     * Track instances that were read in scope.
     */
    fun recordRead(instance: Any) {
        if (rereading) return
        (trackedInstances ?: IdentityArrayIntMap().also { trackedInstances = it })
            .add(instance, currentToken)
        if (instance is DerivedState<*>) {
            val tracked = trackedDependencies ?: IdentityArrayMap<DerivedState<*>, Any?>().also {
                trackedDependencies = it
            }
            tracked[instance] = instance.currentValue
        }
    }

    /**
     * Returns true if the scope is observing derived state which might make this scope
     * conditionally invalidated.
     */
    val isConditional: Boolean get() = trackedDependencies != null

    /**
     * Determine if the scope should be considered invalid.
     *
     * @param instances The set of objects reported as invalidating this scope.
     */
    fun isInvalidFor(instances: IdentityArraySet<Any>?): Boolean {
        // If a non-empty instances exists and contains only derived state objects with their
        // default values, then the scope should not be considered invalid. Otherwise the scope
        // should if it was invalidated by any other kind of instance.
        if (instances == null) return true
        val trackedDependencies = trackedDependencies ?: return true
        if (
            instances.isNotEmpty() &&
            instances.all { instance ->
                instance is DerivedState<*> && trackedDependencies[instance] == instance.value
            }
        )
            return false
        return true
    }

    fun rereadTrackedInstances() {
        composition?.let { composition ->
            trackedInstances?.let { trackedInstances ->
                rereading = true
                try {
                    trackedInstances.forEach { value, _ ->
                        composition.recordReadOf(value)
                    }
                } finally {
                    rereading = false
                }
            }
        }
    }

    /**
     * Called when composition is completed for this scope. The [token] is the same token passed
     * in the previous call to [start]. If [end] returns a non-null value the lambda returned
     * will be called during [ControlledComposition.applyChanges].
     */
    fun end(token: Int): ((Composition) -> Unit)? {
        return trackedInstances?.let { instances ->
            // If any value previous observed was not read in this current composition
            // schedule the value to be removed from the observe scope and removed from the
            // observations tracked by the composition.
            // [skipped] is true if the scope was skipped. If the scope was skipped we should
            // leave the observations unmodified.
            if (
                !skipped && instances.any { _, instanceToken -> instanceToken != token }
            ) { composition ->
                if (
                    currentToken == token && instances == trackedInstances &&
                    composition is CompositionImpl
                ) {
                    instances.removeValueIf { instance, instanceToken ->
                        (instanceToken != token).also { remove ->
                            if (remove) {
                                composition.removeObservation(instance, this)
                                (instance as? DerivedState<*>)?.let {
                                    trackedDependencies?.let { dependencies ->
                                        dependencies.remove(it)
                                        if (dependencies.size == 0) {
                                            trackedDependencies = null
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (instances.size == 0) trackedInstances = null
                }
            } else null
        }
    }
}
