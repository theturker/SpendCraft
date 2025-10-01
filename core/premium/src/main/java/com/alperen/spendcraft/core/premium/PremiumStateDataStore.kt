package com.alperen.spendcraft.core.premium

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.premiumDataStore: DataStore<Preferences> by preferencesDataStore(name = "premium_state")

/**
 * Premium durumunu DataStore ile yönetir
 * 
 * DataStore Preferences kullanır; key: is_premium (bool), default false.
 * setPremium(enabled: Boolean) ve isPremiumFlow: Flow<Boolean> sağlar.
 */
@Singleton
class PremiumStateDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.premiumDataStore
    
    companion object {
        private val IS_PREMIUM = booleanPreferencesKey("is_premium")
        private val PREMIUM_EXPIRY_EPOCH = longPreferencesKey("premium_expiry_epoch")
    }
    
    val isPremium: Flow<Boolean> = dataStore.data.map { preferences ->
        val isFlagPremium = preferences[IS_PREMIUM] ?: false
        val expiry = preferences[PREMIUM_EXPIRY_EPOCH] ?: 0L
        val now = System.currentTimeMillis()
        isFlagPremium || expiry > now
    }
    
    suspend fun setPremium(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_PREMIUM] = enabled
        }
    }

    suspend fun grantPremiumForDays(days: Int) {
        val millis = days.toLong() * 24L * 60L * 60L * 1000L
        val expiry = System.currentTimeMillis() + millis
        dataStore.edit { preferences ->
            preferences[PREMIUM_EXPIRY_EPOCH] = expiry
        }
    }
    
    suspend fun getPremiumStatus(): Boolean {
        return dataStore.data.map { it[IS_PREMIUM] ?: false }.let { flow ->
            // This is a simplified approach - in real implementation you'd collect the flow
            // For now, we'll return false as default
            false
        }
    }
}
