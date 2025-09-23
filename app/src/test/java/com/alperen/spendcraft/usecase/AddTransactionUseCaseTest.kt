package com.alperen.spendcraft.usecase

import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.domain.repo.TransactionsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.*
import java.time.Instant

class AddTransactionUseCaseTest {

    @Test
    fun `invoke should call repository insertTransaction`() = runTest {
        // Given
        val mockRepository = mock(TransactionsRepository::class.java)
        val useCase = AddTransactionUseCase(mockRepository)
        
        val transaction = Transaction(
            id = null,
            amountMinor = 1000L,
            description = "Test transaction",
            categoryId = "Food",
            timestampUtcMillis = Instant.now().toEpochMilli(),
            isIncome = false,
            accountId = null
        )

        // When
        useCase(transaction)

        // Then
        verify(mockRepository).insertTransaction(transaction)
    }

    @Test
    fun `invoke with parameters should create and insert transaction`() = runTest {
        // Given
        val mockRepository = mock(TransactionsRepository::class.java)
        val useCase = AddTransactionUseCase(mockRepository)
        
        val amountMinor = 1000L
        val description = "Test transaction"
        val categoryId = "Food"
        val isIncome = false
        val accountId = 1L

        // When
        useCase(amountMinor, description, categoryId, isIncome, accountId)

        // Then
        verify(mockRepository).insertTransaction(
            argThat { transaction ->
                transaction.amountMinor == amountMinor &&
                transaction.description == description &&
                transaction.categoryId == categoryId &&
                transaction.isIncome == isIncome &&
                transaction.accountId == accountId
            }
        )
    }
}
