

// SUPPRIMEZ ce bloc plugins. Il est déjà défini dans le build.gradle.kts racine.
/*
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android) version "2.0.0"  apply false
    id("com.google.gms.google-services") version "4.4.4" apply false
}
*/

// AJOUTEZ ce bloc "plugins" qui APPLIQUE les plugins au lieu de les définir.
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.hilt)
    id("org.jetbrains.kotlin.kapt")
}


android {
    namespace = "com.example.healthconnect"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.healthconnect"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }


    buildFeatures {
        compose = true
    }



    // DÉPLACEZ le bloc kotlin ici, à l'intérieur de android { ... }
    kotlinOptions {
        jvmTarget = "11"
    }
}

// SUPPRIMEZ ce bloc de l'extérieur.
/*
kotlin {
    jvmToolchain(11) 
}
*/

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose (BOM gère les versions)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.compose.material.icons.core)
    implementation("androidx.compose.material:material-icons-extended")
    // Navigation
    implementation(libs.androidx.navigation.compose)

    // UI (optionnel)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.tooling.test)
    // --- DÉPENDANCES HILT AJOUTÉES ---
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation("io.coil-kt:coil-compose:2.6.0")



    // Import the Firebase BoM
    implementation(platform("com.google.firebase:firebase-bom:34.5.0"))

    // AJOUTEZ CETTE LIGNE - C'est une dépendance de base souvent nécessaire
    implementation("com.google.firebase:firebase-analytics")

    // --- DÉPENDANCES FIREBASE AJOUTÉES ---
    // Pour l'authentification (connexion, inscription)
    implementation("com.google.firebase:firebase-auth")

    // Pour la base de données Firestore (stockage des données)
    implementation("com.google.firebase:firebase-firestore")
}
kapt {
    correctErrorTypes = true
}
