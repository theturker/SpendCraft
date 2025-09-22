package com.alperen.spendcraft.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.alperen.spendcraft.data.db.dao.AccountDao
import com.alperen.spendcraft.data.db.dao.BudgetAlertDao
import com.alperen.spendcraft.data.db.dao.BudgetDao
import com.alperen.spendcraft.data.db.dao.CategoryDao
import com.alperen.spendcraft.data.db.dao.DailyEntryDao
import com.alperen.spendcraft.data.db.dao.TxDao
import com.alperen.spendcraft.data.db.entities.AccountEntity
import com.alperen.spendcraft.data.db.entities.BudgetAlertEntity
import com.alperen.spendcraft.data.db.entities.BudgetEntity
import com.alperen.spendcraft.data.db.entities.CategoryEntity
import com.alperen.spendcraft.data.db.entities.DailyEntryEntity
import com.alperen.spendcraft.data.db.entities.TransactionEntity

@Database(
    entities = [TransactionEntity::class, CategoryEntity::class, AccountEntity::class, DailyEntryEntity::class, BudgetEntity::class, BudgetAlertEntity::class],
    version = 4,
    exportSchema = true
)
@TypeConverters()
abstract class AppDatabase : RoomDatabase() {
    abstract fun txDao(): TxDao
    abstract fun categoryDao(): CategoryDao
    abstract fun accountDao(): AccountDao
    abstract fun dailyEntryDao(): DailyEntryDao
    abstract fun budgetDao(): BudgetDao
    abstract fun budgetAlertDao(): BudgetAlertDao
}




