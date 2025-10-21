package com.alperen.spendcraft.shared.domain.usecase

import com.alperen.spendcraft.shared.domain.repository.TransactionsRepository

class DeleteAccountUseCase(
    private val repository: TransactionsRepository
) {
    suspend operator fun invoke(accountId: Long) {
        repository.deleteAccount(accountId)
    }
}


