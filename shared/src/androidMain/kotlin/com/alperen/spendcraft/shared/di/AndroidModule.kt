package com.alperen.spendcraft.shared.di

import com.alperen.spendcraft.shared.data.DatabaseDriverFactory
import com.alperen.spendcraft.shared.platform.Analytics
import com.alperen.spendcraft.shared.platform.AnalyticsImpl
import com.alperen.spendcraft.shared.platform.Preferences
import com.alperen.spendcraft.shared.platform.PreferencesImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { DatabaseDriverFactory(androidContext()) }
    single<Analytics> { AnalyticsImpl(androidContext()) }
    single<Preferences> { PreferencesImpl(androidContext()) }
}

