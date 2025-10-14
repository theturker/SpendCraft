# 🎯 iOS Interstitial Reklam Test Kılavuzu

## ✅ Yapılan Güncellemeler

### 1. **Gerçek Reklam ID'leri Aktif**
- DEBUG ve RELEASE modda gerçek AdMob ID'leriniz kullanılıyor
- Banner: `ca-app-pub-3199248450820147/4629737731`
- Interstitial: `ca-app-pub-3199248450820147/4092156468`

### 2. **Interstitial Reklamların Gösterileceği Yerler**
- ✅ **AI Önerileri Ekranı**: Ekran açıldığında 1 saniye sonra
- ✅ **İçe/Dışa Aktar Ekranı**: Ekran açıldığında 1 saniye sonra

### 3. **Detaylı Debug Logları Eklendi**
Artık Console'da şu mesajları göreceksiniz:
- 📱 Uygulama başladı - Interstitial yükleniyor
- 🔄 Interstitial reklam yükle isteği
- ✅ Interstitial reklam başarıyla yüklendi
- ❌ Interstitial yüklenemedi (hata mesajı ile)
- 🎯 Ekran açıldı - Reklam göstermeye çalışıyor
- 🎬 Reklam gösteriliyor
- ⚠️ Reklam hazır değil - Yeni reklam yükleniyor

---

## 🧪 Test Adımları

### 1. Uygulamayı Yeniden Derleyin
```bash
# Xcode'da temiz build
Product > Clean Build Folder (Shift + Cmd + K)
Product > Run (Cmd + R)
```

### 2. Console'u Açın
Xcode'da **Console** (Debug Area) açık olmalı:
- View > Debug Area > Show Debug Area (Cmd + Shift + Y)

### 3. Uygulamayı Test Edin

#### A) İlk Açılış
Console'da görmeli:
```
📱 App started - Preloading interstitial ads...
🔄 Loading interstitial ad with ID: ca-app-pub-3199248450820147/4092156468
✅ Interstitial ad loaded successfully!
```

#### B) AI Önerileri Ekranı
1. AI Önerileri'ne gidin
2. Console'da görmeli:
```
🎯 AI Suggestions View - Attempting to show interstitial ad...
✅ AI Suggestions - Root view controller found
🚀 AI Suggestions - Showing interstitial ad now...
🎬 Presenting interstitial ad...
```
3. **1 saniye sonra tam ekran reklam görmelisiniz**

#### C) İçe/Dışa Aktar Ekranı
1. İçe/Dışa Aktar'a gidin
2. Console'da görmeli:
```
🎯 Export View - Attempting to show interstitial ad...
✅ Export View - Root view controller found
🚀 Export View - Showing interstitial ad now...
🎬 Presenting interstitial ad...
```
3. **1 saniye sonra tam ekran reklam görmelisiniz**

---

## ⚠️ Sorun Giderme

### Sorun: "Interstitial ad not ready - Loading new ad..."
**Neden**: Reklam henüz yüklenmedi  
**Çözüm**: 
- İlk kez ekrana giriyorsanız, 5-10 saniye bekleyip tekrar deneyin
- AdMob'da reklam birimi yeni oluşturulduysa, aktif olması 1-2 saat sürebilir

### Sorun: "Failed to load interstitial ad: ..."
**Olası Nedenler**:
1. **İnternet bağlantısı yok**
2. **AdMob'da reklam birimi henüz aktif değil** (yeni oluşturulduysa 1-2 saat bekleyin)
3. **Ad Unit ID yanlış** (tekrar kontrol edin)
4. **Cihaz/Simulator test cihazı olarak işaretli değil**

**Çözümler**:
- İnternet bağlantısını kontrol edin
- AdMob Console'da reklam biriminin "Etkin" olduğunu doğrulayın
- ID'lerin doğru olduğunu kontrol edin:
  ```swift
  Banner: ca-app-pub-3199248450820147/4629737731
  Interstitial: ca-app-pub-3199248450820147/4092156468
  ```

### Sorun: Console'da hiçbir mesaj görünmüyor
**Çözüm**: Xcode'da Debug Area açık olmalı
- View > Debug Area > Show Debug Area (Cmd + Shift + Y)

---

## 📊 AdMob Console Kontrolü

1. [AdMob Console](https://apps.admob.com/) gidin
2. Uygulamanızı seçin
3. **Reklam birimleri** sekmesine gidin
4. Interstitial reklam biriminizi kontrol edin:
   - ✅ **Durum**: Etkin olmalı
   - ✅ **Ad Unit ID**: `ca-app-pub-3199248450820147/4092156468` olmalı
   - ℹ️ **Gösterimler**: İlk birkaç saat boyunca "0" olabilir (normal)

---

## 🎯 Beklenen Davranış

### ✅ Başarılı Senaryo
1. Uygulama açılır → Interstitial reklam arka planda yüklenir
2. AI Önerileri'ne gidersiniz → 1 saniye sonra tam ekran reklam görürsünüz
3. Reklamı kapatırsınız → Yeni reklam otomatik yüklenir
4. İçe/Dışa Aktar'a gidersiniz → 1 saniye sonra tam ekran reklam görürsünüz

### Banner Reklamlar
- ✅ Dashboard (Ana Sayfa) - Alt kısımda
- ✅ İşlemler - Alt kısımda
- ✅ Raporlar - Alt kısımda

Banner reklamlar her zaman görünür (premium olmayan kullanıcılar için).

---

## 💡 Notlar

- **İlk açılışta** reklamların yüklenmesi 5-10 saniye sürebilir
- **Yeni oluşturulan** AdMob reklam birimleri 1-2 saat içinde aktif olur
- **Test cihazlarında** reklam gösterimi daha tutarlıdır
- **Premium kullanıcılar** hiç reklam görmez

---

## 🚀 Production'a Hazırlık

Şu anda tüm ayarlar production için hazır:
- ✅ Gerçek App ID: `ca-app-pub-3199248450820147~6334294542`
- ✅ Gerçek Banner ID: `ca-app-pub-3199248450820147/4629737731`
- ✅ Gerçek Interstitial ID: `ca-app-pub-3199248450820147/4092156468`
- ✅ Premium kullanıcı kontrolü aktif
- ✅ Otomatik reklam yeniden yükleme aktif

Archive alıp TestFlight/App Store'a yükleyebilirsiniz!

