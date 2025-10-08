package com.alperen.spendcraft.shared.di

import com.alperen.spendcraft.shared.data.DatabaseDriverFactory
import com.alperen.spendcraft.shared.data.repository.IosTransactionsRepository
import com.alperen.spendcraft.shared.domain.repository.TransactionsRepository
import com.alperen.spendcraft.shared.platform.Analytics
import com.alperen.spendcraft.shared.platform.AnalyticsImpl
import com.alperen.spendcraft.shared.platform.Preferences
import com.alperen.spendcraft.shared.platform.PreferencesImpl
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { DatabaseDriverFactory() }
    single<Analytics> { AnalyticsImpl() }
    single<Preferences> { PreferencesImpl() }
    
    // iOS CoreData-based repository
    single<TransactionsRepository> { IosTransactionsRepository() }
}

