package com.franklinndiwe.insight.utils

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.franklinndiwe.insight.data.Gradient
import com.franklinndiwe.insight.data.GradientV2
import com.franklinndiwe.insight.repository.GradientRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GradientUtils(
    private val viewModelScope: CoroutineScope,
    private val gradientRepository: GradientRepository,
) {
    private fun getGradients(
        pageSize: Int = 20,
        prefetchDistance: Int = pageSize,
        enablePlaceholders: Boolean = false,
        initialLoadSize: Int = pageSize * 3,
        maxSize: Int = Int.MAX_VALUE,
        jumpThreshold: Int = Int.MIN_VALUE,
        pagingSourceFactory: () -> PagingSource<Int, GradientV2>,
    ): Flow<PagingData<GradientV2>> {
        return Pager(
            PagingConfig(
                pageSize,
                prefetchDistance,
                enablePlaceholders,
                initialLoadSize,
                maxSize,
                jumpThreshold
            )
        ) {
            pagingSourceFactory()
        }.flow.flowOn(Dispatchers.IO).cachedIn(viewModelScope)
    }

    fun insertGradient(gradient: Gradient) =
        viewModelScope.async { gradientRepository.insert(gradient) }

    fun deleteGradient(gradient: Gradient) =
        viewModelScope.launch { gradientRepository.delete(gradient) }

    fun updateGradient(gradient: Gradient) =
        viewModelScope.launch(Dispatchers.IO) { gradientRepository.update(gradient) }

    val gradients = getGradients { gradientRepository.query() }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        PagingData.empty()
    )
    val displayGradients = gradientRepository.getDisplayGradients().flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun getGradientById(id: Int) = gradientRepository.queryById(id).flowOn(Dispatchers.IO)
}