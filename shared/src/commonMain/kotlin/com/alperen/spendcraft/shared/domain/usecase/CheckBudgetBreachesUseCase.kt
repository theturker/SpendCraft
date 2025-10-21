package com.alperen.spendcraft.shared.domain.usecase

import com.alperen.spendcraft.shared.domain.repository.BudgetRepository

class CheckBudgetBreachesUseCase(
    private val repository: BudgetRepository
) {
    suspend operator fun invoke(): List<String> = repository.checkBudgetBreaches()
}


