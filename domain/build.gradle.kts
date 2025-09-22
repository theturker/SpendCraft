plugins {
    alias(libs.plugins.kotlin.jvm)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))

    // testImplementation(libs.junit) // Geçici olarak devre dışı
    implementation(libs.kotlinx.coroutines.core)
    // testImplementation(libs.kotlinx.coroutines.test) // Geçici olarak devre dışı
    
    // Dependency injection
    implementation("javax.inject:javax.inject:1")
}


