package com.franklinndiwe.insight.repository

import com.franklinndiwe.insight.data.QuoteImageCategory
import kotlinx.coroutines.flow.Flow

interface QuoteImageCategoryRepository {
    fun query(): Flow<List<QuoteImageCategory>>

    fun checkForExistingCategory(categoryName: String): Int

    suspend fun insert(quoteImageCategory: QuoteImageCategory)

    suspend fun delete(quoteImageCategory: QuoteImageCategory)

    suspend fun update(quoteImageCategory: QuoteImageCategory)

}