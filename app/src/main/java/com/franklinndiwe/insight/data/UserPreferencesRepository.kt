package com.franklinndiwe.insight.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class UserPreferencesRepository(
    private val dataStore: DataStore<Preferences>,
) {
    private companion object {
        val FIRST_INSTALL = booleanPreferencesKey("first_install")
        val QUOTA = intPreferencesKey("quota")
    }

    val firstInstall: Flow<Boolean> = dataStore.data.catch { }.map { preferences ->
        preferences[FIRST_INSTALL] != false
    }

    suspend fun saveFirstInstall(firstInstall: Boolean) {
        dataStore.edit { preferences ->
            preferences[FIRST_INSTALL] = firstInstall
        }
    }

    val quota: Flow<Int> = dataStore.data.catch { }.map { preferences ->
        preferences[QUOTA] ?: 100
    }

    suspend fun saveQuota(quota: Int) {
        dataStore.edit { preferences ->
            preferences[QUOTA] = quota
        }
    }
}