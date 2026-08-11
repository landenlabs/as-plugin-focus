// ----------------------------------------------------------------------
// Copyright (c) 2026 LanDen Labs - Dennis Lang
// https://landenlabs.com
// ----------------------------------------------------------------------
package com.dennislang.focushighlighter

import com.intellij.ui.JBColor
import com.intellij.util.ui.JBUI
import java.awt.Color
import javax.swing.JComponent

/**
 * Tracks the single component (a tool window's content, or an editor's content
 * component) that currently owns IDE focus, and paints a border on it.
 * App-wide singleton: only one component can hold real focus at a time.
 */
object FocusHighlighter {

    private var highlightedComponent: JComponent? = null

    fun highlight(component: JComponent) {
        val state = FocusHighlighterSettings.getInstance().state
        if (!state.enabled) {
            clear()
            return
        }
        if (isTooSmall(component, state)) {
            clear()
            return
        }
        if (highlightedComponent === component) return

        clear()
        val color = JBColor(Color(state.colorRgb), Color(state.colorRgb))
        component.border = JBUI.Borders.customLine(color, state.thickness)
        highlightedComponent = component
    }

    private fun clear() {
        highlightedComponent?.border = null
        highlightedComponent = null
    }

    private fun isTooSmall(component: JComponent, state: FocusHighlighterSettings.State): Boolean {
        val ignore = state.ignoreDimension
        if (ignore == FocusHighlighterSettings.IgnoreDimension.BOTH) return false
        val checkWidth = ignore != FocusHighlighterSettings.IgnoreDimension.WIDTH
        val checkHeight = ignore != FocusHighlighterSettings.IgnoreDimension.HEIGHT
        return (checkWidth && component.width < state.minSize) ||
               (checkHeight && component.height < state.minSize)
    }
}
