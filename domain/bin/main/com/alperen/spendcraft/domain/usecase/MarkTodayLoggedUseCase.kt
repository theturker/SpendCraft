package com.alperen.spendcraft.domain.usecase

import com.alperen.spendcraft.domain.repo.StreakRepository
import javax.inject.Inject

class MarkTodayLoggedUseCase @Inject constructor(
    private val repository: StreakRepository
) {
    suspend operator fun invoke() {
        repository.markTodayLogged()
    }
}

