# Publishing to JetBrains Marketplace

Android Studio plugins are distributed through the JetBrains Marketplace
(plugins.jetbrains.com) — the same marketplace IntelliJ uses. There is no
separate "Android plugin store".

## One-time setup

1. Create a JetBrains Account at https://account.jetbrains.com if you don't
   have one.
2. Go to https://plugins.jetbrains.com/author/me and create a vendor/publisher
   profile (name, email — this becomes the public-facing author info).
3. Generate a Marketplace API token at
   https://plugins.jetbrains.com/author/me/tokens for automated uploads
   (needed only if publishing via Gradle instead of the web UI).

## Prepare the plugin

1. Make sure `plugin.xml` has everything Marketplace requires:
   - `<name>` and `<id>` — already set.
   - `<vendor>` with an email attribute (currently only `url` is set —
     add `email="..."`).
   - `<description>` — already present, keep it Marketplace-friendly (no
     marketing fluff, explain what it does).
   - `<change-notes>` — not yet present; add one and update it per release.
   - `sinceBuild`/`untilBuild` in `build.gradle.kts` (`patchPluginXml`) —
     confirm the range covers the Android Studio versions you want to
     support.
2. Bump `VERSION` (source of truth used by `build.gradle.kts` and
   `set-version.bash`/`set-version.ps1`).

## Build the distributable

```bash
./gradlew buildPlugin
```

Output zip lands in `build/distributions/`.

## Publish

### Option A — Manual upload (simplest, good for the first release)

1. Go to https://plugins.jetbrains.com/plugin/add
2. Upload the zip from `build/distributions/`.
3. Fill out the listing (tags, screenshots, license, etc.).
4. Submit — JetBrains reviews new plugins before they go public (subsequent
   version updates are usually auto-approved unless flagged).

### Option B — Automated via Gradle (`publishPlugin` task)

1. Store the Marketplace API token as an env var, e.g. `PUBLISH_TOKEN`.
2. Add to `build.gradle.kts`:
   ```kotlin
   tasks {
       publishPlugin {
           token.set(System.getenv("PUBLISH_TOKEN"))
       }
   }
   ```
3. Run:
   ```bash
   ORG_GRADLE_PROJECT_PUBLISH_TOKEN=xxx ./gradlew publishPlugin
   ```
   (or however you choose to inject `PUBLISH_TOKEN`).

## After the first release

- Every new version: bump `VERSION`, update `<change-notes>`, run
  `./gradlew buildPlugin` (or `publishPlugin`), upload/publish.
- Marketplace shows install counts, ratings, and reviews — respond to
  reviews and issues from the plugin's Marketplace page.
