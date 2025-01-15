package com.franklinndiwe.insight.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.franklinndiwe.insight.data.QuoteImageCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteImageCategoryDao {
    @Query("SELECT * FROM image_categories ORDER BY name DESC")
    fun query(): Flow<List<QuoteImageCategory>>

    @Query("SELECT COUNT(name) FROM image_categories WHERE LOWER(name) = LOWER(:categoryName)")
    fun checkForExistingCategory(categoryName: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(quoteImageCategory: QuoteImageCategory)

    @Delete
    suspend fun delete(quoteImageCategory: QuoteImageCategory)

    @Update
    suspend fun update(quoteImageCategory: QuoteImageCategory)

}