package com.alperen.spendcraft.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.alperen.spendcraft.data.db.dao.AccountDao
import com.alperen.spendcraft.data.db.dao.AchievementDao
import com.alperen.spendcraft.data.db.dao.AIUsageDao
import com.alperen.spendcraft.data.db.dao.BudgetAlertDao
import com.alperen.spendcraft.data.db.dao.BudgetDao
import com.alperen.spendcraft.data.db.dao.CategoryDao
import com.alperen.spendcraft.data.db.dao.DailyEntryDao
import com.alperen.spendcraft.data.db.dao.RecurringRuleDao
import com.alperen.spendcraft.data.db.dao.RecurringTransactionDao
import com.alperen.spendcraft.data.db.dao.SharingMemberDao
import com.alperen.spendcraft.data.db.dao.TxDao
import com.alperen.spendcraft.data.db.entities.AccountEntity
import com.alperen.spendcraft.data.db.entities.CategoryEntity
import com.alperen.spendcraft.data.db.migrations.MIGRATION_4_TO_5
import com.alperen.spendcraft.data.db.migrations.MIGRATION_7_8
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
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_TO_5, MIGRATION_7_8)
            .fallbackToDestructiveMigration()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                    super.onCreate(db)
                    val scope = CoroutineScope(Dispatchers.IO)
                    scope.launch {
                        val database = Room.databaseBuilder(context, AppDatabase::class.java, "spendcraft.db").build()
                        // Insert default account
                        database.accountDao().insertAll(
                            listOf(
                                AccountEntity(
                                    name = "Ana Hesap", 
                                    type = "CASH",
                                    currency = "TRY",
                                    isDefault = true
                                )
                            )
                        )
                        
                        // Insert default categories - iOS pattern: type-specific
                        // ✅ iOS CoreDataStack.swift:147-167 ile birebir aynı
                        database.categoryDao().insertAll(
                            listOf(
                                // Gider Kategorileri (iOS: Expense)
                                CategoryEntity(name = "Gıda", color = "#FF6347", icon = "fork.knife", isIncome = false),
                                CategoryEntity(name = "Ulaşım", color = "#4682B4", icon = "car.fill", isIncome = false),
                                CategoryEntity(name = "Fatura", color = "#DAA520", icon = "doc.text.fill", isIncome = false),
                                CategoryEntity(name = "Eğlence", color = "#9370DB", icon = "gamecontroller.fill", isIncome = false),
                                CategoryEntity(name = "Alışveriş", color = "#3CB371", icon = "cart.fill", isIncome = false),
                                CategoryEntity(name = "Sağlık", color = "#FF69B4", icon = "heart.fill", isIncome = false),
                                CategoryEntity(name = "Eğitim", color = "#8B4513", icon = "book.closed.fill", isIncome = false),
                                CategoryEntity(name = "Kredi", color = "#DC143C", icon = "creditcard.fill", isIncome = false),
                                CategoryEntity(name = "Diğer Gider", color = "#808080", icon = "ellipsis.circle.fill", isIncome = false),
                                
                                // Gelir Kategorileri (iOS: Income)
                                CategoryEntity(name = "Maaş", color = "#008000", icon = "banknote.fill", isIncome = true),
                                CategoryEntity(name = "Kira", color = "#32CD32", icon = "house.fill", isIncome = true),
                                CategoryEntity(name = "Prim", color = "#FFD700", icon = "star.fill", isIncome = true),
                                CategoryEntity(name = "Yatırım", color = "#4169E1", icon = "chart.line.uptrend.xyaxis", isIncome = true),
                                CategoryEntity(name = "İkramiye", color = "#FFA500", icon = "gift.fill", isIncome = true),
                                CategoryEntity(name = "Serbest Çalışma", color = "#9370DB", icon = "briefcase.fill", isIncome = true),
                                CategoryEntity(name = "Kira Geliri", color = "#20B2AA", icon = "building.2.fill", isIncome = true),
                                CategoryEntity(name = "Diğer Gelir", color = "#808080", icon = "ellipsis.circle.fill", isIncome = true)
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
    
    @Provides
    fun provideRecurringRuleDao(db: AppDatabase): RecurringRuleDao = db.recurringRuleDao()
    
    @Provides
    fun provideSharingMemberDao(db: AppDatabase): SharingMemberDao = db.sharingMemberDao()
    
    @Provides
    fun provideAIUsageDao(db: AppDatabase): AIUsageDao = db.aiUsageDao()
    
    @Provides
    fun provideAchievementDao(db: AppDatabase): AchievementDao = db.achievementDao()
    
    @Provides
    fun provideRecurringTransactionDao(db: AppDatabase): RecurringTransactionDao = db.recurringTransactionDao()
    
}




