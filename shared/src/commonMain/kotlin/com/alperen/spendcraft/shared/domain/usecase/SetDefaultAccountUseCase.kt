package com.alperen.spendcraft.shared.domain.usecase

import com.alperen.spendcraft.shared.domain.repository.TransactionsRepository

class SetDefaultAccountUseCase(
    private val repository: TransactionsRepository
) {
    suspend operator fun invoke(accountId: Long) {
        // Get current default account
        val default = repository.getDefaultAccount()
        if (default?.id == accountId) return
        
        // Set new default account
        // Note: Implementation depends on repository support for isDefault flag
    }
}


