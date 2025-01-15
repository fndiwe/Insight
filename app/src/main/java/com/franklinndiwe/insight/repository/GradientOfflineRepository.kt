package com.franklinndiwe.insight.repository

import com.franklinndiwe.insight.dao.GradientDao
import com.franklinndiwe.insight.data.Gradient

class GradientOfflineRepository(private val gradientDao: GradientDao) : GradientRepository {
    override fun query() = gradientDao.query()

    override fun queryById(id: Int) = gradientDao.queryById(id)

    override fun getDisplayGradients() = gradientDao.getDisplayGradients()

    override fun getLockedGradients() =
        gradientDao.getLockedGradients()

    override fun checkForExistingGradient(colors: List<Int>, textColor: Int) =
        gradientDao.checkForExistingGradient(colors, textColor)

    override suspend fun insert(gradient: Gradient) = gradientDao.insert(gradient)

    override suspend fun insert(vararg gradient: Gradient) = gradientDao.insert(*gradient)

    override suspend fun update(vararg gradient: Gradient) = gradientDao.update(*gradient)

    override suspend fun delete(vararg gradient: Gradient) = gradientDao.delete(*gradient)
}