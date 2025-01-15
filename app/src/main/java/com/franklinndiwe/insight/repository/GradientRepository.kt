package com.franklinndiwe.insight.repository

import androidx.paging.PagingSource
import com.franklinndiwe.insight.data.Gradient
import com.franklinndiwe.insight.data.GradientV2
import kotlinx.coroutines.flow.Flow

interface GradientRepository {
    fun query(): PagingSource<Int, GradientV2>

    fun queryById(id: Int): Flow<GradientV2?>

    fun getDisplayGradients(): Flow<List<GradientV2>>

    fun getLockedGradients(): PagingSource<Int, GradientV2>

    fun checkForExistingGradient(colors: List<Int>, textColor: Int): Int

    suspend fun insert(gradient: Gradient): Long

    suspend fun insert(vararg gradient: Gradient)

    suspend fun update(vararg gradient: Gradient)

    suspend fun delete(vararg gradient: Gradient)
}