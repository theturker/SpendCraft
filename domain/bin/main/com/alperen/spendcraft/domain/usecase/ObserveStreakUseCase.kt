package com.alperen.spendcraft.domain.usecase

import com.alperen.spendcraft.core.model.Streak
import com.alperen.spendcraft.domain.repo.StreakRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveStreakUseCase @Inject constructor(
    private val repository: StreakRepository
) {
    operator fun invoke(): Flow<Streak> = repository.observeStreak()
}

