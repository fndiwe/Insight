package com.franklinndiwe.insight.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.franklinndiwe.insight.data.SolidColor
import com.franklinndiwe.insight.data.SolidColorV2
import kotlinx.coroutines.flow.Flow

@Dao
interface ColorDao {
    @Transaction
    @Query("SELECT * FROM colors ORDER BY unlocked DESC")
    fun query(): PagingSource<Int, SolidColorV2>

    @Transaction
    @Query("SELECT * FROM colors WHERE id = :id")
    fun queryById(id: Int): Flow<SolidColorV2?>

    @Transaction
    @Query("SELECT * FROM colors WHERE id IN (SELECT id FROM colors WHERE unlocked = 1 ORDER BY RANDOM() DESC LIMIT 100)")
    fun getDisplayColors(): Flow<List<SolidColorV2>>

    @Transaction
    @Query("SELECT * FROM colors WHERE unlocked = 0")
    fun getLockedColors(): PagingSource<Int, SolidColorV2>

    @Query("SELECT COUNT(*) FROM colors WHERE background = :backgroundColor AND textColor = :textColor")
    fun checkForExistingColor(backgroundColor: Int, textColor: Int): Int

    @Insert
    suspend fun insert(color: SolidColor): Long

    @Insert
    suspend fun insert(vararg color: SolidColor)

    @Update
    suspend fun update(vararg color: SolidColor)

    @Delete
    suspend fun delete(vararg color: SolidColor)
}