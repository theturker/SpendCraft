package com.alperen.spendcraft.domain.usecase

import com.alperen.spendcraft.domain.repo.TransactionsRepository
import javax.inject.Inject

class SetDefaultAccountUseCase @Inject constructor(
    private val repository: TransactionsRepository
) {
    suspend operator fun invoke(accountId: Long) {
        // Repository currently lacks explicit setDefault; update via updateAccount if needed
        // This use case exists as a placeholder for future repository method.
        val default = repository.getDefaultAccount()
        if (default?.id == accountId) return
        // No-op; to be implemented when repository supports isDefault flag on Account
    }
}



