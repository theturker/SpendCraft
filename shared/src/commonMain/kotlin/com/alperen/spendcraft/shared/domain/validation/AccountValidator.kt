package com.alperen.spendcraft.shared.domain.validation

import com.alperen.spendcraft.shared.domain.model.Account

/**
 * Account validation rules
 */
object AccountValidator {
    
    private const val MIN_NAME_LENGTH = 1
    private const val MAX_NAME_LENGTH = 50
    
    private val VALID_TYPES = setOf("CASH", "BANK", "CREDIT_CARD", "SAVINGS")
    
    /**
     * Public validation methods for UI forms
     */
    fun validateName(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Invalid("Name cannot be blank")
            name.length < 2 -> ValidationResult.Invalid("Name must be at least 2 characters")
            name.length > 50 -> ValidationResult.Invalid("Name must be less than 50 characters")
            else -> ValidationResult.Valid
        }
    }
    
    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Invalid("Email cannot be blank")
            !email.contains("@") -> ValidationResult.Invalid("Email must contain @")
            email.length < 5 -> ValidationResult.Invalid("Email must be at least 5 characters")
            else -> ValidationResult.Valid
        }
    }
    
    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Invalid("Password cannot be blank")
            password.length < 6 -> ValidationResult.Invalid("Password must be at least 6 characters")
            password.length > 100 -> ValidationResult.Invalid("Password must be less than 100 characters")
            else -> ValidationResult.Valid
        }
    }
    
    /**
     * Validate account
     */
    fun validate(account: Account): ValidationResult {
        val validators = listOf(
            ::validateName,
            ::validateType,
            ::validateCurrency
        )
        
        validators.forEach { validator ->
            val result = validator(account)
            if (result is ValidationResult.Invalid) {
                return result
            }
        }
        
        return ValidationResult.Valid
    }
    
    /**
     * Name validation
     */
    private fun validateName(account: Account): ValidationResult {
        return when {
            account.name.isBlank() -> 
                ValidationResult.Invalid("Hesap adı boş olamaz")
            
            account.name.length < MIN_NAME_LENGTH -> 
                ValidationResult.Invalid("Hesap adı en az $MIN_NAME_LENGTH karakter olmalıdır")
            
            account.name.length > MAX_NAME_LENGTH -> 
                ValidationResult.Invalid("Hesap adı maksimum $MAX_NAME_LENGTH karakter olabilir")
            
            else -> ValidationResult.Valid
        }
    }
    
    /**
     * Type validation
     */
    private fun validateType(account: Account): ValidationResult {
        return when {
            account.type !in VALID_TYPES -> 
                ValidationResult.Invalid("Geçersiz hesap tipi. Geçerli tipler: ${VALID_TYPES.joinToString()}")
            
            else -> ValidationResult.Valid
        }
    }
    
    /**
     * Currency validation
     */
    private fun validateCurrency(account: Account): ValidationResult {
        // Check if currency is supported using CurrencyFormatter
        return when {
            account.currency.isBlank() -> 
                ValidationResult.Invalid("Para birimi seçilmelidir")
            
            !com.alperen.spendcraft.shared.domain.formatter.CurrencyFormatter.isSupported(account.currency) -> 
                ValidationResult.Invalid("Desteklenmeyen para birimi: ${account.currency}")
            
            else -> ValidationResult.Valid
        }
    }
}




