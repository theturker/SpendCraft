package com.alperen.spendcraft.shared.di

import com.alperen.spendcraft.shared.data.DatabaseDriverFactory
import com.alperen.spendcraft.shared.data.repository.TransactionsRepositoryImpl
import com.alperen.spendcraft.shared.database.SpendCraftDatabase
import com.alperen.spendcraft.shared.domain.repository.TransactionsRepository
import com.alperen.spendcraft.shared.domain.usecase.*
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun platformModule(): Module

val sharedModule = module {
    // Database
    single { 
        val driver = get<DatabaseDriverFactory>().createDriver()
        SpendCraftDatabase(driver)
    }
    
    // Repositories
    single<TransactionsRepository> { TransactionsRepositoryImpl(get()) }
    
    // Use Cases
    factory { ObserveTransactionsUseCase(get()) }
    factory { UpsertTransactionUseCase(get()) }
    factory { DeleteTransactionUseCase(get()) }
    factory { ObserveCategoriesUseCase(get()) }
    factory { InsertCategoryUseCase(get()) }
}

fun getSharedModules() = listOf(sharedModule, platformModule())

