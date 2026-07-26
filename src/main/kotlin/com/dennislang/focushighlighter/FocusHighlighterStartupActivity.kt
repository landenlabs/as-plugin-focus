// ----------------------------------------------------------------------
// Copyright (c) 2026 LanDen Labs - Dennis Lang
// https://landenlabs.com
// ----------------------------------------------------------------------
package com.dennislang.focushighlighter

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.event.EditorFactoryEvent
import com.intellij.openapi.editor.event.EditorFactoryListener
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.ex.FocusChangeListener
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.wm.ex.ToolWindowManagerListener
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Wires up two focus sources:
 *  - editor content components: each editor gets its own focus listener,
 *    attached as it's created (there's no app-wide multicaster for focus events).
 *  - tool window content components, via each project's ToolWindowManager
 * Both funnel into [FocusHighlighter], which paints/clears a border on
 * whichever single component currently owns focus.
 */
class FocusHighlighterStartupActivity : ProjectActivity {

    companion object {
        // Editor creation is application-scoped; only register once
        // no matter how many projects are opened during this IDE session.
        private val editorListenerRegistered = AtomicBoolean(false)
    }

    override suspend fun execute(project: Project) {
        registerEditorFocusListenerOnce()
        registerToolWindowListener(project)
    }

    private fun registerEditorFocusListenerOnce() {
        if (!editorListenerRegistered.compareAndSet(false, true)) return
        val focusListener = object : FocusChangeListener {
            override fun focusGained(editor: Editor) {
                FocusHighlighter.highlight(editor.contentComponent)
            }
        }
        EditorFactory.getInstance().addEditorFactoryListener(
            object : EditorFactoryListener {
                override fun editorCreated(event: EditorFactoryEvent) {
                    (event.editor as? EditorEx)?.addFocusListener(focusListener)
                }
            },
            ApplicationManager.getApplication()
        )
    }

    private fun registerToolWindowListener(project: Project) {
        project.messageBus.connect(project).subscribe(
            ToolWindowManagerListener.TOPIC,
            object : ToolWindowManagerListener {
                override fun stateChanged(toolWindowManager: ToolWindowManager) {
                    val activeId = toolWindowManager.activeToolWindowId ?: return
                    val toolWindow = toolWindowManager.getToolWindow(activeId) ?: return
                    FocusHighlighter.highlight(toolWindow.component)
                }
            }
        )
    }
}
