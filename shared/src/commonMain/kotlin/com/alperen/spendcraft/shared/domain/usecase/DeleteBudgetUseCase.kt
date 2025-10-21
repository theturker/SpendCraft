package com.alperen.spendcraft.shared.domain.usecase

import com.alperen.spendcraft.shared.domain.repository.BudgetRepository

class DeleteBudgetUseCase(
    private val budgetRepository: BudgetRepository
) {
    suspend operator fun invoke(categoryId: String) = budgetRepository.deleteBudget(categoryId)
}


