plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
}

android {
    namespace = "com.alperen.spendcraft"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.alperen.spendcraft"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = 36
        versionCode = 2
        versionName = "1.0.1"

        vectorDrawables { useSupportLibrary = true }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
        viewBinding = true
    }
    packaging {
        resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" }
    }
}

dependencies {
    implementation(project(":core:ui"))
    implementation(project(":core:analytics"))
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:billing"))
    implementation(project(":core:premium"))
    implementation(project(":core:ai"))
    implementation(project(":core:notifications"))
    implementation(project(":domain"))
    implementation(project(":data:repository"))
    implementation(project(":data:db"))
    implementation(project(":feature:transactions"))
    implementation(project(":feature:reports"))
    implementation(project(":feature:paywall"))
    implementation(project(":feature:premiumdebug"))
    implementation(project(":feature:ai"))
    implementation(project(":feature:settings"))
    implementation(project(":feature:accounts"))
    implementation(project(":feature:recurrence"))
    implementation(project(":feature:sharing"))
    implementation(project(":feature:dashboard"))
    implementation(project(":feature:notifications"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:achievements"))
    implementation(project(":tools:export"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.coil)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics.ktx)
    implementation("com.google.firebase:firebase-perf-ktx")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.retrofit)
    implementation(libs.retrofit.moshi)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)

    implementation(libs.inapp.review)
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    
    // AdMob
    implementation(libs.play.services.ads)
    
    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    
    // Play Billing
    implementation("com.android.billingclient:billing-ktx:6.2.1")
    
    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    
    // Core library desugaring
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    
    debugImplementation(libs.androidx.compose.ui.tooling)
    
    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.1.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.room:room-testing:2.6.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
}