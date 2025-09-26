package com.alperen.spendcraft.core.billing.di

import android.content.Context
import com.alperen.spendcraft.core.billing.BillingManager
import com.alperen.spendcraft.core.billing.BillingManagerImpl
import com.alperen.spendcraft.core.billing.BillingProductValidator
import com.alperen.spendcraft.core.billing.BillingRepository
import com.alperen.spendcraft.core.premium.PremiumStateDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BillingModule {
    
    @Provides
    @Singleton
    fun provideBillingManager(@ApplicationContext context: Context): BillingManager {
        return BillingManagerImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideBillingProductValidator(): BillingProductValidator {
        return BillingProductValidator()
    }
    
    @Provides
    @Singleton
    fun provideBillingRepository(
        billingManager: BillingManager,
        premiumStateDataStore: PremiumStateDataStore,
        productValidator: BillingProductValidator
    ): BillingRepository {
        return BillingRepository(billingManager, premiumStateDataStore, productValidator)
    }
}
