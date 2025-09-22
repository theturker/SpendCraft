package com.alperen.spendcraft.domain.usecase

import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.domain.repo.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTransactionsByAccountUseCase @Inject constructor(
    private val repository: TransactionsRepository
) {
    operator fun invoke(accountId: Long): Flow<List<Transaction>> = 
        repository.observeTransactionsByAccount(accountId)
}
