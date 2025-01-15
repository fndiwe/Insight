package com.franklinndiwe.insight.utils

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.franklinndiwe.insight.data.Category
import com.franklinndiwe.insight.data.SortOrder
import com.franklinndiwe.insight.repository.CategoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch


class CategoryUtils(
    private val viewModelScope: CoroutineScope,
    private val categoryRepository: CategoryRepository,
) {

    private fun getCategories(
        pageSize: Int = 10,
        prefetchDistance: Int = pageSize,
        enablePlaceholders: Boolean = true,
        initialLoadSize: Int = pageSize * 3,
        maxSize: Int = Int.MAX_VALUE,
        jumpThreshold: Int = Int.MIN_VALUE,
        pagingSourceFactory: () -> PagingSource<Int, Category>,
    ): Flow<PagingData<Category>> {
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

    fun categorySuggestions(value: String) =
        categoryRepository.categorySuggestions(String.format("*%s*", value))

    fun getCategoryById(id: Int) = categoryRepository.getCategoryById(id)

    fun getQuoteCount(id: Int) = categoryRepository.getQuoteCount(id)

    fun getCategoryId(query: String) =
        viewModelScope.async { categoryRepository.getCategoryId(query).first() }

    fun getFreeCategories(sortOrder: SortOrder) = getCategories {
        categoryRepository.getUnlockedCategories(
            sortOrder.sortBy, sortOrder.order
        )
    }

    fun getDailyCategories() = getCategories { categoryRepository.getDailyCategories() }

    fun getFollowingCategories() = getCategories { categoryRepository.getFollowingCategories() }

    fun getPopularCategories(sortOrder: SortOrder) =
        getCategories { categoryRepository.getPopularCategories(sortOrder.sortBy, sortOrder.order) }

    fun getPremiumCategories(sortOrder: SortOrder) = getCategories {
        categoryRepository.getLockedCategories(
            sortOrder.sortBy, sortOrder.order
        )
    }

    fun searchCategory(query: String) = getCategories {
        categoryRepository.searchCategory(
            String.format("*%s*", query)
        )
    }

    fun getLikedCategories(sortOrder: SortOrder) = getCategories {
        categoryRepository.getLikedCategories(
            sortOrder.sortBy, sortOrder.order
        )
    }

    fun getPersonalCategories(sortOrder: SortOrder) = getCategories {
        categoryRepository.getUserCategories(
            sortOrder.sortBy, sortOrder.order
        )
    }

    fun insertCategory(category: Category) =
        viewModelScope.async { categoryRepository.insert(category) }

    fun updateCategory(category: Category) =
        viewModelScope.launch(Dispatchers.IO) { categoryRepository.update(category) }

    fun deleteCategory(category: Category) =
        viewModelScope.launch(Dispatchers.IO) { categoryRepository.delete(category) }
}