package com.alperen.spendcraft.shared.domain.usecase

import com.alperen.spendcraft.shared.domain.model.Streak
import com.alperen.spendcraft.shared.domain.repository.StreakRepository
import kotlinx.coroutines.flow.Flow

class ObserveStreakUseCase(
    private val repository: StreakRepository
) {
    operator fun invoke(): Flow<Streak> = repository.observeStreak()
}


