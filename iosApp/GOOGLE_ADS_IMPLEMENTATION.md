# iOS Google Ads Entegrasyonu

## 📋 Genel Bakış

Bu dokümantasyon, SpendCraft iOS uygulamasına Google Mobile Ads SDK entegrasyonunu açıklamaktadır. Android tarafındaki implementasyona benzer bir yapı kurulmuştur.

## ✅ Yapılan Değişiklikler

### 1. **Podfile Güncellemesi**
- `Google-Mobile-Ads-SDK` dependency eklendi
- Pod install ile SDK başarıyla yüklendi (v12.12.0)

### 2. **AdsManager.swift** (Yeni Dosya)
Singleton pattern kullanarak reklam yönetimini merkezi bir noktadan kontrol eder:

**Özellikler:**
- ✅ Singleton pattern ile tek instance
- ✅ Premium kullanıcı kontrolü (UserDefaults ile)
- ✅ Banner, Interstitial ve Rewarded reklam desteği
- ✅ Test ve Production ad unit ID'leri (#if DEBUG kontrolü ile)
- ✅ FullScreenContentDelegate implementasyonu (Google Ads SDK 12.x)
- ✅ Otomatik reklam yeniden yükleme
- ✅ Yeni Google Ads SDK API'lerine uyumlu (v12.12.0)

**Ad Unit ID'leri:**
```swift
// Test IDs (DEBUG build)
Banner: ca-app-pub-3940256099942544/2934735716
Interstitial: ca-app-pub-3940256099942544/4411468910
Rewarded: ca-app-pub-3940256099942544/1712485313

// Production IDs - Gerçek AdMob hesabınızdan alınacak
// AdsManager.swift dosyasındaki AdUnitIDs struct'ında güncelleyin
```

### 3. **BannerAdView.swift** (Yeni Dosya)
SwiftUI için banner reklam komponenti:

**Özellikler:**
- ✅ UIViewRepresentable wrapper ile GADBannerView entegrasyonu
- ✅ Premium kullanıcılar için otomatik gizleme
- ✅ Standard ve Adaptive banner desteği
- ✅ GADBannerViewDelegate implementasyonu
- ✅ SwiftUI preview desteği

**Kullanım:**
```swift
// Standard Banner
BannerAdView()

// Adaptive Banner (ekran genişliğine göre)
AdaptiveBannerAdView()
```

### 4. **Info.plist Güncellemesi**
Google Ads için gerekli key'ler eklendi:

