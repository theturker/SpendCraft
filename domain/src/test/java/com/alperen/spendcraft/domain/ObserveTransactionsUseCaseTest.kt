package com.alperen.spendcraft.domain

import com.alperen.spendcraft.core.model.Money
import com.alperen.spendcraft.core.model.Transaction
import com.alperen.spendcraft.core.model.TransactionType
import com.alperen.spendcraft.domain.repo.TransactionsRepository
import com.alperen.spendcraft.domain.usecase.ObserveTransactionsUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Test

class FakeRepo : TransactionsRepository {
    private val flow = MutableStateFlow<List<Transaction>>(emptyList())
    override fun observeTransactions(): Flow<List<Transaction>> = flow
    override fun observeCategories() = MutableStateFlow(emptyList<com.alperen.spendcraft.core.model.Category>())
    override suspend fun upsert(transaction: Transaction) {
        val current = flow.value.toMutableList()
        current.add(transaction.copy(id = (current.size + 1).toLong()))
        flow.value = current
    }
    override suspend fun delete(transactionId: Long) {
        flow.value = flow.value.filterNot { it.id == transactionId }
    }
}

class ObserveTransactionsUseCaseTest {
    @Test
    fun emitsItems() {
        val repo = FakeRepo()
        val useCase = ObserveTransactionsUseCase(repo)
        val tx = Transaction(
            id = null,
            amount = Money(1000),
            timestampUtcMillis = 0L,
            note = "Test",
            categoryId = null,
            type = TransactionType.EXPENSE
        )
        kotlinx.coroutines.runBlocking {
            repo.upsert(tx)
            val list = useCase()
            var result: List<Transaction>? = null
            list.collect {
                result = it
            }
            assertEquals(1, result?.size)
            assertEquals(1000, result?.first()?.amount?.minorUnits)
        }
    }
}




