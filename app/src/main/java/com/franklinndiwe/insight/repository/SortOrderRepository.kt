package com.franklinndiwe.insight.repository

import com.franklinndiwe.insight.data.SortOrder
import com.franklinndiwe.insight.utils.SubType
import kotlinx.coroutines.flow.Flow

interface SortOrderRepository {
    fun getSortOrder(subType: SubType): Flow<SortOrder>

    suspend fun insert(sortOrders: List<SortOrder>)

    suspend fun update(sortOrder: SortOrder)
}