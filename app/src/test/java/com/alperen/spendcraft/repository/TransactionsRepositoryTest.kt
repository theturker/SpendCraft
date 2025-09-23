package com.alperen.spendcraft.repository

import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.domain.repo.TransactionsRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

class TransactionsRepositoryTest {

    // This would be an in-memory database for testing
    // In a real implementation, you'd use Room's in-memory database
    private lateinit var repository: TransactionsRepository

    @Test
    fun `insertTransaction should add transaction to database`() = runTest {
        // Given
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
        repository.insertTransaction(transaction)

        // Then
        val transactions = repository.observeTransactions().first()
        assertTrue(transactions.isNotEmpty())
        assertEquals(transaction.description, transactions.first().description)
    }

    @Test
    fun `deleteTransaction should remove transaction from database`() = runTest {
        // Given
        val transaction = Transaction(
            id = null,
            amountMinor = 1000L,
            description = "Test transaction",
            categoryId = "Food",
            timestampUtcMillis = Instant.now().toEpochMilli(),
            isIncome = false,
            accountId = null
        )
        repository.insertTransaction(transaction)
        val insertedTransaction = repository.observeTransactions().first().first()

        // When
        repository.deleteTransaction(insertedTransaction.id!!)

        // Then
        val transactions = repository.observeTransactions().first()
        assertTrue(transactions.isEmpty())
    }

    @Test
    fun `observeTransactions should emit updated list when transactions change`() = runTest {
        // Given
        val transaction1 = Transaction(
            id = null,
            amountMinor = 1000L,
            description = "Transaction 1",
            categoryId = "Food",
            timestampUtcMillis = Instant.now().toEpochMilli(),
            isIncome = false,
            accountId = null
        )

        val transaction2 = Transaction(
            id = null,
            amountMinor = 2000L,
            description = "Transaction 2",
            categoryId = "Transport",
            timestampUtcMillis = Instant.now().toEpochMilli(),
            isIncome = false,
            accountId = null
        )

        // When
        repository.insertTransaction(transaction1)
        var transactions = repository.observeTransactions().first()
        assertEquals(1, transactions.size)

        repository.insertTransaction(transaction2)
        transactions = repository.observeTransactions().first()
        assertEquals(2, transactions.size)
    }

    @Test
    fun `getTransactionsByAccount should return only transactions for specific account`() = runTest {
        // Given
        val accountId = 1L
        val transaction1 = Transaction(
            id = null,
            amountMinor = 1000L,
            description = "Account 1 transaction",
            categoryId = "Food",
            timestampUtcMillis = Instant.now().toEpochMilli(),
            isIncome = false,
            accountId = accountId
        )

        val transaction2 = Transaction(
            id = null,
            amountMinor = 2000L,
            description = "Account 2 transaction",
            categoryId = "Transport",
            timestampUtcMillis = Instant.now().toEpochMilli(),
            isIncome = false,
            accountId = 2L
        )

        repository.insertTransaction(transaction1)
        repository.insertTransaction(transaction2)

        // When
        val accountTransactions = repository.observeTransactionsByAccount(accountId).first()

        // Then
        assertEquals(1, accountTransactions.size)
        assertEquals(accountId, accountTransactions.first().accountId)
    }
}
