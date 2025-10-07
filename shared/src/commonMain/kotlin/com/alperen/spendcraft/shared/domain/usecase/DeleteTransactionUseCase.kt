package com.alperen.spendcraft.shared.domain.usecase

import com.alperen.spendcraft.shared.domain.repository.TransactionsRepository

class DeleteTransactionUseCase(
    private val repository: TransactionsRepository
) {
    suspend operator fun invoke(transactionId: Long) {
        repository.delete(transactionId)
    }
}

