# 🚀 SpendCraft KMP - Quick Start Guide

Bu dokümanda SpendCraft Kotlin Multiplatform projesini hızlıca çalıştırmak için gereken adımlar bulunmaktadır.

## 📋 Gereksinimler

### Tüm Platformlar
- ✅ JDK 17 veya üzeri
- ✅ Android Studio Hedgehog (2023.1.1) veya üzeri
- ✅ Gradle 8.6+

### Android Geliştirme
- ✅ Android SDK 24-36
- ✅ Android Emulator veya fiziksel cihaz

### iOS Geliştirme (sadece macOS)
- ✅ Xcode 15+
- ✅ CocoaPods (`sudo gem install cocoapods`)
- ✅ iOS Simulator veya fiziksel cihaz

## 🏃 Hızlı Başlangıç

### 1. Projeyi Klonlama

```bash
git clone https://github.com/alperenturker/SpendCraft.git
cd SpendCraft
```

### 2. Shared Modülü Build Etme

```bash
# Tüm platformlar için build
./gradlew :shared:build

# Sadece Android
./gradlew :shared:assembleDebug

# iOS Framework oluşturma
./gradlew :shared:syncFramework
```

### 3. Android Uygulamasını Çalıştırma

**Option A: Android Studio**
1. Android Studio'yu açın
2. `Open` -> SpendCraft klasörünü seçin
3. Gradle sync bekleyin
4. Run 'app' ▶️

**Option B: Terminal**
```bash
# Debug APK oluştur
./gradlew :app:assembleDebug

# Cihaza yükle ve çalıştır
./gradlew :app:installDebug

# Direkt çalıştır
./gradlew :app:run
```

### 4. iOS Uygulamasını Çalıştırma

**Terminal:**
```bash
# CocoaPods kurulumu
cd iosApp
pod install

# Xcode workspace'i aç
open iosApp.xcworkspace
```

**Xcode'da:**
1. Scheme: `iosApp` seçin
2. Simulator veya cihaz seçin
3. Run (⌘+R)

## 🔧 Yaygın Sorunlar ve Çözümleri

### Sorun 1: Shared modülü bulunamıyor

```bash
# Çözüm
./gradlew clean
./gradlew :shared:build
# File -> Invalidate Caches / Restart (Android Studio)
```

### Sorun 2: SQLDelight kod oluşturulmuyor

```bash
./gradlew :shared:generateSqlDelightInterface
```

### Sorun 3: iOS Framework hatası

```bash
./gradlew :shared:syncFramework
cd iosApp
pod deintegrate
pod install
```

### Sorun 4: Koin hatası

Eğer Koin initialization hatası alırsanız:
```kotlin
// Application sınıfında
startKoin {
    androidContext(this@YourApplication)
    modules(getSharedModules())
}
```

### Sorun 5: CocoaPods hatası

```bash
sudo gem install cocoapods
pod repo update
cd iosApp
pod install
```

## 📱 Test Etme

### Android Unit Tests
```bash
./gradlew :shared:testDebugUnitTest
./gradlew :app:testDebugUnitTest
```

### Android Instrumentation Tests
```bash
./gradlew :app:connectedDebugAndroidTest
```

### iOS Tests
```bash
cd iosApp
xcodebuild test -workspace iosApp.xcworkspace -scheme iosApp -destination 'platform=iOS Simulator,name=iPhone 15'
```

## 🏗️ Build Tipleri

### Debug Build (Android)
```bash
./gradlew :app:assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

### Release Build (Android)
```bash
./gradlew :app:assembleRelease
# Output: app/build/outputs/apk/release/app-release.apk
```

### Debug Build (iOS)
Xcode'da Scheme: Debug seçili olmalı

### Release Build (iOS)
1. Xcode'da Product -> Archive
2. Distribute App -> Ad Hoc / App Store

## 🔍 Önemli Gradle Komutları

```bash
# Tüm projeyi temizle
./gradlew clean

