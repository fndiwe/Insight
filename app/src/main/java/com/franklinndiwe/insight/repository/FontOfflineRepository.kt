package com.franklinndiwe.insight.repository

import com.franklinndiwe.insight.dao.FontDao
import com.franklinndiwe.insight.data.AppFont

class FontOfflineRepository(private val fontDao: FontDao) : FontRepository {
    override fun getFontById(id: Int) = fontDao.getFontById(id)

    override fun getUnlockedFont() =
        fontDao.getUnlockedFont()

    override fun getAllFont() =
        fontDao.getAllFont()

    override fun checkForExistingFont(name: String) =
        fontDao.checkForExistingFont(name)

    override suspend fun insert(vararg fonts: AppFont) =
        fontDao.insert(*fonts)

    override suspend fun update(font: AppFont) = fontDao.update(font)

    override suspend fun delete(vararg fonts: AppFont) =
        fontDao.delete(*fonts)
}