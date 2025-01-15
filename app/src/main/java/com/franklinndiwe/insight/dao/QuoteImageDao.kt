package com.franklinndiwe.insight.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.franklinndiwe.insight.data.QuoteImage
import com.franklinndiwe.insight.data.QuoteImageV2
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteImageDao {
    @Transaction
    @Query("SELECT * FROM quote_images WHERE id = :id")
    fun getQuoteImageById(id: Int): Flow<QuoteImageV2?>

    @Transaction
    @Query("SELECT * FROM quote_images WHERE categoryName = :categoryName ORDER BY id DESC")
    fun getQuoteImageForCategory(categoryName: String): PagingSource<Int, QuoteImageV2>

    @Transaction
    @Query("SELECT * FROM quote_images WHERE id IN (SELECT q.id FROM quote_images q JOIN image_categories i ON q.categoryName = i.name WHERE q.unlocked = 1 AND i.included = 1 ORDER BY RANDOM() DESC LIMIT 100)")
    fun getDisplayImages(): Flow<List<QuoteImageV2>>

    @Transaction
    @Query("SELECT * FROM quote_images WHERE unlocked = 1 ORDER BY id DESC")
    fun getUnlockedImages(): PagingSource<Int, QuoteImageV2>

    @Transaction
    @Query("SELECT * FROM quote_images WHERE unlocked = 0 ORDER BY id DESC")
    fun getLockedImages(): PagingSource<Int, QuoteImageV2>

    @Insert
    suspend fun insert(vararg image: QuoteImage)

    @Update
    suspend fun update(vararg image: QuoteImage)

    @Delete
    suspend fun delete(vararg image: QuoteImage)
}