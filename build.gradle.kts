


// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Les plugins d'application et Hilt n'ont besoin d'être définis ici que si
    // vous utilisez leurs versions directement dans le bloc `plugins` des sous-projets,
    // ce qui n'est pas le cas grâce au catalogue de versions (TOML).
    // Pour plus de clarté, nous ne gardons que ce qui est commun ou nécessaire pour le classpath.
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.hilt) apply false // C'est bien de le garder ici

    // La version est déjà dans libs.versions.toml, mais cette syntaxe est aussi correcte.
    id("com.google.gms.google-services") version "4.4.4" apply false
}

