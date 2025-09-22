package com.alperen.spendcraft.domain.usecase

import com.alperen.spendcraft.core.model.Account
import com.alperen.spendcraft.domain.repo.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAccountsUseCase @Inject constructor(
    private val repository: TransactionsRepository
) {
    operator fun invoke(): Flow<List<Account>> = repository.observeAccounts()
}
