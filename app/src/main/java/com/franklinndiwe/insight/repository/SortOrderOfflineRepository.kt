package com.franklinndiwe.insight.repository

import com.franklinndiwe.insight.dao.SortOrderDao
import com.franklinndiwe.insight.data.SortOrder
import com.franklinndiwe.insight.utils.SubType
import kotlinx.coroutines.flow.Flow

class SortOrderOfflineRepository(private val sortOrderDao: SortOrderDao) : SortOrderRepository {
    override fun getSortOrder(subType: SubType): Flow<SortOrder> =
        sortOrderDao.getSortOrder(subType)

    override suspend fun insert(sortOrders: List<SortOrder>) = sortOrderDao.insert(sortOrders)

    override suspend fun update(sortOrder: SortOrder) = sortOrderDao.update(sortOrder)
}