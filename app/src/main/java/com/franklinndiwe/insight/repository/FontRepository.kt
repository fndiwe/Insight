package com.franklinndiwe.insight.repository

import androidx.paging.PagingSource
import com.franklinndiwe.insight.data.AppFont
import kotlinx.coroutines.flow.Flow

interface FontRepository {
    fun getFontById(id: Int): Flow<AppFont?>

    fun getUnlockedFont(): PagingSource<Int, AppFont>

    fun getAllFont(): PagingSource<Int, AppFont>

    fun checkForExistingFont(name: String): Int

    suspend fun insert(vararg fonts: AppFont)

    suspend fun update(font: AppFont)

    suspend fun delete(vararg fonts: AppFont)
}