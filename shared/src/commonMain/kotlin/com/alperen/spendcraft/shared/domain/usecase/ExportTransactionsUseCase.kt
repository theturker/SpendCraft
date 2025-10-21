package com.alperen.spendcraft.shared.domain.usecase

import com.alperen.spendcraft.shared.domain.repository.TransactionsRepository
import com.alperen.spendcraft.shared.domain.util.CsvExporter

class ExportTransactionsUseCase(
    private val repository: TransactionsRepository,
    private val csvExporter: CsvExporter
) {
    suspend operator fun invoke(): String {
        val transactions = repository.getAllAscending()
        return csvExporter.exportTransactions(transactions)
    }
}


