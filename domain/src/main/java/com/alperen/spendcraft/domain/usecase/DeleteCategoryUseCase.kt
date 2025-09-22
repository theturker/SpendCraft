package com.alperen.spendcraft.domain.usecase

import com.alperen.spendcraft.domain.repo.TransactionsRepository
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
    private val repository: TransactionsRepository
) {
    suspend operator fun invoke(categoryId: Long) {
        repository.deleteCategory(categoryId)
    }
}
