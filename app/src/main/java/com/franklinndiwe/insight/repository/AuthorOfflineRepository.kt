package com.franklinndiwe.insight.repository

import com.franklinndiwe.insight.dao.AuthorDao
import com.franklinndiwe.insight.data.Author
import kotlinx.coroutines.flow.Flow

class AuthorOfflineRepository(private val authorDao: AuthorDao) : AuthorRepository {
    override fun getAuthorById(id: Int) = authorDao.getAuthorById(id)

    override fun getAuthorId(query: String): Flow<Int?> = authorDao.getAuthorId(query)

    override fun getQuoteCount(id: Int): Flow<Int> = authorDao.getQuoteCount(id)

    override fun getFollowingAuthorsCount() = authorDao.getFollowingAuthorsCount()

    override fun authorSuggestions(query: String) = authorDao.authorSuggestions(query)

    override fun getAllAuthors(
        sortBy: String, order: String,
    ) = authorDao.getAllAuthors(sortBy, order)

    override fun searchAuthor(query: String) = authorDao.searchAuthor(query)

    override fun getFollowingAuthors() = authorDao.getFollowingAuthors()

    override fun getLikedAuthors(
        sortBy: String, order: String,
    ) = authorDao.getFavoriteAuthors(sortBy, order)

    override fun getUserAuthors(
        sortBy: String, order: String,
    ) = authorDao.getUserAuthors(sortBy, order)

    override fun getPopularAuthors(sortBy: String, order: String) =
        authorDao.getPopularAuthors(sortBy, order)

    override fun checkForExistingAuthor(author: String) =
        authorDao.checkForExistingAuthor(author)

    override suspend fun insert(author: Author) = authorDao.insert(author)

    override suspend fun insert(authors: List<Author>) = authorDao.insert(authors)

    override suspend fun update(author: Author) = authorDao.update(author)

    override suspend fun delete(vararg author: Author) = authorDao.delete(*author)
}