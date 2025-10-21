package com.alperen.spendcraft.shared.domain.usecase

import com.alperen.spendcraft.shared.domain.model.Account
import com.alperen.spendcraft.shared.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow

class ObserveAccountsUseCase(
    private val repository: TransactionsRepository
) {
    operator fun invoke(): Flow<List<Account>> = repository.observeAccounts()
}


