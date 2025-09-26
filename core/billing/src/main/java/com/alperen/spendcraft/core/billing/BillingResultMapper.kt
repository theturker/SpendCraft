package com.alperen.spendcraft.core.billing

import com.android.billingclient.api.BillingClient

/**
 * Billing hatalarını kullanıcı dostu mesajlara dönüştürür
 */
object BillingResultMapper {
    
    fun mapErrorCode(responseCode: Int): String {
        return when (responseCode) {
            BillingClient.BillingResponseCode.OK -> "İşlem başarılı"
            BillingClient.BillingResponseCode.USER_CANCELED -> "Ödeme iptal edildi"
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> "Hizmet geçici olarak kullanılamıyor. Lütfen tekrar deneyin"
            BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> "Ödeme hizmeti kullanılamıyor"
            BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> "Ürün mevcut değil"
            BillingClient.BillingResponseCode.DEVELOPER_ERROR -> "Geliştirici hatası"
            BillingClient.BillingResponseCode.ERROR -> "Bilinmeyen hata oluştu"
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> "Zaten Premium'sun"
            BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> "Bu ürün satın alınmamış"
            BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> "Bu özellik desteklenmiyor"
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> "Bağlantı kesildi"
            BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> "Bağlantı zaman aşımı"
            BillingClient.BillingResponseCode.NETWORK_ERROR -> "Ağ hatası. İnternet bağlantınızı kontrol edin"
            else -> "Bilinmeyen hata: $responseCode"
        }
    }
    
    fun isRetryableError(responseCode: Int): Boolean {
        return when (responseCode) {
            BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE,
            BillingClient.BillingResponseCode.SERVICE_DISCONNECTED,
            BillingClient.BillingResponseCode.SERVICE_TIMEOUT,
            BillingClient.BillingResponseCode.NETWORK_ERROR -> true
            else -> false
        }
    }
    
    fun getAnalyticsEventName(responseCode: Int): String {
        return when (responseCode) {
            BillingClient.BillingResponseCode.OK -> "purchase_success"
            BillingClient.BillingResponseCode.USER_CANCELED -> "purchase_cancelled"
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> "purchase_already_owned"
            else -> "purchase_failed_$responseCode"
        }
    }
}
