package com.alperen.spendcraft.domain.usecase

import com.alperen.spendcraft.core.model.Budget
import com.alperen.spendcraft.domain.repo.BudgetRepository
import javax.inject.Inject

class UpsertBudgetUseCase @Inject constructor(
    private val repository: BudgetRepository
) {
    suspend operator fun invoke(budget: Budget) {
        repository.upsertBudget(budget)
    }
}

