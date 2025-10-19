package com.alperen.spendcraft.domain.usecase

import com.alperen.spendcraft.domain.repo.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * iOS pattern: Reactive spent amounts by category
 * Returns Flow for automatic updates when transactions change
 */
class GetSpentAmountsUseCase @Inject constructor(
    private val transactionsRepository: TransactionsRepository
) {
    operator fun invoke(): Flow<Map<String, Long>> {
        return transactionsRepository.observeSpentAmountsByCategory()
    }
}

