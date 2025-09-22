package com.alperen.spendcraft.domain.usecase

import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.domain.repo.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTransactionsUseCase @Inject constructor(
    private val repo: TransactionsRepository
) {
    operator fun invoke(): Flow<List<Transaction>> = repo.observeTransactions()
}




