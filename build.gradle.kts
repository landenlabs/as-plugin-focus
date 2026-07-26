plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij") version "1.17.4"
}

group   = "com.dennislang"
version = "1.0.0"

repositories {
    mavenCentral()
}

intellij {
    version.set("2024.1")
    type.set("IC")          // IntelliJ Community; switch to "AI" to sandbox inside Android Studio
    plugins.set(listOf())
    updateSinceUntilBuild.set(false)
}

kotlin {
    jvmToolchain(17)
}

tasks {
    patchPluginXml {
        sinceBuild.set("241")
        untilBuild.set("")
    }
    // instrumentCode relies on a JAR removed in IntelliJ 2024.1+; disable it.
    // (Only affects Java null-check bytecode injection — not needed for Kotlin.)
    instrumentCode {
        enabled = false
    }
}
