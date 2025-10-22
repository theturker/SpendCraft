package com.alperen.spendcraft.shared.domain.validation

import com.alperen.spendcraft.shared.domain.model.Budget

/**
 * Budget validation rules
 * Combined from Android and iOS patterns
 */
object BudgetValidator {
    
    private const val MIN_BUDGET = 100L // 1 TRY minimum
    private const val MAX_BUDGET = 1_000_000_00L // 10 Million TRY maximum
    
    /**
     * Validate budget
     */
    fun validate(budget: Budget): ValidationResult {
        return when {
            budget.categoryId.isBlank() -> 
                ValidationResult.Invalid("Kategori seçilmelidir")
            
            budget.monthlyLimitMinor <= MIN_BUDGET -> 
                ValidationResult.Invalid("Bütçe limiti en az 1 ₺ olmalıdır")
            
            budget.monthlyLimitMinor > MAX_BUDGET -> 
                ValidationResult.Invalid("Bütçe limiti çok yüksek (maksimum: 10.000.000 ₺)")
            
            else -> ValidationResult.Valid
        }
    }
    
    /**
     * Calculate budget breach level
     * Used for notifications and UI colors
     */
    fun calculateBreachLevel(spent: Long, limit: Long): BreachLevel {
        if (limit <= 0) return BreachLevel.Safe
        
        val percentage = (spent.toDouble() / limit * 100)
        
        return when {
            percentage < 50 -> BreachLevel.Safe      // 0-50%: Güvenli (yeşil)
            percentage < 80 -> BreachLevel.Warning   // 50-80%: Dikkat (sarı)
            percentage < 100 -> BreachLevel.Critical // 80-100%: Kritik (turuncu)
            else -> BreachLevel.Exceeded             // >100%: Aşıldı (kırmızı)
        }
    }
    
    /**
     * Check if alert should be sent
     * iOS/Android pattern: Alert only for critical levels
     */
    fun shouldSendAlert(
        breachLevel: BreachLevel,
        lastAlertSentMillis: Long
    ): Boolean {
        // Only send alerts for critical and exceeded
        if (breachLevel !in listOf(BreachLevel.Critical, BreachLevel.Exceeded)) {
            return false
        }
        
        // Don't spam: minimum 24 hours between alerts
        val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        val hoursSinceLastAlert = (now - lastAlertSentMillis) / 3_600_000
        
        return hoursSinceLastAlert >= 24
    }
    
    /**
     * Get alert message for breach level
     */
    fun getAlertMessage(
        categoryName: String,
        spent: Long,
        limit: Long,
        breachLevel: BreachLevel
    ): String {
        val percentage = if (limit > 0) (spent.toDouble() / limit * 100).toInt() else 0
        
        return when (breachLevel) {
            BreachLevel.Warning -> 
                "$categoryName kategorisi bütçenizin %$percentage'sine ulaştı!"
            
            BreachLevel.Critical -> 
                "⚠️ $categoryName kategorisi bütçenizin %$percentage'sine ulaştı! Dikkatli olun."
            
            BreachLevel.Exceeded -> 
                "🚨 $categoryName kategorisi bütçenizi %$percentage oranında aştı!"
            
            BreachLevel.Safe -> 
                "$categoryName kategorisi bütçeniz güvenli (%$percentage kullanıldı)"
        }
    }
    
    enum class BreachLevel {
        Safe,       // 0-50%: Güvenli
        Warning,    // 50-80%: Dikkat
        Critical,   // 80-100%: Kritik
        Exceeded    // >100%: Aşıldı
    }
}




