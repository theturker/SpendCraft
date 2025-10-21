package com.alperen.spendcraft.shared.domain.usecase

import com.alperen.spendcraft.shared.domain.repository.TransactionsRepository
import kotlinx.coroutines.flow.Flow

/**
 * iOS pattern: Reactive spent amounts by category
 * Returns Flow for automatic updates when transactions change
 */
class GetSpentAmountsUseCase(
    private val transactionsRepository: TransactionsRepository
) {
    operator fun invoke(): Flow<Map<String, Long>> {
        return transactionsRepository.observeSpentAmountsByCategory()
    }
}


