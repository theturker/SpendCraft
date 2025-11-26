plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.alperen.spendcraft.feature.dashboard"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    buildFeatures { compose = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    // KMP Shared module (Domain + Business Logic)
    implementation(project(":shared"))
    
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":domain"))
    implementation(project(":core:ui"))
    implementation(project(":data:repository"))
    implementation(project(":data:db"))
    implementation(project(":core:achievements"))
    implementation(project(":feature:transactions"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.navigation.compose)
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation(libs.coil)

    implementation(libs.hilt.android)
    implementation(libs.androidx.ui.graphics)
    ksp(libs.hilt.compiler)
    
    // ML Kit Text Recognition for receipt scanning
    implementation("com.google.mlkit:text-recognition:16.0.1")
    
    // Kotlinx Coroutines Play Services for Tasks.await
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")
}