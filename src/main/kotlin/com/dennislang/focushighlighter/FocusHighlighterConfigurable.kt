// ----------------------------------------------------------------------
// Copyright (c) 2026 LanDen Labs - Dennis Lang
// https://landenlabs.com
// ----------------------------------------------------------------------
package com.dennislang.focushighlighter

import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.openapi.extensions.PluginId
import com.intellij.openapi.options.Configurable
import com.intellij.ui.ColorPanel
import com.intellij.ui.HyperlinkLabel
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBLabel
import java.awt.Color
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JSlider

class FocusHighlighterConfigurable : Configurable {

    private lateinit var panel: JPanel
    private lateinit var enabledCheck: JBCheckBox
    private lateinit var colorPanel: ColorPanel
    private lateinit var thicknessSlider: JSlider

    override fun getDisplayName() = "Focus Highlighter"

    override fun createComponent(): JComponent {
        panel = JPanel(GridBagLayout())

        enabledCheck = JBCheckBox("Highlight the focused tool window / editor split")
        colorPanel = ColorPanel()
        thicknessSlider = JSlider(1, 6, 2).apply {
            majorTickSpacing = 1
            paintTicks = true
            paintLabels = true
            snapToTicks = true
        }

        fun row(y: Int, vararg comps: JComponent) {
            comps.forEachIndexed { x, c ->
                panel.add(c, GridBagConstraints().apply {
                    gridx = x; gridy = y
                    insets = Insets(4, 8, 4, 8)
                    anchor = GridBagConstraints.WEST
                    fill = if (c is JSlider) GridBagConstraints.HORIZONTAL else GridBagConstraints.NONE
                    weightx = if (c is JSlider) 1.0 else 0.0
                })
            }
        }

        row(0, enabledCheck)
        row(1, JBLabel("Border color:"), colorPanel)
        row(2, JBLabel("Border thickness (px):"), thicknessSlider)

        val version = PluginManagerCore.getPlugin(PluginId.getId("com.dennislang.focus-highlighter"))?.version
        row(3, JBLabel("Version ${version ?: "unknown"}"))
        val repoLink = HyperlinkLabel("Repo: github.com/dlang").apply {
            setHyperlinkTarget("https://github.com/dlang")
        }
        row(4, repoLink)
        row(5, JBLabel("© 2026 LanDen Labs - Dennis Lang"))

        panel.add(JPanel(), GridBagConstraints().apply {
            gridx = 0; gridy = 6; weighty = 1.0; fill = GridBagConstraints.VERTICAL
        })

        reset()
        return panel
    }

    override fun isModified(): Boolean {
        val s = FocusHighlighterSettings.getInstance().state
        val currentColor = (colorPanel.selectedColor?.rgb ?: s.colorRgb) and 0xFFFFFF
        return enabledCheck.isSelected != s.enabled ||
               currentColor != s.colorRgb ||
               thicknessSlider.value != s.thickness
    }

    override fun apply() {
        val s = FocusHighlighterSettings.getInstance().state
        s.enabled = enabledCheck.isSelected
        colorPanel.selectedColor?.let { s.colorRgb = it.rgb and 0xFFFFFF }
        s.thickness = thicknessSlider.value
    }

    override fun reset() {
        val s = FocusHighlighterSettings.getInstance().state
        enabledCheck.isSelected = s.enabled
        colorPanel.selectedColor = Color(s.colorRgb)
        thicknessSlider.value = s.thickness
    }
}
