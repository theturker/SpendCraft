package com.alperen.spendcraft.shared.domain.usecase

import com.alperen.spendcraft.shared.domain.model.Account
import com.alperen.spendcraft.shared.domain.repository.TransactionsRepository

class InsertAccountUseCase(
    private val repository: TransactionsRepository
) {
    suspend operator fun invoke(account: Account): Long {
        return repository.insertAccount(account)
    }
}


