package com.alperen.spendcraft.domain.usecase

import com.alperen.spendcraft.core.model.Budget
import com.alperen.spendcraft.domain.repo.BudgetRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveBudgetsUseCase @Inject constructor(
    private val repository: BudgetRepository
) {
    operator fun invoke(): Flow<List<Budget>> = repository.observeBudgets()
}

