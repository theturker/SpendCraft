package com.alperen.spendcraft.database

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.alperen.spendcraft.data.db.AppDatabase
import com.alperen.spendcraft.data.db.DbModule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val TEST_DB = "migration-test"

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java,
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate1To2() {
        var db = helper.createDatabase(TEST_DB, 1).apply {
            // Insert some data in version 1
            execSQL("INSERT INTO transactions (amountMinor, description, categoryId, timestampUtcMillis, isIncome) VALUES (1000, 'Test', 'Food', 1640995200000, 0)")
        }
        db.close()

        // Re-open the database with version 2 and provide MIGRATION_1_2 as the migration process.
        db = helper.runMigrationsAndValidate(TEST_DB, 2, true, DbModule.MIGRATION_1_2)

        // Verify that the accounts table was created
        db.query("SELECT * FROM accounts").use { cursor ->
            assertTrue("Accounts table should exist", cursor.columnCount > 0)
        }

        // Verify that accountId was added to transactions
        db.query("SELECT accountId FROM transactions").use { cursor ->
            assertTrue("accountId column should exist", cursor.columnCount > 0)
        }
    }

    @Test
    fun migrate2To3() {
        var db = helper.createDatabase(TEST_DB, 2).apply {
            // Insert some data in version 2
            execSQL("INSERT INTO accounts (name, isDefault) VALUES ('Main Account', 1)")
        }
        db.close()

        // Re-open the database with version 3
        db = helper.runMigrationsAndValidate(TEST_DB, 3, true, DbModule.MIGRATION_2_3)

        // Verify that the daily_entry table was created
        db.query("SELECT * FROM daily_entry").use { cursor ->
            assertTrue("Daily entry table should exist", cursor.columnCount > 0)
        }
    }

    @Test
    fun migrate3To4() {
        var db = helper.createDatabase(TEST_DB, 3).apply {
            // Insert some data in version 3
            execSQL("INSERT INTO daily_entry (dateEpochDay) VALUES (19000)")
        }
        db.close()

        // Re-open the database with version 4
        db = helper.runMigrationsAndValidate(TEST_DB, 4, true, DbModule.MIGRATION_3_4)

        // Verify that budget tables were created
        db.query("SELECT * FROM budget").use { cursor ->
            assertTrue("Budget table should exist", cursor.columnCount > 0)
        }
        
        db.query("SELECT * FROM budget_alert").use { cursor ->
            assertTrue("Budget alert table should exist", cursor.columnCount > 0)
        }
    }

    @Test
    fun migrate4To5() {
        var db = helper.createDatabase(TEST_DB, 4).apply {
            // Insert some data in version 4
            execSQL("INSERT INTO budget (categoryId, monthlyLimitMinor) VALUES ('Food', 100000)")
        }
        db.close()

        // Re-open the database with version 5
        db = helper.runMigrationsAndValidate(TEST_DB, 5, true, DbModule.MIGRATION_4_5)

        // Verify that analytics_events table was created
        db.query("SELECT * FROM analytics_events").use { cursor ->
            assertTrue("Analytics events table should exist", cursor.columnCount > 0)
        }
    }

    @Test
    fun migrateAll() {
        // Test migration from version 1 to 5
        var db = helper.createDatabase(TEST_DB, 1).apply {
            execSQL("INSERT INTO transactions (amountMinor, description, categoryId, timestampUtcMillis, isIncome) VALUES (1000, 'Test', 'Food', 1640995200000, 0)")
        }
        db.close()

        // Run all migrations
        db = helper.runMigrationsAndValidate(
            TEST_DB, 5, true,
            DbModule.MIGRATION_1_2,
            DbModule.MIGRATION_2_3,
            DbModule.MIGRATION_3_4,
            DbModule.MIGRATION_4_5
        )

        // Verify all tables exist
        val tables = listOf("transactions", "categories", "accounts", "daily_entry", "budget", "budget_alert", "analytics_events")
        tables.forEach { tableName ->
            db.query("SELECT * FROM $tableName").use { cursor ->
                assertTrue("Table $tableName should exist", cursor.columnCount > 0)
            }
        }
    }
}
