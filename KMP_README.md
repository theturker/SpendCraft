# SpendCraft - Kotlin Multiplatform Projesi

Bu proje artık **Kotlin Multiplatform (KMP)** mimarisine sahiptir ve hem Android hem de iOS platformlarında çalışmaktadır.

## 🏗️ Proje Yapısı

```
SpendCraft/
├── shared/                          # Ortak KMP modülü
│   ├── src/
│   │   ├── commonMain/             # Platform-agnostic kod
│   │   │   ├── kotlin/
│   │   │   │   └── com/alperen/spendcraft/shared/
│   │   │   │       ├── domain/     # Domain layer (Use Cases, Repository Interfaces, Models)
│   │   │   │       ├── data/       # Data layer (Repository Implementations)
│   │   │   │       └── di/         # Dependency Injection (Koin)
│   │   │   └── sqldelight/         # SQLDelight database schemas
│   │   ├── androidMain/            # Android-specific kod
│   │   └── iosMain/                # iOS-specific kod
│   └── build.gradle.kts
│
├── app/                            # Android uygulaması
├── iosApp/                         # iOS uygulaması (SwiftUI)
│   └── iosApp/
│       ├── Views/                  # SwiftUI Views
│       ├── ViewModels/             # iOS ViewModels
│       └── SpendCraftApp.swift
└── [diğer Android modülleri]
```

## 🎯 Shared Modülde Neler Var?

### Domain Layer
- **Models**: `Transaction`, `Category`, `Account`, `Budget`, `Streak`, `Money`
- **Repository Interfaces**: Platform-agnostic repository tanımları
- **Use Cases**: İş mantığı katmanı
  - `ObserveTransactionsUseCase`
  - `UpsertTransactionUseCase`
  - `DeleteTransactionUseCase`
  - `ObserveCategoriesUseCase`
  - `InsertCategoryUseCase`

### Data Layer
- **SQLDelight Database**: Platform-agnostic SQL veritabanı
- **Repository Implementations**: Repository interface'lerinin implementasyonları
- **Database Drivers**: Platform-specific (Android: AndroidSqliteDriver, iOS: NativeSqliteDriver)

### Dependency Injection
- **Koin**: Platform-agnostic DI framework
- Her platform için özel modüller (AndroidModule, IosModule)

## 🚀 Android Tarafı

Android uygulaması mevcut yapısını korur ama artık `shared` modülünü kullanır.

### Değişiklikler
1. `app/build.gradle.kts`'ye `implementation(project(":shared"))` eklenmeli
2. Hilt yerine Koin kullanılacak (opsiyonel - mevcut Hilt yapısı da korunabilir)
3. Domain ve Data katmanları shared modülünden import edilir

### Çalıştırma
```bash
./gradlew :app:assembleDebug
```

## 🍎 iOS Tarafı

iOS uygulaması SwiftUI ile yazılmıştır ve Android uygulamasıyla aynı özelliklere sahiptir.

### Özellikler
- ✅ İşlem listesi ve ekleme
- ✅ Kategoriler yönetimi
- ✅ Raporlar ve grafikler
- ✅ Ayarlar
- ✅ Dark mode desteği
- ✅ Türkçe dil desteği

### Kurulum ve Çalıştırma

1. **CocoaPods Kurulumu**:
```bash
cd iosApp
pod install
```

2. **Xcode'da Açma**:
```bash
open iosApp.xcworkspace
```

3. **Framework Build**:
```bash
./gradlew :shared:syncFramework
```

4. **Çalıştırma**: Xcode'dan Run (⌘+R)

## 📦 Teknolojiler

### Shared (KMP)
- **Kotlin 2.0.20**
- **Coroutines**: Asenkron işlemler
- **Kotlinx Serialization**: JSON serileştirme
- **Kotlinx DateTime**: Platform-agnostic tarih/saat
- **SQLDelight 2.0.2**: Platform-agnostic veritabanı
- **Koin 4.0.0**: Dependency injection
- **Ktor**: Networking (API çağrıları için)

### Android
- **Jetpack Compose**: Modern UI
- **Hilt/Koin**: Dependency injection
- **Material 3**: Design system
- **Firebase**: Analytics, Crashlytics
- **AdMob**: Reklamlar
- **Billing**: In-app satın almalar

