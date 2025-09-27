# 🛒 Play Console Ürün Bağlantıları

## 📋 Oluşturulması Gereken Ürünler

### 1. **Premium Abonelikler (SUBS)**

#### Aylık Abonelik
```
Ürün ID: premium_monthly
Tür: Abonelik (SUBS)
Fiyat: ₺29,99/ay
Base Plan ID: monthly
```

#### Yıllık Abonelik  
```
Ürün ID: premium_yearly
Tür: Abonelik (SUBS)
Fiyat: ₺299,99/yıl
Base Plan ID: yearly
```

### 2. **Tek Seferlik Satın Almalar (INAPP)**

#### AI Haftalık Paket
```
Ürün ID: ai_weekly
Tür: Tek Seferlik (INAPP)
Fiyat: ₺9,99
```

#### Yaşam Boyu Premium
```
Ürün ID: premium_lifetime
Tür: Tek Seferlik (INAPP)
Fiyat: ₺999,99
```

## 🔧 Play Console Ayarları

### 1. **Base Plan Oluşturma**

Her abonelik için base plan oluşturun:

#### premium_monthly için:
```
Base Plan ID: monthly
Fiyat: ₺29,99
Para Birimi: TRY
```

#### premium_yearly için:
```
Base Plan ID: yearly  
Fiyat: ₺299,99
Para Birimi: TRY
```

### 2. **Offer Ayarları**

#### İlk Hafta Ücretsiz Deneme
```
Offer ID: free_trial
Tür: Free Trial
Süre: 7 gün
```

#### Yıllık İndirim
```
Offer ID: yearly_discount
Tür: Discount
İndirim: %20 (₺59,99 yerine ₺47,99)
```

## 📱 Uygulama Entegrasyonu

### 1. **BillingRepository Güncellemesi**

```kotlin
// data/repository/src/main/java/com/alperen/spendcraft/data/repository/BillingRepository.kt

class BillingRepository @Inject constructor(
    private val billingManager: BillingManager,
    private val premiumStateDataStore: PremiumStateDataStore
) {
    
    suspend fun getAvailableProducts(): List<ProductDetails> {
        return billingManager.queryProductDetails(
            listOf(
                "premium_monthly",
                "premium_yearly", 
                "ai_weekly",
                "premium_lifetime"
            )
        )
    }
    
    suspend fun purchaseProduct(productId: String): BillingResult {
        return when (productId) {
            "premium_monthly" -> billingManager.launchBillingFlow(
                productId, 
                BillingClient.ProductType.SUBS
            )
            "premium_yearly" -> billingManager.launchBillingFlow(
                productId,
                BillingClient.ProductType.SUBS
            )
            "ai_weekly" -> billingManager.launchBillingFlow(
                productId,
                BillingClient.ProductType.INAPP
            )
            "premium_lifetime" -> billingManager.launchBillingFlow(
                productId,
                BillingClient.ProductType.INAPP
            )
            else -> BillingResult.Error("Geçersiz ürün ID")
        }
    }
}
```

### 2. **PaywallScreen Güncellemesi**

```kotlin
// feature/paywall/src/main/java/com/alperen/spendcraft/feature/paywall/PaywallScreen.kt

@Composable
fun PaywallScreen(
    viewModel: PaywallViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
    onSuccess: () -> Unit
) {
    val products by viewModel.products.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    
    // Ürün fiyatlarını göster
    products.forEach { product ->
        when (product.productId) {
            "premium_monthly" -> {
                // Aylık abonelik UI
                MonthlySubscriptionCard(
                    price = product.oneTimePurchaseOfferDetails?.formattedPrice ?: "₺29,99",
                    onPurchase = { viewModel.purchaseProduct("premium_monthly") }
                )
            }
            "premium_yearly" -> {
                // Yıllık abonelik UI  
                YearlySubscriptionCard(
                    price = product.oneTimePurchaseOfferDetails?.formattedPrice ?: "₺299,99",
                    onPurchase = { viewModel.purchaseProduct("premium_yearly") }
                )
            }
            "ai_weekly" -> {
                // AI haftalık paket UI
                AIWeeklyCard(
                    price = product.oneTimePurchaseOfferDetails?.formattedPrice ?: "₺9,99",
                    onPurchase = { viewModel.purchaseProduct("ai_weekly") }
                )
            }
        }
    }
}
```

## 🧪 Test Ayarları

### 1. **Test Hesapları**

Play Console > Setup > License testing > Test accounts:
```
test1@spendcraft.com
test2@spendcraft.com
test3@spendcraft.com
```

### 2. **Test Kartları**

```
Visa: 4111 1111 1111 1111
Mastercard: 5555 5555 5555 4444
CVV: 123
Son Kullanma: 12/25
```

### 3. **Internal Testing**

1. Play Console > Testing > Internal testing
2. Release oluştur
3. Test hesaplarını ekle
4. Test linkini paylaş

## 🔐 Güvenlik Kontrolleri

### 1. **Server-Side Validation**

```kotlin
// Gelecekte implement edilecek
class PurchaseValidator {
    suspend fun validatePurchase(
        purchaseToken: String,
        productId: String
    ): Boolean {
        // Google Play Developer API ile doğrulama
        return true
    }
}
```

### 2. **Purchase Acknowledgment**

```kotlin
// BillingManager.kt
suspend fun acknowledgePurchase(purchaseToken: String) {
    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
        .setPurchaseToken(purchaseToken)
        .build()
    
    billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            // Purchase acknowledged
        }
    }
}
```

## 📊 Analytics Events

```kotlin
// Firebase Analytics events
analyticsService.logEvent("purchase_started", mapOf(
    "product_id" to productId,
    "product_type" to productType
))

analyticsService.logEvent("purchase_completed", mapOf(
    "product_id" to productId,
    "revenue" to price,
    "currency" to "TRY"
))

analyticsService.logEvent("purchase_failed", mapOf(
    "product_id" to productId,
    "error_code" to errorCode
))
```

## 🚀 Release Checklist

- [ ] Tüm ürünler Play Console'da oluşturuldu
- [ ] Base plan'lar ayarlandı
- [ ] Offer'lar oluşturuldu
- [ ] Test hesapları eklendi
- [ ] Internal testing yapıldı
- [ ] Purchase flow test edildi
- [ ] Analytics events eklendi
- [ ] Error handling test edildi
- [ ] Premium gating test edildi
- [ ] Reklam kaldırma test edildi
