package com.alperen.spendcraft.domain.usecase

import com.alperen.spendcraft.core.model.Account
import com.alperen.spendcraft.domain.repo.TransactionsRepository
import javax.inject.Inject

class UpdateAccountUseCase @Inject constructor(
    private val repository: TransactionsRepository
) {
    suspend operator fun invoke(account: Account) {
        repository.updateAccount(account)
    }
}
