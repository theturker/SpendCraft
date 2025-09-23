package com.alperen.spendcraft.domain.usecase

import com.alperen.spendcraft.domain.repo.BudgetRepository
import javax.inject.Inject

class DeleteBudgetUseCase @Inject constructor(
    private val budgetRepository: BudgetRepository
) {
    suspend operator fun invoke(categoryId: String) = budgetRepository.deleteBudget(categoryId)
}
