package com.franklinndiwe.insight.repository

import com.franklinndiwe.insight.dao.SettingDao
import com.franklinndiwe.insight.data.Setting

class SettingOfflineRepository(private val settingDao: SettingDao) : SettingRepository {
    override fun getSetting() = settingDao.getSetting()

    override suspend fun insert(setting: Setting) = settingDao.insert(setting)

    override suspend fun update(setting: Setting) = settingDao.update(setting)

    override suspend fun delete(setting: Setting) = settingDao.delete(setting)
}