package com.alperen.spendcraft.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.alperen.spendcraft.data.db.dao.AccountDao
import com.alperen.spendcraft.data.db.dao.BudgetAlertDao
import com.alperen.spendcraft.data.db.dao.BudgetDao
import com.alperen.spendcraft.data.db.dao.CategoryDao
import com.alperen.spendcraft.data.db.dao.DailyEntryDao
import com.alperen.spendcraft.data.db.dao.TxDao
import com.alperen.spendcraft.data.db.entities.AccountEntity
import com.alperen.spendcraft.data.db.entities.CategoryEntity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create accounts table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `accounts` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `name` TEXT NOT NULL,
                    `isDefault` INTEGER NOT NULL DEFAULT 0
                )
            """)
            
            // Insert default account
            database.execSQL("INSERT INTO accounts (name, isDefault) VALUES ('Ana Hesap', 1)")
            
            // Add accountId column to transactions table
            database.execSQL("ALTER TABLE transactions ADD COLUMN accountId INTEGER")
            
            // Create index for accountId
            database.execSQL("CREATE INDEX IF NOT EXISTS index_transactions_accountId ON transactions (accountId)")
            
            // Set all existing transactions to default account
            database.execSQL("UPDATE transactions SET accountId = 1")
        }
    }
    
    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create daily_entry table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `daily_entry` (
                    `dateEpochDay` INTEGER PRIMARY KEY NOT NULL
                )
            """)
        }
    }
    
    private val MIGRATION_3_4 = object : Migration(3, 4) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Create budget table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `budget` (
                    `categoryId` TEXT PRIMARY KEY NOT NULL,
                    `monthlyLimitMinor` INTEGER NOT NULL
                )
            """)
            
            // Create budget_alert table
            database.execSQL("""
                CREATE TABLE IF NOT EXISTS `budget_alert` (
                    `categoryId` TEXT NOT NULL,
                    `level` INTEGER NOT NULL,
                    `monthKey` TEXT NOT NULL,
                    PRIMARY KEY(`categoryId`, `level`, `monthKey`)
                )
            """)
        }
    }


    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "spendcraft.db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onCreate(db)
                    val scope = CoroutineScope(Dispatchers.IO)
                    scope.launch {
                        val database = Room.databaseBuilder(context, AppDatabase::class.java, "spendcraft.db").build()
                        // Insert default account
                        database.accountDao().insertAll(
                            listOf(
                                AccountEntity(name = "Ana Hesap", isDefault = true)
                            )
                        )
                        
                        // Insert default categories
                        database.categoryDao().insertAll(
                            listOf(
                                CategoryEntity(name = "Food", icon = "🍔"),
                                CategoryEntity(name = "Transport", icon = "🚌"),
                                CategoryEntity(name = "Entertainment", icon = "🎬"),
                                CategoryEntity(name = "Salary", icon = "💼"),
                                CategoryEntity(name = "Shopping", icon = "🛒")
                            )
                        )
                        database.close()
                    }
                }
            })
            .build()
    }

    @Provides
    fun provideTxDao(db: AppDatabase): TxDao = db.txDao()

    @Provides
    fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()
    
    @Provides
    fun provideAccountDao(db: AppDatabase): AccountDao = db.accountDao()
    
    @Provides
    fun provideDailyEntryDao(db: AppDatabase): DailyEntryDao = db.dailyEntryDao()
    
    @Provides
    fun provideBudgetDao(db: AppDatabase): BudgetDao = db.budgetDao()
    
    @Provides
    fun provideBudgetAlertDao(db: AppDatabase): BudgetAlertDao = db.budgetAlertDao()
    
}




