package com.franklinndiwe.insight.repository

import androidx.paging.PagingSource
import com.franklinndiwe.insight.data.Category
import com.franklinndiwe.insight.data.Suggestion
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategoryById(id: Int): Flow<Category?>

    fun getCategoryId(query: String): Flow<Int?>

    fun getFollowingCategoriesCount(): Flow<Int>

    fun getQuoteCount(id: Int): Flow<Int>

    fun getDailyCategories(): PagingSource<Int, Category>

    fun getFollowingCategories(): PagingSource<Int, Category>

    fun categorySuggestions(query: String): Flow<List<Suggestion>>

    fun getAllCategories(sortBy: String, order: String): PagingSource<Int, Category>

    fun searchCategory(query: String): PagingSource<Int, Category>

    fun getLikedCategories(sortBy: String, order: String): PagingSource<Int, Category>

    fun getUserCategories(sortBy: String, order: String): PagingSource<Int, Category>

    fun getPopularCategories(sortBy: String, order: String): PagingSource<Int, Category>

    fun getUnlockedCategories(sortBy: String, order: String): PagingSource<Int, Category>

    fun getLockedCategories(sortBy: String, order: String): PagingSource<Int, Category>

    fun checkForExistingCategory(category: String): Flow<Int>

    suspend fun insert(category: Category): Long

    suspend fun insert(categories: List<Category>)

    suspend fun update(vararg category: Category)

    suspend fun delete(vararg category: Category)
}