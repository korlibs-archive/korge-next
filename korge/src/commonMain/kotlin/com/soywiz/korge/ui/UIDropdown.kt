package com.soywiz.korge.ui

import com.soywiz.klogger.Logger
import com.soywiz.korge.input.isScrollDown
import com.soywiz.korge.input.isScrollUp
import com.soywiz.korge.input.onClick
import com.soywiz.korge.input.onScroll
import com.soywiz.korge.input.onUpOutside
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.ScalingOption
import com.soywiz.korge.view.SolidRect
import com.soywiz.korge.view.Text
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.alignLeftToLeftOf
import com.soywiz.korge.view.container
import com.soywiz.korge.view.scaleWhileMaintainingAspect
import com.soywiz.korge.view.solidRect
import com.soywiz.korge.view.text
import com.soywiz.korim.color.Colors
import com.soywiz.korim.color.RGBA
import com.soywiz.korio.async.Signal
import com.soywiz.korio.async.launchImmediately
import kotlinx.coroutines.Dispatchers
import kotlin.math.max
import kotlin.math.min

data class DropdownOptionClickedEvent(
    val newOption: UIDropdownOption
)

sealed class UIDropdownOption {
    abstract val text: String

    object Empty : UIDropdownOption() {
        override val text: String
            get() = "NULL"
    }

    data class NumberOption(
        override val text: String,
        val data: Number
    ) : UIDropdownOption()

    data class StringOption(
        override val text: String,
        val data: String
    ) : UIDropdownOption()

    data class AnyOption(
        override val text: String,
        val data: Any
    ) : UIDropdownOption()
}

data class UIDropdownSettings(
    val dropdownWidth: Double = 100.0,
    val entryHeight: Double = 20.0,
    val entryTextPaddingLeft: Double = 5.0,
    val entrySpacing: Double = 5.0,
    val numEntriesVisible: Int = 10
)

inline fun Container.uiDropdown(
    initialOptions: List<UIDropdownOption> = emptyList(),
    settings: UIDropdownSettings = UIDropdownSettings()
): UIDropdown = UIDropdown(initialOptions, settings).addTo(this)

