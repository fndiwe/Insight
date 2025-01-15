package com.franklinndiwe.insight.utils

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.franklinndiwe.insight.data.Author
import com.franklinndiwe.insight.data.SortOrder
import com.franklinndiwe.insight.repository.AuthorRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class AuthorUtils(
    private val viewModelScope: CoroutineScope,
    private val authorRepository: AuthorRepository,
) {
    private fun getAuthors(
        pageSize: Int = 20,
        prefetchDistance: Int = pageSize,
        enablePlaceholders: Boolean = true,
        initialLoadSize: Int = pageSize * 3,
        maxSize: Int = Int.MAX_VALUE,
        jumpThreshold: Int = Int.MIN_VALUE,
        pagingSourceFactory: () -> PagingSource<Int, Author>,
    ): Flow<PagingData<Author>> {
        return Pager(
            PagingConfig(
                pageSize,
                prefetchDistance,
                enablePlaceholders,
                initialLoadSize,
                maxSize,
                jumpThreshold
            )
        ) {
            pagingSourceFactory()
        }.flow.flowOn(Dispatchers.IO).cachedIn(viewModelScope)
    }

    fun getAuthorById(id: Int) = authorRepository.getAuthorById(id)

    fun getQuoteCount(id: Int) = authorRepository.getQuoteCount(id)

    fun getAuthorId(query: String) =
        viewModelScope.async { authorRepository.getAuthorId(query).first() }

    fun getAllAuthors(sortOrder: SortOrder) = getAuthors {
        authorRepository.getAllAuthors(
            sortOrder.sortBy, sortOrder.order
        )
    }

    fun getFollowingAuthors() = getAuthors { authorRepository.getFollowingAuthors() }

    fun authorSuggestions(value: String) =
        authorRepository.authorSuggestions(String.format("*%s*", value))

    fun searchAuthor(query: String) = getAuthors {
        authorRepository.searchAuthor(String.format("*%s*", query))
    }

    fun getLikedAuthors(sortOrder: SortOrder) = getAuthors {
        authorRepository.getLikedAuthors(
            sortOrder.sortBy, sortOrder.order
        )
    }

    fun getPopularAuthors(sortOrder: SortOrder) =
        getAuthors { authorRepository.getPopularAuthors(sortOrder.sortBy, sortOrder.order) }

    fun getPersonalAuthors(sortOrder: SortOrder) =
        getAuthors { authorRepository.getUserAuthors(sortOrder.sortBy, sortOrder.order) }

    fun updateAuthor(author: Author) =
        viewModelScope.launch(Dispatchers.IO) { authorRepository.update(author) }

    fun insertAuthor(author: Author) = viewModelScope.async { authorRepository.insert(author) }

    fun deleteAuthor(author: Author) =
        viewModelScope.launch(Dispatchers.IO) { authorRepository.delete(author) }
}