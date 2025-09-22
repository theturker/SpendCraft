package com.alperen.spendcraft.domain.usecase

import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.domain.repo.TransactionsRepository
import javax.inject.Inject

class UpsertTransactionUseCase @Inject constructor(
    private val repo: TransactionsRepository
) {
    suspend operator fun invoke(tx: Transaction) = repo.upsert(tx)
}




