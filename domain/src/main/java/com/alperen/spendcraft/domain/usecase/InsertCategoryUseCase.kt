package com.alperen.spendcraft.domain.usecase

import com.alperen.spendcraft.core.model.Category
import com.alperen.spendcraft.domain.repo.TransactionsRepository
import javax.inject.Inject

class InsertCategoryUseCase @Inject constructor(
    private val repository: TransactionsRepository
) {
    suspend operator fun invoke(name: String): Long {
        val category = Category(
            id = null,
            name = name,
            color = "#FF5722", // Default color
            icon = "📂" // Default icon
        )
        return repository.insertCategory(category)
    }
}
