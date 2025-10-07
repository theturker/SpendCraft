package com.alperen.spendcraft.shared.domain.usecase

import com.alperen.spendcraft.shared.domain.model.Category
import com.alperen.spendcraft.shared.domain.repository.CategoryRepository

class InsertCategoryUseCase(
    private val repository: CategoryRepository
) {
    suspend operator fun invoke(name: String, color: String = "#FF5722", icon: String = "📂"): Long {
        val category = Category(
            id = null,
            name = name,
            color = color,
            icon = icon
        )
        return repository.insertCategory(category)
    }
}

