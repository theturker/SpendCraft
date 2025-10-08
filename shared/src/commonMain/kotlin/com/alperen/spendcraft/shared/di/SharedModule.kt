package com.alperen.spendcraft.shared.di

import com.alperen.spendcraft.shared.domain.usecase.*
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun platformModule(): Module

val sharedModule = module {
    // Use Cases
    // Repository her platform kendi module'ünde sağlayacak
    factory { ObserveTransactionsUseCase(get()) }
    factory { UpsertTransactionUseCase(get()) }
    factory { DeleteTransactionUseCase(get()) }
    factory { ObserveCategoriesUseCase(get()) }
    factory { InsertCategoryUseCase(get()) }
}

fun getSharedModules() = listOf(sharedModule, platformModule())

