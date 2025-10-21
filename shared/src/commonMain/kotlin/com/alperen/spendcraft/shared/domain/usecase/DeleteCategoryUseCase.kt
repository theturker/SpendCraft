package com.alperen.spendcraft.shared.domain.usecase

import com.alperen.spendcraft.shared.domain.repository.TransactionsRepository

class DeleteCategoryUseCase(
    private val repository: TransactionsRepository
) {
    suspend operator fun invoke(categoryId: Long) {
        repository.deleteCategory(categoryId)
    }
}


