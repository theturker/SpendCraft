package com.alperen.spendcraft.domain.usecase

import com.alperen.spendcraft.domain.repo.TransactionsRepository
import com.alperen.spendcraft.domain.csv.CsvExporter
import javax.inject.Inject

class ExportTransactionsUseCase @Inject constructor(
    private val repository: TransactionsRepository,
    private val csvExporter: CsvExporter
) {
    suspend operator fun invoke(): String {
        val transactions = repository.getAllAscending()
        return csvExporter.exportTransactions(transactions)
    }
}