# Sadece shared modülü build et
./gradlew :shared:build

# Android app build et
./gradlew :app:build

# iOS framework sync
./gradlew :shared:syncFramework

# Dependency'leri görüntüle
./gradlew :shared:dependencies

# Task'ları listele
./gradlew tasks

# Build cache temizle
./gradlew cleanBuildCache
```

## 📊 Proje Yapısı

```
SpendCraft/
├── shared/                    # KMP Modülü
│   ├── build.gradle.kts
│   └── src/
│       ├── commonMain/       # Platform-agnostic kod
│       ├── androidMain/      # Android-specific
│       └── iosMain/          # iOS-specific
│
├── app/                      # Android App
│   ├── build.gradle.kts
│   └── src/main/
│
├── iosApp/                   # iOS App
│   ├── Podfile
│   └── iosApp/
│       └── *.swift
│
├── core/                     # Android Core Modules
├── data/                     # Android Data Modules
├── domain/                   # Android Domain (deprecated)
└── feature/                  # Android Feature Modules
```

## 🎯 Geliştirme Workflow'u

### 1. Feature Ekleme
```bash
# 1. Shared modülde model/use case ekle
# 2. Repository implementasyonu yaz
# 3. Android UI ekle (Compose)
# 4. iOS UI ekle (SwiftUI)
# 5. Test et
```

### 2. Database Değişikliği
```bash
# 1. .sq dosyasını güncelle (shared/src/commonMain/sqldelight)
# 2. Build et
./gradlew :shared:generateSqlDelightInterface
# 3. Migration yaz (gerekirse)
# 4. Test et
```

### 3. Platform-Specific Özellik
```kotlin
// 1. Expect tanımı (commonMain)
expect class PlatformFeature {
    fun doSomething(): String
}

// 2. Android actual (androidMain)
actual class PlatformFeature {
    actual fun doSomething() = "Android"
}

// 3. iOS actual (iosMain)
actual class PlatformFeature {
    actual fun doSomething() = "iOS"
}
```

## 🐛 Debug İpuçları

### Android
- Logcat: `adb logcat | grep SpendCraft`
- Database: Device Explorer -> data/data/com.alperen.spendcraft
- Breakpoint: Android Studio debugger

### iOS
- Console: Xcode -> View -> Debug Area -> Show Debug Area
- Database: Xcode -> Window -> Devices and Simulators -> Installed Apps
- Breakpoint: Xcode debugger

## 📚 Daha Fazla Bilgi

- [KMP_README.md](./KMP_README.md) - Detaylı proje dokümantasyonu
- [MIGRATION_GUIDE.md](./MIGRATION_GUIDE.md) - Android'den KMP'ye geçiş
- [Kotlin Multiplatform Docs](https://kotlinlang.org/docs/multiplatform.html)
- [SQLDelight](https://cashapp.github.io/sqldelight/)
- [Koin](https://insert-koin.io/)

## 💡 Önerilen IDE Ayarları

### Android Studio
```
Preferences -> Build, Execution, Deployment -> Compiler
✅ Build project automatically
✅ Compile independent modules in parallel
```

### Xcode
```
Preferences -> Locations
✅ Derived Data: Custom location
✅ Command Line Tools: Xcode 15+
```

## 🚀 Production Deployment

### Android (Google Play)
```bash
# 1. Release build
./gradlew :app:bundleRelease

# 2. Bundle oluşur
# app/build/outputs/bundle/release/app-release.aab

# 3. Google Play Console'a yükle
```

### iOS (App Store)
```
1. Xcode -> Product -> Archive
2. Organizer -> Distribute App
3. App Store Connect'e yükle
```

## 📞 Yardım

Sorun yaşarsanız:
1. [GitHub Issues](https://github.com/alperenturker/SpendCraft/issues)
2. Clean build: `./gradlew clean build`
3. Invalidate caches (Android Studio)
4. Pod install (iOS)

---

**Başarılar! 🎉**

