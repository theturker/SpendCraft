package com.alperen.spendcraft.shared.domain.usecase

import com.alperen.spendcraft.shared.domain.repository.TransactionsRepository
import com.alperen.spendcraft.shared.domain.util.CsvParser

class ImportTransactionsUseCase(
    private val repository: TransactionsRepository,
    private val csvParser: CsvParser
) {
    suspend operator fun invoke(csvContent: String): ImportResult {
        return try {
            val csvTransactions = csvParser.parseTransactions(csvContent)
            if (csvTransactions.isEmpty()) {
                ImportResult.Error("No valid transactions found in CSV")
            } else {
                val transactions = csvTransactions.map { it.toTransaction() }
                transactions.forEach { transaction ->
                    repository.upsert(transaction)
                }
                ImportResult.Success(csvTransactions.size)
            }
        } catch (e: Exception) {
            ImportResult.Error("Failed to import transactions: ${e.message}")
        }
    }
}

sealed class ImportResult {
    data class Success(val count: Int) : ImportResult()
    data class Error(val message: String) : ImportResult()
}


