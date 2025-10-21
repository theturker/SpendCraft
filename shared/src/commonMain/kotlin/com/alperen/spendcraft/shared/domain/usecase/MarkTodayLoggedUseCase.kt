package com.alperen.spendcraft.shared.domain.usecase

import com.alperen.spendcraft.shared.domain.repository.StreakRepository

class MarkTodayLoggedUseCase(
    private val repository: StreakRepository
) {
    suspend operator fun invoke() {
        repository.markTodayLogged()
    }
}


