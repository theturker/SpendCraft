package com.alperen.spendcraft.domain.usecase

import com.alperen.spendcraft.core.model.Category
import com.alperen.spendcraft.domain.repo.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCategoriesUseCase @Inject constructor(
    private val repo: TransactionsRepository
) {
    operator fun invoke(): Flow<List<Category>> = repo.observeCategories()
}




