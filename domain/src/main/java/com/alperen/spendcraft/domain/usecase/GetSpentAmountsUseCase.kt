package com.alperen.spendcraft.domain.usecase

import com.alperen.spendcraft.domain.repo.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSpentAmountsUseCase @Inject constructor(
    private val transactionsRepository: TransactionsRepository
) {
    suspend operator fun invoke(): Map<String, Long> {
        return transactionsRepository.getSpentAmountsByCategory()
    }
}
