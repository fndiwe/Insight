package com.franklinndiwe.insight.repository

import androidx.paging.PagingSource
import com.franklinndiwe.insight.data.QuoteImage
import com.franklinndiwe.insight.data.QuoteImageV2
import kotlinx.coroutines.flow.Flow

interface QuoteImageRepository {
    fun getQuoteImageById(id: Int): Flow<QuoteImageV2?>

    fun getQuoteImageForCategory(categoryName: String): PagingSource<Int, QuoteImageV2>

    fun getDisplayImages(): Flow<List<QuoteImageV2>>

    fun getUnlockedImages(): PagingSource<Int, QuoteImageV2>

    fun getLockedImages(): PagingSource<Int, QuoteImageV2>

    suspend fun insert(vararg image: QuoteImage)

    suspend fun update(vararg image: QuoteImage)

    suspend fun delete(vararg image: QuoteImage)
}