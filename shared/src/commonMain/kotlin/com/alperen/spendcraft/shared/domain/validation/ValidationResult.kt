package com.alperen.spendcraft.shared.domain.validation

/**
 * Validation result wrapper
 * Platform-agnostic validation response
 */
sealed class ValidationResult {
    object Valid : ValidationResult()
    data class Invalid(val message: String) : ValidationResult()
    
    val isValid: Boolean get() = this is Valid
    val errorMessage: String? get() = (this as? Invalid)?.message
    
    companion object {
        fun valid() = Valid
        fun invalid(message: String) = Invalid(message)
    }
}

/**
 * Multiple validation results
 */
data class ValidationResults(
    val results: List<ValidationResult>
) {
    val isValid: Boolean get() = results.all { it is ValidationResult.Valid }
    val errorMessages: List<String> get() = results.filterIsInstance<ValidationResult.Invalid>().map { it.message }
    val firstError: String? get() = errorMessages.firstOrNull()
}




