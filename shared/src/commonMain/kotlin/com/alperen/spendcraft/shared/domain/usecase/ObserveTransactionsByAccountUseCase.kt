package com.alperen.spendcraft.shared.domain.usecase

import com.alperen.spendcraft.shared.domain.model.Transaction
import com.alperen.spendcraft.shared.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow

class ObserveTransactionsByAccountUseCase(
    private val repository: TransactionsRepository
) {
    operator fun invoke(accountId: Long): Flow<List<Transaction>> = 
        repository.observeTransactionsByAccount(accountId)
}


