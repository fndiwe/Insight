package com.franklinndiwe.insight.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.room.Update
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.franklinndiwe.insight.data.Category
import com.franklinndiwe.insight.data.Suggestion
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CategoryDao {
    @Query("SELECT * FROM categories WHERE id = :id")
    abstract fun getCategoryById(id: Int): Flow<Category?>

    @Query("SELECT id FROM categories WHERE LOWER(:query) = LOWER(name)")
    abstract fun getCategoryId(query: String): Flow<Int?>

    @Query("SELECT COUNT(CASE WHEN c.id = q.categoryId1 THEN q.id END) + COUNT(CASE WHEN c.id = q.categoryId2 THEN q.id END) FROM categories c LEFT JOIN quotes q ON c.id IN (q.categoryId1, q.categoryId2) WHERE c.id = :id GROUP BY c.id")
    abstract fun getQuoteCount(id: Int): Flow<Int>

    @RawQuery([Category::class])
    @Transaction
    protected abstract fun getCategories(query: SupportSQLiteQuery): PagingSource<Int, Category>

    @Query("SELECT * FROM categories WHERE daily = 1 ORDER BY id DESC")
    abstract fun getDailyCategories(): PagingSource<Int, Category>

    @Query("SELECT * FROM categories WHERE following = 1 ORDER BY id DESC")
    abstract fun getFollowingCategories(): PagingSource<Int, Category>

    @Query("SELECT COUNT(id) FROM categories WHERE following = 1")
    abstract fun getFollowingCategoriesCount(): Flow<Int>

    fun getAllCategories(
        sortBy: String, order: String,
    ): PagingSource<Int, Category> {
        val statement = "SELECT * FROM categories ${
            orderLogic(
                sortBy, order = order
            )
        }"
        return getCategories(SimpleSQLiteQuery(statement))
    }

    @Transaction
    @Query("SELECT c.* FROM categories c JOIN category_fts ON (c.id = category_fts.rowid) WHERE category_fts MATCH :query AND c.unlocked = 1")
    abstract fun searchCategory(query: String): PagingSource<Int, Category>

    @Query("SELECT c.id, c.name FROM categories c JOIN category_fts ON (c.id = category_fts.rowid) WHERE category_fts MATCH :query AND c.unlocked = 1 LIMIT 10")
    abstract fun categorySuggestions(query: String): Flow<List<Suggestion>>

    fun getLikedCategories(
        sortBy: String, order: String,
    ): PagingSource<Int, Category> {
        val statement = "SELECT * FROM categories WHERE liked NOT NULL ${
            orderLogic(
                sortBy, order = order
            )
        }"
        return getCategories(SimpleSQLiteQuery(statement))
    }

    fun getUserCategories(
        sortBy: String, order: String,
    ): PagingSource<Int, Category> {
        val statement = "SELECT * FROM categories WHERE userGenerated NOT NULL ${
            orderLogic(
                sortBy, order = order
            )
        }"
        return getCategories(SimpleSQLiteQuery(statement))
    }

    fun getPopularCategories(sortBy: String, order: String): PagingSource<Int, Category> {
        val statement = "SELECT * FROM categories WHERE popular = 1 ${
            orderLogic(sortBy, order = order)
        }"
        return getCategories(SimpleSQLiteQuery(statement))
    }

    fun getLockedCategories(sortBy: String, order: String): PagingSource<Int, Category> {
        val statement = "SELECT * FROM categories WHERE unlocked = 0 ${
            orderLogic(sortBy, order = order)
        }"
        return getCategories(SimpleSQLiteQuery(statement))
    }

    fun getUnlockedCategories(sortBy: String, order: String): PagingSource<Int, Category> {
        val statement = "SELECT * FROM categories WHERE unlocked = 1 ${
            orderLogic(sortBy, order = order)
        }"
        return getCategories(SimpleSQLiteQuery(statement))
    }

    @Query("SELECT COUNT(*) FROM categories WHERE LOWER(name) = LOWER(:category)")
    abstract fun checkForExistingCategory(category: String): Flow<Int>

    @Insert
    abstract suspend fun insert(category: Category): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insert(categories: List<Category>)

    @Update
    abstract suspend fun update(vararg category: Category)

    @Delete
    abstract suspend fun delete(vararg category: Category)
}
