# 🍎 iOS Projesini Xcode'da Açma Rehberi

## Yöntem 1: Xcode ile Yeni Proje Oluşturma (Önerilen)

Şu an iOS dosyaları hazır ama Xcode project dosyası yok. En kolay yol Xcode'da yeni bir proje oluşturup dosyalarımızı içe aktarmak:

### Adım 1: Yeni Xcode Projesi Oluşturma

1. **Xcode'u açın**
2. **File > New > Project** seçin
3. **iOS > App** seçin, **Next**
4. Proje ayarları:
   - **Product Name**: `SpendCraft`
   - **Team**: Kendi development team'iniz
   - **Organization Identifier**: `com.alperen.spendcraft`
   - **Bundle Identifier**: `com.alperen.spendcraft.ios`
   - **Interface**: SwiftUI
   - **Language**: Swift
   - **Storage**: None
5. **Kaydetme konumu**: `/Users/alperenturker/SpendCraft/iosApp` klasörünü seçin
6. **Create** tıklayın

### Adım 2: Dosyaları Projeye Ekleme

1. Xcode'da sol taraftaki **Project Navigator**'da `iosApp` klasörüne sağ tıklayın
2. **Add Files to "iosApp"** seçin
3. Şu dosyaları seçin:
   - `ContentView.swift`
   - `Views` klasörü (tüm içeriğiyle)
   - `ViewModels` klasörü (tüm içeriğiyle)
4. **Options** kısmında:
   - ✅ **Copy items if needed** işaretli olsun
   - ✅ **Create groups** seçili olsun
5. **Add** tıklayın

### Adım 3: SpendCraftApp.swift Değiştirme

1. Xcode'da `iosAppApp.swift` dosyasını açın (otomatik oluştu)
2. Tüm içeriğini silin
3. `SpendCraftApp.swift` dosyasının içeriğini kopyalayıp yapıştırın

### Adım 4: Shared Framework Ekleme (Geçici Çözüm)

Shared framework henüz build olmadı. Şimdilik mock data ile çalışacak. İleride framework'ü ekleyeceğiz.

### Adım 5: Çalıştırma

1. Simulator seçin (örn. iPhone 15)
2. **⌘ + R** ile çalıştırın

---

## Yöntem 2: Manuel Xcode Project Oluşturma

### Terminal Komutları

```bash
cd /Users/alperenturker/SpendCraft/iosApp

# Xcode komut satırı araçları ile proje oluştur
xcodebuild -project iosApp.xcodeproj

# Veya direkt Xcode'da aç
open -a Xcode .
```

Sonra **File > New > Project** ile yeni proje oluşturup mevcut dosyaları ekleyin.

---

## Yöntem 3: Basit Test Projesi (En Hızlı)

Sadece iOS UI'ı test etmek istiyorsanız:

1. Xcode'u açın
2. File > New > Project > iOS > App
3. Herhangi bir yere kaydedin
4. `ContentView.swift` dosyasını `iosApp/iosApp/Views/` klasöründeki dosyalarla değiştirin
5. Çalıştırın

---

## Shared Framework'ü Build Etme (İleride)

Şu an shared framework build etmek için cocoapods yapılandırması gerekiyor:

```bash
# 1. Shared modülü build et
cd /Users/alperenturker/SpendCraft
./gradlew :shared:build

# 2. Framework'ü iOS için hazırla
./gradlew :shared:linkDebugFrameworkIosArm64
./gradlew :shared:linkDebugFrameworkIosX64

# 3. Framework'ü Xcode'a ekle
# Xcode'da: General > Frameworks, Libraries, and Embedded Content
# shared/build/bin/iosArm64/debugFramework/shared.framework ekle
```

---

## Alternatif: Swift Package Manager (Önerilen)

Daha gelişmiş bir entegrasyon için Swift Package Manager kullanabilirsiniz:

```bash
# 1. Shared'ı XCFramework olarak build et
./gradlew :shared:assembleXCFramework

# 2. Xcode'da Swift Package olarak ekle
# File > Add Package Dependencies
# Local path: ../shared/build/XCFrameworks/release/
```

---

## Sorun Giderme

### "Shared module not found"
```bash
# Mock data ile çalışır, gerçek framework'ü sonra ekleyebilirsiniz
# ViewModels'daki import shared satırlarını comment out edin
```

### "Cannot find type 'Transaction'"
```swift
// Geçici olarak Transaction struct'ını manuel tanımlayın:
struct Transaction: Identifiable {
    let id: Int64?
    let amount: Money
    let timestampUtcMillis: Int64
    let note: String?
    let categoryId: Int64?
    let accountId: Int64?
    let type: TransactionType
}
```

### "Build failed"
- Clean Build Folder: **⌘ + Shift + K**
- Derived Data temizle: **Xcode > Preferences > Locations**

---

## Hızlı Başlangıç (5 Dakika)

En hızlı yol:

```bash
# Terminal'de
cd /Users/alperenturker/SpendCraft/iosApp
open -a Xcode
```

Sonra Xcode'da:
1. **File > New > Project**
2. **iOS > App** (SwiftUI)
3. Bu dizine kaydet
4. Mevcut `.swift` dosyalarını projeye sürükle-bırak
5. Run!

---

## Notlar

- ⚠️ Shared framework henüz build olmadı
- ✅ UI tamamen çalışır durumda (mock data ile)
- ✅ Tüm ekranlar hazır
- 🔄 Framework entegrasyonu sonra yapılabilir

İlk önce UI'ı test edin, sonra shared framework'ü ekleriz!

