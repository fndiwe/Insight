package com.franklinndiwe.insight.repository

import com.franklinndiwe.insight.dao.ColorDao
import com.franklinndiwe.insight.data.SolidColor

class ColorOfflineRepository(private val colorDao: ColorDao) : ColorRepository {
    override fun query() = colorDao.query()

    override fun queryById(id: Int) = colorDao.queryById(id)

    override fun getDisplayColors() = colorDao.getDisplayColors()

    override fun getLockedColors() = colorDao.getLockedColors()

    override fun checkForExistingColor(backgroundColor: Int, textColor: Int) =
        colorDao.checkForExistingColor(backgroundColor, textColor)

    override suspend fun insert(color: SolidColor) = colorDao.insert(color)

    override suspend fun insert(vararg color: SolidColor) = colorDao.insert(*color)

    override suspend fun update(vararg color: SolidColor) = colorDao.update(*color)

    override suspend fun delete(vararg color: SolidColor) = colorDao.delete(*color)
}