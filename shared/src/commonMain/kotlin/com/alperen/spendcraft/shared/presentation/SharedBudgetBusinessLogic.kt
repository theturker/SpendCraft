package com.alperen.spendcraft.shared.presentation

import com.alperen.spendcraft.shared.domain.model.Budget
import com.alperen.spendcraft.shared.domain.calculator.BudgetCalculator
import com.alperen.spendcraft.shared.domain.validation.BudgetValidator
import com.alperen.spendcraft.shared.domain.validation.ValidationResult
import kotlinx.datetime.Clock

/**
 * Shared budget business logic
 * iOS/Android pattern: Encapsulates budget-related business rules
 */
class SharedBudgetBusinessLogic {
    
    /**
     * Analyze budget
     */
    fun analyzeBudget(budget: Budget, spent: Long, timestampMillis: Long = Clock.System.now().toEpochMilliseconds()): BudgetCalculator.BudgetAnalysis {
        return BudgetCalculator.analyze(budget, spent, timestampMillis)
    }
    
    /**
     * Validate budget
     */
    fun validateBudget(budget: Budget): ValidationResult {
        return BudgetValidator.validate(budget)
    }
    
    /**
     * Calculate daily allowance
     */
    fun calculateDailyAllowance(budgetLimit: Long, spent: Long, daysRemaining: Int): Long {
        return BudgetCalculator.calculateDailyAllowance(budgetLimit, spent, daysRemaining)
    }
    
    /**
     * Get breach level
     */
    fun getBreachLevel(percentage: Double): BudgetValidator.BreachLevel {
        return when {
            percentage >= 100.0 -> BudgetValidator.BreachLevel.Critical
            percentage >= 80.0 -> BudgetValidator.BreachLevel.Exceeded
            percentage >= 60.0 -> BudgetValidator.BreachLevel.Warning
            else -> BudgetValidator.BreachLevel.Safe
        }
    }
}