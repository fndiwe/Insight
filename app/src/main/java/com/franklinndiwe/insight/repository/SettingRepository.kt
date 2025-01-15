package com.franklinndiwe.insight.repository

import com.franklinndiwe.insight.data.Setting
import kotlinx.coroutines.flow.Flow

interface SettingRepository {
    fun getSetting(): Flow<Setting>

    suspend fun insert(setting: Setting)

    suspend fun update(setting: Setting)

    suspend fun delete(setting: Setting)
}