- `GADApplicationIdentifier`: Test App ID (Production'da değiştirilmeli)
- `GADIsAdManagerApp`: Google Ad Manager desteği
- `NSUserTrackingUsageDescription`: App Tracking Transparency açıklaması
- `SKAdNetworkItems`: 48 adet SKAdNetwork identifier (Google Ads için gerekli)

### 5. **SpendCraftiOSApp.swift**
```swift
init() {
    // ...
    AdsManager.shared.initializeAds()
    // ...
}
```

### 6. **Banner Reklamlar Eklenen Ekranlar**

#### ✅ DashboardView
- Ana sayfa alt kısmında adaptive banner
- VStack yapısı ile ScrollView + Banner kombinasyonu

#### ✅ TransactionsTabView
- İşlemler sayfası alt kısmında adaptive banner
- Liste görünümü ile uyumlu yerleşim

#### ✅ ReportsView
- Raporlar sayfası alt kısmında adaptive banner
- Chart'lar ve istatistikler altında görünür

**Ortak Implementasyon:**
```swift
VStack(spacing: 0) {
    ScrollView {
        // İçerik
    }
    
    // Banner Ad at bottom
    AdaptiveBannerAdView()
        .background(Color(uiColor: .systemBackground))
        .shadow(color: .black.opacity(0.1), radius: 4, y: -2)
}
```

### 7. **shared.podspec Düzeltmesi**
- `spec.libraries = sqlite3` → `spec.libraries = 'sqlite3'` (String formatı)

### 8. **Google Ads SDK 12.x API Güncellemeleri**
Yeni SDK versiyonunda API değişiklikleri yapıldı:
- ✅ `GADInterstitialAd` → `InterstitialAd`
- ✅ `GADRewardedAd` → `RewardedAd`
- ✅ `GADFullScreenPresentingAd` → `FullScreenPresentingAd`
- ✅ `GADFullScreenContentDelegate` → `FullScreenContentDelegate`
- ✅ `GADMobileAds.sharedInstance()` → `GADMobileAds.sharedInstance` (property)
- ✅ `withAdUnitID:request:` → `with:request:` (method argument labels)
- ✅ `fromRootViewController:` → `from:` (present method)
- ✅ `GADSimulatorID` kaldırıldı → "Simulator" string olarak kullanılıyor

## 🔧 Kurulum Adımları

1. **Podfile güncellemesi otomatik yapıldı**
```bash
cd iosApp
pod install
```

2. **Xcode'da projeyi açın**
```bash
open SpendCraftiOS.xcworkspace
```

3. **Test build çalıştırın**
- Simulator veya gerçek cihazda çalıştırın
- Banner reklamlar otomatik olarak yüklenecek

## 🎯 Production'a Geçiş

### 1. AdMob Hesabı Ayarları
1. [Google AdMob Console](https://apps.admob.com/) üzerinden:
   - Yeni uygulama oluşturun
   - Banner, Interstitial ve Rewarded ad unit'leri oluşturun
   - Ad Unit ID'lerini kopyalayın

### 2. Ad Unit ID Güncellemesi
`AdsManager.swift` dosyasında:
```swift
#else
static let banner = "ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX"
static let interstitial = "ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX"
static let rewarded = "ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX"
#endif
```

### 3. Info.plist Güncellemesi
```xml
<key>GADApplicationIdentifier</key>
<string>ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX</string>
```

### 4. App Tracking Transparency
iOS 14+ için kullanıcılardan izin istemek:
```swift
import AppTrackingTransparency

func requestTrackingAuthorization() {
    ATTrackingManager.requestTrackingAuthorization { status in
        // Handle status
    }
}
```

## 💰 Premium Kullanıcı Yönetimi

### Premium Durumunu Güncelleme
```swift
// Premium satın alındığında
AdsManager.shared.updatePremiumState(true)

// Premium iptal edildiğinde
AdsManager.shared.updatePremiumState(false)
```

### Premium Kullanıcılar İçin
- Banner reklamlar otomatik gizlenir
- Interstitial reklamlar gösterilmez
- Rewarded reklamlar hemen reward verir (gösterilmez)

## 📊 Interstitial ve Rewarded Reklamlar

### Interstitial Kullanımı
```swift
// Yükle
AdsManager.shared.loadInterstitialAd()

// Göster
if let viewController = UIApplication.shared.windows.first?.rootViewController {
    AdsManager.shared.showInterstitialAd(from: viewController) {
        // Reklam kapatıldı
    }
}
```

### Rewarded Kullanımı
```swift
// Yükle
AdsManager.shared.loadRewardedAd()

// Göster
if let viewController = UIApplication.shared.windows.first?.rootViewController {
    AdsManager.shared.showRewardedAd(
        from: viewController,
        onRewarded: {
            // Kullanıcı ödül kazandı
        },
        onAdClosed: {
            // Reklam kapatıldı
        }
    )
}
```

## 🐛 Debugging

### Console Logları
AdsManager ve BannerAdView detaylı log mesajları içerir:
- ✅ Reklam başarıyla yüklendi
- ❌ Reklam yüklenemedi + hata mesajı
- 📊 Impression kaydedildi
- 🔼 Tam ekran içerik gösterildi
- 🔽 Tam ekran içerik kapatıldı

### Test Cihazlar
`AdsManager.swift` içinde test cihaz ID'leri ekleyebilirsiniz:
```swift
#if DEBUG
GADMobileAds.sharedInstance().requestConfiguration.testDeviceIdentifiers = [
    GADSimulatorID as! String,
    "YOUR_DEVICE_ID_HERE"
]
#endif
```

## 📝 Notlar

1. **Test Reklamları**: DEBUG build'lerde otomatik olarak test reklamları gösterilir
2. **Production Reklamları**: Release build için gerçek Ad Unit ID'leri eklenmeli
3. **Premium Entegrasyonu**: Şu an UserDefaults kullanılıyor, in-app purchase sistemi ile entegre edilmeli
4. **SKAdNetwork**: Info.plist'e 48 adet SKAdNetwork identifier eklendi (Google tarafından önerilen)
5. **App Store Review**: Test reklamlarının production'da kaldırıldığından emin olun

## 🔗 Yararlı Linkler

- [Google Mobile Ads iOS Docs](https://developers.google.com/admob/ios/quick-start)
- [AdMob Console](https://apps.admob.com/)
- [Banner Ads Guide](https://developers.google.com/admob/ios/banner)
- [Interstitial Ads Guide](https://developers.google.com/admob/ios/interstitial)
- [Rewarded Ads Guide](https://developers.google.com/admob/ios/rewarded)
- [App Tracking Transparency](https://developer.apple.com/documentation/apptrackingtransparency)

## ✨ Sonuç

iOS tarafında Google Ads entegrasyonu başarıyla tamamlandı! Android implementasyonuna benzer bir yapı kuruldu ve aşağıdaki ekranlarda reklamlar gösteriliyor:

- ✅ Dashboard (Ana Sayfa)
- ✅ Transactions (İşlemler)
- ✅ Reports (Raporlar)

Reklamlar, premium kullanıcılar için otomatik olarak gizleniyor ve test modunda çalışıyor. Production'a geçiş için yukarıdaki adımları takip edin.

