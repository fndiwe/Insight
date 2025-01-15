package com.franklinndiwe.insight.repository

import androidx.paging.PagingSource
import com.franklinndiwe.insight.data.SolidColor
import com.franklinndiwe.insight.data.SolidColorV2
import kotlinx.coroutines.flow.Flow

interface ColorRepository {
    fun query(): PagingSource<Int, SolidColorV2>

    fun queryById(id: Int): Flow<SolidColorV2?>

    fun getDisplayColors(): Flow<List<SolidColorV2>>

    fun getLockedColors(): PagingSource<Int, SolidColorV2>

    fun checkForExistingColor(backgroundColor: Int, textColor: Int): Int

    suspend fun insert(color: SolidColor): Long

    suspend fun insert(vararg color: SolidColor)

    suspend fun update(vararg color: SolidColor)

    suspend fun delete(vararg color: SolidColor)
}