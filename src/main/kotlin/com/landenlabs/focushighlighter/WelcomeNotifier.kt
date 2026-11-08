// ----------------------------------------------------------------------
// Copyright (c) 2026 LanDen Labs - Dennis Lang
// https://landenlabs.com
// ----------------------------------------------------------------------
package com.landenlabs.focushighlighter

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.notification.NotificationAction
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.project.Project

/**
 * Shows a one-time balloon after the plugin is first installed or updated,
 * with an "Open Settings" link to Settings → Tools → All Focus Highlighter.
 */
object WelcomeNotifier {

    const val PLUGIN_ID = "com.landenlabs.focus-highlighter"   // must match <id> in plugin.xml
    private const val GROUP_ID = "All Focus Highlighter"       // must match <notificationGroup id> in plugin.xml

    fun showIfNewVersion(project: Project) {
        val plugin  = PluginManagerCore.getPlugin(PluginId.getId(PLUGIN_ID)) ?: return
        val version = plugin.version ?: return
        val state   = FocusHighlighterSettings.getInstance().state

        // Several projects can open at startup; only the first one claims the notification.
        val previous = synchronized(this) {
            val prev = state.lastNotifiedVersion
            if (prev == version) return
            state.lastNotifiedVersion = version
            prev
        }

        val title = if (previous.isEmpty()) "${plugin.name} installed" else "${plugin.name} updated to $version"
        NotificationGroupManager.getInstance()
            .getNotificationGroup(GROUP_ID)
            .createNotification(
                title,
                "Draws a colored border around the tool window or editor split that has keyboard focus. " +
                    "Settings: Tools → Focus Highlighter Settings…",
                NotificationType.INFORMATION
            )
            .addAction(NotificationAction.createSimpleExpiring("Open Settings") {
                OpenFocusHighlighterSettingsAction.open(project)
            })
            .notify(project)
    }
}
