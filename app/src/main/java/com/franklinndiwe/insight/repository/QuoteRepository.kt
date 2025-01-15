package com.franklinndiwe.insight.repository

import androidx.paging.PagingSource
import com.franklinndiwe.insight.data.Quote
import com.franklinndiwe.insight.data.QuoteV2
import kotlinx.coroutines.flow.Flow

interface QuoteRepository {
    fun getQuoteById(id: Int): Flow<QuoteV2?>

    fun getAllQuotes(sortBy: String, order: String): PagingSource<Int, QuoteV2>

    fun getQuotesForCategory(
        categoryId: Int, sortBy: String, order: String,
    ): PagingSource<Int, QuoteV2>

    fun dailyQuote(): Flow<QuoteV2?>

    fun getQuotesForAuthor(
        authorId: Int, sortBy: String, order: String,
    ): PagingSource<Int, QuoteV2>

    fun searchQuotes(query: String): PagingSource<Int, QuoteV2>

    fun getLikedQuotes(sortBy: String, order: String): PagingSource<Int, QuoteV2>

    fun getUserQuotes(sortBy: String, order: String): PagingSource<Int, QuoteV2>

    fun recommendedQuotes(): Flow<List<Int>>

    fun checkForExistingQuote(quote: String): Flow<Int>

    suspend fun insert(vararg quote: Quote)

    suspend fun insert(quotes: List<Quote>)

    suspend fun update(vararg quote: Quote)

    suspend fun delete(vararg quote: Quote)
}