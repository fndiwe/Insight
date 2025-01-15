package com.franklinndiwe.insight.repository

import androidx.paging.PagingSource
import com.franklinndiwe.insight.data.Author
import com.franklinndiwe.insight.data.Suggestion
import kotlinx.coroutines.flow.Flow

interface AuthorRepository {
    fun getAuthorById(id: Int): Flow<Author?>

    fun getAuthorId(query: String): Flow<Int?>

    fun getQuoteCount(id: Int): Flow<Int>

    fun getFollowingAuthorsCount(): Flow<Int>

    fun authorSuggestions(query: String): Flow<List<Suggestion>>

    fun getAllAuthors(sortBy: String, order: String): PagingSource<Int, Author>

    fun searchAuthor(query: String): PagingSource<Int, Author>

    fun getFollowingAuthors(): PagingSource<Int, Author>

    fun getLikedAuthors(sortBy: String, order: String): PagingSource<Int, Author>

    fun getUserAuthors(sortBy: String, order: String): PagingSource<Int, Author>

    fun getPopularAuthors(sortBy: String, order: String): PagingSource<Int, Author>

    fun checkForExistingAuthor(author: String): Int

    suspend fun insert(author: Author): Long

    suspend fun insert(authors: List<Author>)

    suspend fun update(author: Author)

    suspend fun delete(vararg author: Author)
}