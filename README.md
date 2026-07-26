<table border="0">
  <tr>
    <td>
      <!-- VERSION -->v6.07.20<br>
      <!-- DATE -->26-Jul-2026<br>
      Android Studio / IntelliJ Plugin (Kotlin)<br>
      <a href="https://github.com/dlang">Repo</a>
    </td>
    <td>
      <a href="https://landenlabs.com">
        <img src="screens/landenlabs_400.webp" width="300" alt="LanDen Labs">
      </a>
    </td>
  </tr>
</table>

# Focus Highlighter

An Android Studio / IntelliJ IDEA plugin that draws a colored border around whichever
panel currently has keyboard focus — a tool window (Project, Logcat, Build, etc.) or
an editor split — so it's obvious at a glance which one is active.

**By [LanDen Labs](https://github.com/landenlabs) (2026)**

---

## How it works

- `FocusHighlighterStartupActivity` wires up two focus sources:
  - Every editor gets its own focus listener attached as it's created
    (`EditorFactory.addEditorFactoryListener` → `EditorEx.addFocusListener`) — there's
    no single app-wide multicaster for focus events.
  - `ToolWindowManagerListener` (per project) fires when the active tool window changes.
- Both funnel into `FocusHighlighter`, an app-wide singleton that paints a border on
  whichever single component currently owns focus and clears it from the previous one.

## Settings

Open **Settings → Tools → Focus Highlighter** to configure:

- Enable/disable toggle
- Border color
- Border thickness (1–6 px)

The same panel shows the installed plugin version, a link to the repo, and a
copyright line.

## Project structure

```
as-plugin-focus/
├── build.gradle.kts               # version = X.Y.Z (bumped manually to match VERSION)
├── src/main/resources/META-INF/plugin.xml   # <version>X.Y.Z</version> (bumped manually to match VERSION)
├── src/main/kotlin/com/dennislang/focushighlighter/
│   ├── FocusHighlighter.kt              # Core border-painting logic
│   ├── FocusHighlighterStartupActivity.kt  # Registers the editor + tool window focus listeners
│   ├── FocusHighlighterSettings.kt      # Persistent app-level state (saved to focus-highlighter.xml)
│   └── FocusHighlighterConfigurable.kt  # Settings UI under Settings -> Tools -> Focus Highlighter
├── VERSION                         # Bare X.Y.Z, source of truth for the version above
├── set-version.bash                # Bump version, commit, tag, push (macOS/Linux)
├── set-version.ps1                 # Bump version, commit, tag, push (Windows)
├── README.md
├── LICENSE
└── .github/workflows/release.yml   # Tag-triggered build + GitHub Release
```

## Build & Install

### Build the plugin zip

```bash
cd as-plugin-focus
./gradlew buildPlugin
```

Output: `build/distributions/focus-highlighter-plugin-<version>.zip`

### Install in Android Studio

1. Open **Settings → Plugins**
2. Click the **⚙** gear icon → **Install Plugin from Disk…**
3. Select the zip file above

### Run in a sandboxed IDE (for development/testing)

```bash
./gradlew runIde
```

## Targeting Android Studio vs IntelliJ

In `build.gradle.kts`, the `type` setting controls which IDE is used for sandboxing:

```kotlin
intellij {
    version.set("2024.1")
    type.set("IC")   // "IC" = IntelliJ Community, "AI" = Android Studio
}
```

Switch to `"AI"` and set `version` to the matching Android Studio build number to
sandbox directly inside Android Studio. The plugin only uses stable, long-standing
platform APIs (no Android-specific APIs), so compiling against IC works fine even
though it will run inside Android Studio once installed.

## Known limitation

Only one component is tracked as "focused" at a time (app-wide). If the whole IDE
window loses focus to another application, the last-highlighted border stays drawn
until you click back into the IDE and focus moves again.

---

## Releasing

Versions are bumped with `set-version.bash` (or `set-version.ps1` on Windows), run
from the repo root:

```bash
./set-version.bash -version 1.1.0 -message "Add distinct light/dark border colors"
```

This updates `VERSION` and the `<!-- VERSION -->v6.07.20<!-- DATE -->26-Jul-2026
README, then commits, tags, and pushes. Because this is a plain Gradle/Kotlin
project rather than an Android app, `build.gradle.kts`'s `version = "..."` and
`plugin.xml`'s `<version>...</version>` aren't touched by the script (it only
special-cases Android's `versionName`) — bump those two by hand to match `VERSION`
before running the script.

Pushing the resulting `vX.Y.Z` tag triggers `.github/workflows/release.yml`, which
runs `./gradlew buildPlugin` and publishes the resulting zip to a GitHub Release.
Unlike an Android APK, an IntelliJ plugin zip needs no signing keystore, so no repo
secrets are required.
