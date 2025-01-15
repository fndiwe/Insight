package com.franklinndiwe.insight.repository

import com.franklinndiwe.insight.dao.CategoryDao
import com.franklinndiwe.insight.data.Category

class CategoryOfflineRepository(private val categoryDao: CategoryDao) : CategoryRepository {
    override fun getCategoryById(id: Int) = categoryDao.getCategoryById(id)

    override fun getCategoryId(query: String) = categoryDao.getCategoryId(query)

    override fun getFollowingCategoriesCount() = categoryDao.getFollowingCategoriesCount()

    override fun getQuoteCount(id: Int) = categoryDao.getQuoteCount(id)

    override fun getDailyCategories() = categoryDao.getDailyCategories()

    override fun getFollowingCategories() = categoryDao.getFollowingCategories()

    override fun categorySuggestions(query: String) = categoryDao.categorySuggestions(query)

    override fun getAllCategories(
        sortBy: String, order: String,
    ) = categoryDao.getAllCategories(sortBy, order)

    override fun searchCategory(
        query: String,
    ) = categoryDao.searchCategory(query)

    override fun getLikedCategories(
        sortBy: String, order: String,
    ) = categoryDao.getLikedCategories(sortBy, order)

    override fun getUserCategories(
        sortBy: String, order: String,
    ) = categoryDao.getUserCategories(sortBy, order)

    override fun getPopularCategories(sortBy: String, order: String) =
        categoryDao.getPopularCategories(sortBy, order)

    override fun getUnlockedCategories(sortBy: String, order: String) =
        categoryDao.getUnlockedCategories(sortBy, order)

    override fun getLockedCategories(sortBy: String, order: String) =
        categoryDao.getLockedCategories(sortBy, order)

    override fun checkForExistingCategory(category: String) =
        categoryDao.checkForExistingCategory(category)

    override suspend fun insert(category: Category) = categoryDao.insert(category)

    override suspend fun insert(categories: List<Category>) = categoryDao.insert(categories)

    override suspend fun update(vararg category: Category) = categoryDao.update(*category)

    override suspend fun delete(vararg category: Category) = categoryDao.delete(*category)
}