### iOS
- **SwiftUI**: Modern UI framework
- **Combine**: Reactive programming
- **Charts**: Grafik ve raporlar
- **UserDefaults**: Ayarlar saklama

## 🔧 Geliştirme

### Yeni Özellik Ekleme

1. **Shared Modülde**:
   - Model'i `shared/src/commonMain/kotlin/.../domain/model/` içine ekle
   - Repository interface'i `domain/repository/` içine ekle
   - Use case'i `domain/usecase/` içine ekle
   - Repository implementasyonunu `data/repository/` içine ekle
   - Gerekirse SQLDelight şemasına ekle

2. **Android'de**:
   - Jetpack Compose ile UI oluştur
   - Shared'daki use case'leri kullan

3. **iOS'ta**:
   - SwiftUI ile UI oluştur
   - Shared framework'ten use case'leri kullan

### Database Şeması Değişikliği

1. `.sq` dosyasını güncelle
2. Migration yaz (gerekirse)
3. Gradle sync yap
4. Her iki platformda test et

### Platform-Specific Kod

**Expect/Actual Pattern** kullan:

```kotlin
// commonMain
expect class PlatformSpecific {
    fun doSomething(): String
}

// androidMain
actual class PlatformSpecific {
    actual fun doSomething(): String = "Android"
}

// iosMain
actual class PlatformSpecific {
    actual fun doSomething(): String = "iOS"
}
```

## 🧪 Test

### Shared Tests
```bash
./gradlew :shared:test
```

### Android Tests
```bash
./gradlew :app:testDebugUnitTest
./gradlew :app:connectedAndroidTest
```

### iOS Tests
Xcode'dan ⌘+U ile test edebilirsiniz.

## 📝 TODO

### Tamamlananlar ✅
- [x] KMP yapılandırması
- [x] Domain layer'ı shared'a taşıma
- [x] Model sınıflarını shared'a taşıma
- [x] Data layer için expect/actual pattern
- [x] iOS projesi ve SwiftUI UI
- [x] Koin DI entegrasyonu

### Yapılacaklar 📋
- [ ] ViewModels'ı shared'a taşıma (Compose Multiplatform için)
- [ ] Android tarafını shared'a bağlama
- [ ] Firebase/Analytics wrapperları
- [ ] Tüm use case'leri taşıma
- [ ] Budget, Achievements, AI özellikleri
- [ ] iOS için Koin entegrasyonu
- [ ] Test coverage

## 🔐 Platform Farklılıkları

| Özellik | Android | iOS | Çözüm |
|---------|---------|-----|-------|
| Database | Room | SQLDelight | SQLDelight (her iki platform) |
| DI | Hilt/Koin | Koin | Koin (shared) |
| UI | Compose | SwiftUI | Platform-specific |
| Navigation | Compose Nav | SwiftUI Nav | Platform-specific |
| Preferences | DataStore | UserDefaults | Expect/Actual |
| Analytics | Firebase | Firebase iOS SDK | Wrapper sınıf |

## 📱 Özellikler

### Mevcut Özellikler
- ✅ İşlem ekleme/düzenleme/silme
- ✅ Kategori yönetimi
- ✅ Hesap yönetimi
- ✅ Raporlar ve analizler
- ✅ Bütçe takibi
- ✅ Streak sistemi
- ✅ Dark mode
- ✅ Çoklu para birimi

### Yakında
- 🚧 AI-powered insights
- 🚧 Cloud sync
- 🚧 Widget'lar
- 🚧 Apple Watch app
- 🚧 iPad optimization

## 🤝 Katkıda Bulunma

1. Fork yapın
2. Feature branch oluşturun (`git checkout -b feature/AmazingFeature`)
3. Commit edin (`git commit -m 'Add some AmazingFeature'`)
4. Push edin (`git push origin feature/AmazingFeature`)
5. Pull Request açın

## 📄 Lisans

Bu proje özel bir projedir.

## 👨‍💻 Geliştirici

**Alperen Türker**

---

**Not**: Bu KMP dönüşümü, mevcut Android uygulamanızı bozmadan iOS versiyonunu ekler. Her iki platform da aynı business logic'i kullanır ama native UI deneyimi sunar.

