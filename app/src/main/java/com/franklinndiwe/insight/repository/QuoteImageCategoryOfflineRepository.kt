package com.franklinndiwe.insight.repository

import com.franklinndiwe.insight.dao.QuoteImageCategoryDao
import com.franklinndiwe.insight.data.QuoteImageCategory

class QuoteImageCategoryOfflineRepository(private val quoteImageCategoryDao: QuoteImageCategoryDao) :
    QuoteImageCategoryRepository {
    override fun query() = quoteImageCategoryDao.query()

    override fun checkForExistingCategory(categoryName: String) =
        quoteImageCategoryDao.checkForExistingCategory(categoryName)


    override suspend fun insert(quoteImageCategory: QuoteImageCategory) =
        quoteImageCategoryDao.insert(quoteImageCategory)

    override suspend fun delete(quoteImageCategory: QuoteImageCategory) =
        quoteImageCategoryDao.delete(quoteImageCategory)

    override suspend fun update(quoteImageCategory: QuoteImageCategory) =
        quoteImageCategoryDao.update(quoteImageCategory)
}