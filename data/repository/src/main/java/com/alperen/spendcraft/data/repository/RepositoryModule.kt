package com.alperen.spendcraft.data.repository

import com.alperen.spendcraft.data.db.dao.CategoryDao
import com.alperen.spendcraft.data.db.dao.TxDao
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
        categoryDao: CategoryDao
    ): TransactionsRepository = TransactionsRepositoryImpl(txDao, categoryDao)
}




