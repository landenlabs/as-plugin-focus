// ----------------------------------------------------------------------
// Copyright (c) 2026 LanDen Labs - Dennis Lang
// https://landenlabs.com
// ----------------------------------------------------------------------
package com.dennislang.focushighlighter

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage

@State(
    name = "FocusHighlighterSettings",
    storages = [Storage("focus-highlighter.xml")]
)
class FocusHighlighterSettings : PersistentStateComponent<FocusHighlighterSettings.State> {

    data class State(
        var enabled: Boolean = true,
        // RGB only (no alpha) — combined with a fixed dark-theme variant at paint time.
        var colorRgb: Int = 0x3D8EFF,
        var thickness: Int = 2,
        // Skip highlighting a component smaller than this on the checked dimension(s).
        var minSize: Int = 100,
        var ignoreDimension: IgnoreDimension = IgnoreDimension.NONE
    )

    enum class IgnoreDimension(val label: String) {
        NONE("Check width & height"),
        WIDTH("Ignore width (check height only)"),
        HEIGHT("Ignore height (check width only)"),
        BOTH("Disable size check")
    }

    private var state = State()

    override fun getState(): State = state

    override fun loadState(state: State) {
        this.state = state
    }

    companion object {
        fun getInstance(): FocusHighlighterSettings =
            ApplicationManager.getApplication().getService(FocusHighlighterSettings::class.java)
    }
}