class UIDropdown(
    initialOptions: List<UIDropdownOption> = emptyList(),
    val settings: UIDropdownSettings = UIDropdownSettings(),
) : Container() {

    private fun Container.uiDropDownEntry(
        entryWidth: Double,
        entryHeight: Double,
        textPaddingLeft: Double,
        data: IndexedValue<UIDropdownOption>
    ): UIDropDownEntry = UIDropDownEntry(
        entryWidth, entryHeight, textPaddingLeft,
        data
    ).addTo(this)

    class UIDropDownEntry(
        private val entryWidth: Double,
        private val entryHeight: Double,
        private val textPaddingLeft: Double,
        var data: IndexedValue<UIDropdownOption>
    ) : Container() {
        val entryTextWidth = entryWidth - textPaddingLeft
        val currentDropdownText: Text
        private val dropdownBg: SolidRect = this.solidRect(entryWidth, entryHeight)

        init {
            currentDropdownText = this.text(
                data.value.text, textSize = entryHeight, color = Colors.BLACK,
                autoScaling = true
            ) {
                scaleWhileMaintainingAspect(ScalingOption.ByWidthAndHeight(entryTextWidth, entryHeight))
                alignLeftToLeftOf(dropdownBg, padding = textPaddingLeft)
            }
        }

        fun setColor(color: RGBA) {
            dropdownBg.color = color
        }

        fun setOption(option: IndexedValue<UIDropdownOption>) {
            data = option
            currentDropdownText.text = option.value.text
        }
    }

    private var options = emptyList<IndexedValue<UIDropdownOption>>()
    private val mainEntryDropdown: UIDropDownEntry = this.uiDropDownEntry(
        settings.dropdownWidth,
        settings.entryHeight,
        settings.entryTextPaddingLeft,
        EMPTY_OPTION
    )
    var isOpen = false
    private var currWindowIndex = 0
    private val openedDropdownContainer: Container = this.container()
    private val dropdownEntries = mutableListOf<UIDropDownEntry>()

    private val UNSELECTED_ENTRY_COLOR = Colors.WHITE
    private val SELECTED_COLOR = Colors.YELLOW

    val onDropdownChange = Signal<DropdownOptionClickedEvent>()

    init {
        resetWithOptions(initialOptions)

        mainEntryDropdown.onClick {
            openDropdown()
        }

        onUpOutside {
            closeDropdown()
        }

        openedDropdownContainer.onScroll {
            if (!isOpen) return@onScroll
            if (it.isScrollDown()) {
                incrementWindowIndex()
            }
            if (it.isScrollUp()) {
                decrementWindowIndex()
            }
            updateDropdownEntries()
        }
    }

    inline fun onDropdownChange(noinline handler: suspend (DropdownOptionClickedEvent) -> Unit) {
        onDropdownChange.add {
            launchImmediately(Dispatchers.Default) {
                handler(it)
            }
        }
    }

    // Resets this dropdown with the provided new options.
    fun resetWithOptions(newOptions: List<UIDropdownOption>) {
        currWindowIndex = 0
        logger.info {
            """
                Previous options size: ${options.size}
                new options size: ${newOptions.size}
                newOptions: $newOptions
            """.trimIndent()
        }
        options = newOptions.withIndex().toList()
        mainEntryDropdown.setOption(options.firstOrNull() ?: EMPTY_OPTION)
        dropdownEntries.clear()
        openedDropdownContainer.apply {
            removeChildren()
            for ((i, option) in options.withIndex()) {
                if (i >= settings.numEntriesVisible) break
                val curr = this.uiDropDownEntry(
                    settings.dropdownWidth, settings.entryHeight,
                    settings.entryTextPaddingLeft,
                    option
                )
                curr.onClick {
                    updateMainEntry(curr.data)
                    closeDropdown()
                }
                curr.y += (settings.entryHeight + settings.entrySpacing) * i
                dropdownEntries.add(curr)
            }
        }
        openedDropdownContainer.removeFromParent()
        setSelectedDropdownEntryColorIfExists()
    }

    private fun decrementWindowIndex(delta: Int = 1) {
        currWindowIndex = max(0, currWindowIndex - delta)
    }

    private fun incrementWindowIndex(delta: Int = 1) {
        currWindowIndex = min(
            max(options.size - settings.numEntriesVisible, 0),
            currWindowIndex + delta
        )
    }

    // Sets to the entry matching the text.
    // Returns true if the option was found and applied or false otherwise.
    fun setEntry(text: String): Boolean {
        val option = options.find { it.value.text == text } ?: return false
        updateMainEntry(option)
        return true
    }

    // Updates this dropdown to the next entry
    fun nextEntry() {
        val nextOption = options[min(mainEntryDropdown.data.index + 1, options.size - 1)]
        updateMainEntry(nextOption)
        incrementWindowIndex()
        updateDropdownEntries()
    }

    // Updates this dropdown to the previous entry
    fun previousEntry() {
        val previousOption = options[max(mainEntryDropdown.data.index - 1, 0)]
        updateMainEntry(previousOption)
        decrementWindowIndex()
        updateDropdownEntries()
    }

    fun getCurrentOption(): UIDropdownOption {
        return mainEntryDropdown.data.value
    }

    private fun updateMainEntry(option: IndexedValue<UIDropdownOption>) {
        mainEntryDropdown.setOption(option)
        updateDropdownEntries()
        val event = DropdownOptionClickedEvent(
            option.value
        )
        onDropdownChange(event)
    }

    private fun updateDropdownEntries() {
        for ((i, dropDownEntry) in dropdownEntries.withIndex()) {
            val optionIndex = currWindowIndex + i
            dropDownEntry.apply {
                setOption(options[optionIndex])
            }
        }
        setSelectedDropdownEntryColorIfExists()
    }

    private fun setSelectedDropdownEntryColorIfExists() {
        dropdownEntries.forEach {
            it.setColor(
                if (it.data.index == mainEntryDropdown.data.index)
                    SELECTED_COLOR
                else UNSELECTED_ENTRY_COLOR
            )
        }
    }

    private fun openDropdown() {
        openedDropdownContainer.addTo(this@UIDropdown)
        isOpen = true
    }

    private fun closeDropdown() {
        openedDropdownContainer.removeFromParent()
        isOpen = false
    }

    companion object {
        private val logger = Logger<UIDropdown>()
        private val EMPTY_OPTION = IndexedValue(0, UIDropdownOption.Empty)
    }
}
