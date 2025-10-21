package com.alperen.spendcraft.shared.domain.usecase

import com.alperen.spendcraft.shared.domain.model.Budget
import com.alperen.spendcraft.shared.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow

class ObserveBudgetsUseCase(
    private val repository: BudgetRepository
) {
    operator fun invoke(): Flow<List<Budget>> = repository.observeBudgets()
}


