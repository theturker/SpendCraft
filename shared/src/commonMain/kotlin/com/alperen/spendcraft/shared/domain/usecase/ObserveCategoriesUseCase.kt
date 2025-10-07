package com.alperen.spendcraft.shared.domain.usecase

import com.alperen.spendcraft.shared.domain.model.Category
import com.alperen.spendcraft.shared.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow

class ObserveCategoriesUseCase(
    private val repository: CategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> = repository.observeCategories()
}

