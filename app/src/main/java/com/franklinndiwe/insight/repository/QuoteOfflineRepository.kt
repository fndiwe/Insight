package com.franklinndiwe.insight.repository

import com.franklinndiwe.insight.dao.QuoteDao
import com.franklinndiwe.insight.data.Quote

class QuoteOfflineRepository(private val quoteDao: QuoteDao) : QuoteRepository {
    override fun getQuoteById(id: Int) = quoteDao.getQuoteById(id)

    override fun getAllQuotes(sortBy: String, order: String) =
        quoteDao.getAllQuotes(sortBy, order)

    override fun getQuotesForCategory(
        categoryId: Int, sortBy: String, order: String,
    ) = quoteDao.getQuotesForCategory(categoryId, sortBy, order)

    override fun dailyQuote() = quoteDao.dailyQuote()

    override fun getQuotesForAuthor(
        authorId: Int, sortBy: String, order: String,
    ) = quoteDao.getQuotesForAuthor(authorId, sortBy, order)

    override fun searchQuotes(query: String) =
        quoteDao.searchQuotes(query)

    override fun getLikedQuotes(
        sortBy: String, order: String,
    ) = quoteDao.getLikedQuotes(sortBy, order)

    override fun getUserQuotes(
        sortBy: String, order: String,
    ) = quoteDao.getUserQuotes(sortBy, order)

    override fun recommendedQuotes() = quoteDao.recommendedQuotes()

    override fun checkForExistingQuote(quote: String) = quoteDao.checkForExistingQuote(quote)

    override suspend fun insert(vararg quote: Quote) = quoteDao.insert(*quote)

    override suspend fun insert(quotes: List<Quote>) = quoteDao.insert(quotes)

    override suspend fun update(vararg quote: Quote) = quoteDao.update(*quote)

    override suspend fun delete(vararg quote: Quote) = quoteDao.delete(*quote)
}