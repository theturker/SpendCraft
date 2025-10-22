package com.alperen.spendcraft.shared.domain.validation

import com.alperen.spendcraft.shared.domain.model.Category

/**
 * Category validation rules
 */
object CategoryValidator {
    
    private const val MIN_NAME_LENGTH = 1
    private const val MAX_NAME_LENGTH = 30
    
    /**
     * Validate category
     */
    fun validate(category: Category): ValidationResult {
        return when {
            category.name.isBlank() -> 
                ValidationResult.Invalid("Kategori adı boş olamaz")
            
            category.name.length < MIN_NAME_LENGTH -> 
                ValidationResult.Invalid("Kategori adı en az $MIN_NAME_LENGTH karakter olmalıdır")
            
            category.name.length > MAX_NAME_LENGTH -> 
                ValidationResult.Invalid("Kategori adı maksimum $MAX_NAME_LENGTH karakter olabilir")
            
            category.color.isBlank() -> 
                ValidationResult.Invalid("Kategori rengi seçilmelidir")
            
            !isValidColorHex(category.color) -> 
                ValidationResult.Invalid("Geçersiz renk kodu. Format: #RRGGBB")
            
            else -> ValidationResult.Valid
        }
    }
    
    /**
     * Validate hex color format
     * Accepts: #RGB, #RRGGBB
     */
    private fun isValidColorHex(color: String): Boolean {
        if (!color.startsWith("#")) return false
        
        val hex = color.substring(1)
        return when (hex.length) {
            3, 6 -> hex.all { it.isDigit() || it.lowercaseChar() in 'a'..'f' }
            else -> false
        }
    }
    
    /**
     * Quick validation (name only)
     */
    fun validateQuick(name: String): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Invalid("Kategori adı boş olamaz")
            name.length > MAX_NAME_LENGTH -> ValidationResult.Invalid("Kategori adı çok uzun")
            else -> ValidationResult.Valid
        }
    }
}




