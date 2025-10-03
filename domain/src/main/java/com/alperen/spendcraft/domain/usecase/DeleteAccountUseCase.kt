package com.alperen.spendcraft.domain.usecase

import com.alperen.spendcraft.domain.repo.TransactionsRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val repository: TransactionsRepository
) {
    suspend operator fun invoke(accountId: Long) {
        repository.deleteAccount(accountId)
    }
}


