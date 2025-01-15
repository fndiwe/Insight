package com.franklinndiwe.insight.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.franklinndiwe.insight.data.SortOrder
import com.franklinndiwe.insight.utils.SubType
import kotlinx.coroutines.flow.Flow

@Dao
interface SortOrderDao {
    @Query("SELECT * FROM sort_order WHERE subType = :subType")
    fun getSortOrder(subType: SubType): Flow<SortOrder>

    @Insert
    suspend fun insert(sortOrders: List<SortOrder>)

    @Update
    suspend fun update(sortOrder: SortOrder)
}