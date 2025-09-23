package com.alperen.spendcraft.data.repository

import com.alperen.spendcraft.data.db.dao.AccountDao
import com.alperen.spendcraft.data.db.dao.BudgetAlertDao
import com.alperen.spendcraft.data.db.dao.BudgetDao
import com.alperen.spendcraft.data.db.dao.CategoryDao
import com.alperen.spendcraft.data.db.dao.DailyEntryDao
import com.alperen.spendcraft.data.db.dao.TxDao
import com.alperen.spendcraft.domain.repo.BudgetRepository
import com.alperen.spendcraft.domain.repo.StreakRepository
import com.alperen.spendcraft.domain.repo.TransactionsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideTransactionsRepository(
        txDao: TxDao,
        categoryDao: CategoryDao,
        accountDao: AccountDao
    ): TransactionsRepository = TransactionsRepositoryImpl(txDao, categoryDao, accountDao)
    
    @Provides
    @Singleton
    fun provideStreakRepository(
        dailyEntryDao: DailyEntryDao
    ): StreakRepository = StreakRepositoryImpl(dailyEntryDao)
    
    @Provides
    @Singleton
    fun provideBudgetRepository(
        budgetDao: BudgetDao,
        budgetAlertDao: BudgetAlertDao,
        txDao: TxDao,
        categoryDao: CategoryDao
    ): BudgetRepository = BudgetRepositoryImpl(budgetDao, budgetAlertDao, txDao, categoryDao)

}




