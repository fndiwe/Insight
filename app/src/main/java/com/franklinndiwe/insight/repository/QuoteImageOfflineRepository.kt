package com.franklinndiwe.insight.repository

import com.franklinndiwe.insight.dao.QuoteImageDao
import com.franklinndiwe.insight.data.QuoteImage

class QuoteImageOfflineRepository(private val quoteImageDao: QuoteImageDao) : QuoteImageRepository {
    override fun getQuoteImageById(id: Int) = quoteImageDao.getQuoteImageById(id)

    override fun getQuoteImageForCategory(categoryName: String) =
        quoteImageDao.getQuoteImageForCategory(categoryName)

    override fun getDisplayImages() = quoteImageDao.getDisplayImages()

    override fun getUnlockedImages() =
        quoteImageDao.getUnlockedImages()

    override fun getLockedImages() = quoteImageDao.getLockedImages()

    override suspend fun insert(vararg image: QuoteImage) = quoteImageDao.insert(*image)

    override suspend fun update(vararg image: QuoteImage) = quoteImageDao.update(*image)

    override suspend fun delete(vararg image: QuoteImage) = quoteImageDao.delete(*image)
}