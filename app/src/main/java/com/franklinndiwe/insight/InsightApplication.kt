package com.franklinndiwe.insight

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.franklinndiwe.insight.data.AppContainer
import com.franklinndiwe.insight.data.AppDataContainer
import com.franklinndiwe.insight.data.UserPreferencesRepository

private const val PREFERENCE_NAME = "user_preferences"
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFERENCE_NAME
)

class InsightApplication : Application() {
    lateinit var userPreferencesRepository: UserPreferencesRepository
        private set
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        userPreferencesRepository = UserPreferencesRepository(dataStore)
        container = AppDataContainer(this)
    }
}