package com.alperen.spendcraft.shared.domain.validation

import com.alperen.spendcraft.shared.domain.model.Transaction
import com.alperen.spendcraft.shared.domain.model.TransactionType

/**
 * Transaction validation rules
 * Combined from Android and iOS best practices
 */
object TransactionValidator {
    
    private const val MAX_NOTE_LENGTH = 500
    private const val MIN_AMOUNT = 0L
    private const val MAX_AMOUNT = 1_000_000_00L // 1 Million
    
    /**
     * Validate transaction
     * Returns ValidationResult with specific error message
     */
    fun validate(transaction: Transaction): ValidationResult {
        val validators = listOf(
            ::validateAmount,
            ::validateCategory,
            ::validateNote,
            ::validateTimestamp
        )
        
        validators.forEach { validator ->
            val result = validator(transaction)
            if (result is ValidationResult.Invalid) {
                return result
            }
        }
        
        return ValidationResult.Valid
    }
    
    /**
     * Validate all fields and return multiple results
     */
    fun validateAll(transaction: Transaction): ValidationResults {
        val results = listOf(
            validateAmount(transaction),
            validateCategory(transaction),
            validateNote(transaction),
            validateTimestamp(transaction)
        )
        
        return ValidationResults(results)
    }
    
    /**
     * Amount validation
     * - Must be > 0
     * - Must be <= MAX_AMOUNT
     */
    private fun validateAmount(transaction: Transaction): ValidationResult {
        return when {
            transaction.amount.minorUnits <= MIN_AMOUNT -> 
                ValidationResult.Invalid("Tutar sıfırdan büyük olmalıdır")
            
            transaction.amount.minorUnits > MAX_AMOUNT -> 
                ValidationResult.Invalid("Tutar çok yüksek (maksimum: 1.000.000 ₺)")
            
            else -> ValidationResult.Valid
        }
    }
    
    /**
     * Category validation
     * - EXPENSE transactions should have category (iOS/Android pattern)
     * - INCOME can be optional
     */
    private fun validateCategory(transaction: Transaction): ValidationResult {
        // iOS/Android pattern: Gider için kategori zorunlu
        if (transaction.type == TransactionType.EXPENSE && transaction.categoryId == null) {
            return ValidationResult.Invalid("Gider için kategori seçilmelidir")
        }
        
        return ValidationResult.Valid
    }
    
    /**
     * Note validation
     * - Optional field
     * - Max length: 500 characters
     */
    private fun validateNote(transaction: Transaction): ValidationResult {
        val note = transaction.note ?: return ValidationResult.Valid
        
        return when {
            note.length > MAX_NOTE_LENGTH -> 
                ValidationResult.Invalid("Not maksimum $MAX_NOTE_LENGTH karakter olabilir")
            
            else -> ValidationResult.Valid
        }
    }
    
    /**
     * Timestamp validation
     * - Should not be in future
     * - Should not be too old (> 10 years)
     */
    private fun validateTimestamp(transaction: Transaction): ValidationResult {
        val now = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
        val tenYearsAgo = now - (10L * 365 * 24 * 60 * 60 * 1000)
        
        return when {
            transaction.timestampUtcMillis > now + 86_400_000 -> // 1 day tolerance
                ValidationResult.Invalid("Tarih gelecekte olamaz")
            
            transaction.timestampUtcMillis < tenYearsAgo -> 
                ValidationResult.Invalid("Tarih çok eski (maksimum 10 yıl)")
            
            else -> ValidationResult.Valid
        }
    }
    
    /**
     * Quick validation (only critical fields)
     */
    fun validateQuick(transaction: Transaction): ValidationResult {
        return when {
            transaction.amount.minorUnits <= 0 -> 
                ValidationResult.Invalid("Tutar sıfırdan büyük olmalıdır")
            
            transaction.type == TransactionType.EXPENSE && transaction.categoryId == null -> 
                ValidationResult.Invalid("Kategori seçilmelidir")
            
            else -> ValidationResult.Valid
        }
    }
}




