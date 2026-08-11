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
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JSlider
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel

class FocusHighlighterConfigurable : Configurable {

    private lateinit var panel: JPanel
    private lateinit var enabledCheck: JBCheckBox
    private lateinit var colorPanel: ColorPanel
    private lateinit var thicknessSlider: JSlider
    private lateinit var minSizeSpinner: JSpinner
    private lateinit var ignoreDimensionCombo: JComboBox<FocusHighlighterSettings.IgnoreDimension>

    override fun getDisplayName() = "All Focus Highlighter"

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

        minSizeSpinner = JSpinner(SpinnerNumberModel(100, 0, 2000, 10))
        ignoreDimensionCombo = JComboBox(FocusHighlighterSettings.IgnoreDimension.values())
        ignoreDimensionCombo.renderer = object : javax.swing.DefaultListCellRenderer() {
            override fun getListCellRendererComponent(
                list: javax.swing.JList<*>?, value: Any?, index: Int,
                isSelected: Boolean, cellHasFocus: Boolean
            ): java.awt.Component {
                val text = (value as? FocusHighlighterSettings.IgnoreDimension)?.label ?: value?.toString()
                return super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus)
            }
        }

        row(0, enabledCheck)
        row(1, JBLabel("Border color:"), colorPanel)
        row(2, JBLabel("Border thickness (px):"), thicknessSlider)
        row(3, JBLabel("Minimum focus area size (px):"), minSizeSpinner)
        row(4, JBLabel("Size check:"), ignoreDimensionCombo)

        val version = PluginManagerCore.getPlugin(PluginId.getId("com.dennislang.focus-highlighter"))?.version
        row(5, JBLabel("Version ${version ?: "unknown"}"))
        val repoLink = HyperlinkLabel("Repo: github.com/dlang").apply {
            setHyperlinkTarget("https://github.com/dlang")
        }
        row(6, repoLink)
        row(7, JBLabel("© 2026 LanDen Labs - Dennis Lang"))

        panel.add(JPanel(), GridBagConstraints().apply {
            gridx = 0; gridy = 8; weighty = 1.0; fill = GridBagConstraints.VERTICAL
        })

        reset()
        return panel
    }

    override fun isModified(): Boolean {
        val s = FocusHighlighterSettings.getInstance().state
        val currentColor = (colorPanel.selectedColor?.rgb ?: s.colorRgb) and 0xFFFFFF
        return enabledCheck.isSelected != s.enabled ||
               currentColor != s.colorRgb ||
               thicknessSlider.value != s.thickness ||
               (minSizeSpinner.value as Int) != s.minSize ||
               ignoreDimensionCombo.selectedItem != s.ignoreDimension
    }

    override fun apply() {
        val s = FocusHighlighterSettings.getInstance().state
        s.enabled = enabledCheck.isSelected
        colorPanel.selectedColor?.let { s.colorRgb = it.rgb and 0xFFFFFF }
        s.thickness = thicknessSlider.value
        s.minSize = minSizeSpinner.value as Int
        s.ignoreDimension = ignoreDimensionCombo.selectedItem as FocusHighlighterSettings.IgnoreDimension
    }

    override fun reset() {
        val s = FocusHighlighterSettings.getInstance().state
        enabledCheck.isSelected = s.enabled
        colorPanel.selectedColor = Color(s.colorRgb)
        thicknessSlider.value = s.thickness
        minSizeSpinner.value = s.minSize
        ignoreDimensionCombo.selectedItem = s.ignoreDimension
    }
}
