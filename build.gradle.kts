// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false

    // Utilisez la version de Kotlin définie dans votre fichier libs.versions.toml
    alias(libs.plugins.kotlin.android) apply false

    // La version est déjà dans libs.versions.toml, mais cette syntaxe est aussi correcte.
    id("com.google.gms.google-services") version "4.4.4" apply false
}
