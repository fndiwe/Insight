package com.franklinndiwe.insight.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.franklinndiwe.insight.data.Gradient
import com.franklinndiwe.insight.data.GradientV2
import kotlinx.coroutines.flow.Flow

@Dao
interface GradientDao {
    @Transaction
    @Query("SELECT * FROM gradients ORDER BY unlocked DESC")
    fun query(): PagingSource<Int, GradientV2>

    @Transaction
    @Query("SELECT * FROM gradients WHERE id = :id")
    fun queryById(id: Int): Flow<GradientV2?>

    @Transaction
    @Query("SELECT * FROM gradients WHERE id IN (SELECT id FROM gradients WHERE unlocked = 1 ORDER BY RANDOM() LIMIT 100)")
    fun getDisplayGradients(): Flow<List<GradientV2>>

    @Transaction
    @Query("SELECT * FROM gradients WHERE unlocked = 0")
    fun getLockedGradients(): PagingSource<Int, GradientV2>

    @Query("SELECT COUNT(*) FROM gradients WHERE colors = :colors AND textColor = :textColor")
    fun checkForExistingGradient(colors: List<Int>, textColor: Int): Int

    @Insert
    suspend fun insert(gradient: Gradient): Long

    @Insert
    suspend fun insert(vararg gradient: Gradient)

    @Update
    suspend fun update(vararg gradient: Gradient)

    @Delete
    suspend fun delete(vararg gradient: Gradient)
}