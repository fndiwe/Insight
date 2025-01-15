package com.franklinndiwe.insight.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.franklinndiwe.insight.data.AppFont
import kotlinx.coroutines.flow.Flow

@Dao
interface FontDao {
    @Transaction
    @Query("SELECT * FROM fonts WHERE id = :id")
    fun getFontById(id: Int): Flow<AppFont?>

    @Transaction
    @Query("SELECT * FROM fonts WHERE unlocked = 1")
    fun getUnlockedFont(): PagingSource<Int, AppFont>

    @Transaction
    @Query("SELECT * FROM fonts ORDER BY unlocked DESC")
    fun getAllFont(): PagingSource<Int, AppFont>

    @Query("SELECT COUNT(*) FROM fonts WHERE LOWER(name) = LOWER(:name)")
    fun checkForExistingFont(name: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg fontFamily: AppFont)

    @Update
    suspend fun update(fontFamily: AppFont)

    @Delete
    suspend fun delete(vararg fontFamily: AppFont)
}