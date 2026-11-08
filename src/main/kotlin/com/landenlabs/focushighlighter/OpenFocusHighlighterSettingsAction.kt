// ----------------------------------------------------------------------
// Copyright (c) 2026 LanDen Labs - Dennis Lang
// https://landenlabs.com
// ----------------------------------------------------------------------
package com.landenlabs.focushighlighter

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project

/**
 * Tools → Focus Highlighter Settings… — jumps straight to Settings → Tools → All Focus Highlighter.
 * Also reachable from Find Action (⌘⇧A / Ctrl+Shift+A) by typing "Focus Highlighter".
 */
class OpenFocusHighlighterSettingsAction : AnAction(), DumbAware {

    override fun actionPerformed(e: AnActionEvent) = open(e.project)

    companion object {
        fun open(project: Project?) {
            ShowSettingsUtil.getInstance().showSettingsDialog(project, FocusHighlighterConfigurable::class.java)
        }
    }
}
