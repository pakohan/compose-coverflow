// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    alias(libs.plugins.org.jetbrains.dokka)
    alias(libs.plugins.android.library) apply false
}

tasks.dokkaHtmlMultiModule {
    outputDirectory.set(layout.buildDirectory.dir("doc"))
}

subprojects {
    apply(plugin = "org.jetbrains.dokka")
}
