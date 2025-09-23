package com.alperen.spendcraft.domain.usecase

import com.alperen.spendcraft.domain.repo.BudgetRepository
import javax.inject.Inject

class CheckBudgetBreachesUseCase @Inject constructor(
    private val repository: BudgetRepository
) {
    suspend operator fun invoke(): List<String> = repository.checkBudgetBreaches()
}

