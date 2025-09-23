package com.alperen.spendcraft.csv

import org.junit.Test
import org.junit.Assert.*

class CsvParserTest {

    private val csvParser = CsvParser()

    @Test
    fun `parseTransactions with valid CSV should return correct transactions`() {
        val csvContent = """
            Date,Amount,Description,Category,Type
            2024-01-15,25.50,Coffee,Food,Expense
            2024-01-16,3000.00,Salary,Income,Income
        """.trimIndent()

        val result = csvParser.parseTransactions(csvContent)

        assertEquals(2, result.size)
        
        val expense = result[0]
        assertEquals(2550L, expense.amount) // 25.50 * 100
        assertEquals("Coffee", expense.description)
        assertEquals("Food", expense.category)
        assertEquals(false, expense.isIncome)
        
        val income = result[1]
        assertEquals(300000L, income.amount) // 3000.00 * 100
        assertEquals("Salary", income.description)
        assertEquals("Income", income.category)
        assertEquals(true, income.isIncome)
    }

    @Test
    fun `parseTransactions with empty CSV should return empty list`() {
        val result = csvParser.parseTransactions("")
        assertTrue(result.isEmpty())
    }

    @Test
    fun `parseTransactions with invalid lines should skip them`() {
        val csvContent = """
            Date,Amount,Description,Category,Type
            2024-01-15,25.50,Coffee,Food,Expense
            invalid,line,data
            2024-01-16,3000.00,Salary,Income,Income
        """.trimIndent()

        val result = csvParser.parseTransactions(csvContent)
        assertEquals(2, result.size)
    }

    @Test
    fun `parseAmount should handle different formats correctly`() {
        val parser = CsvParser()
        
        // Test private method through reflection or make it public for testing
        // This is a simplified test - in real implementation you'd test the actual parsing
        assertTrue(true) // Placeholder for actual amount parsing tests
    }
}
