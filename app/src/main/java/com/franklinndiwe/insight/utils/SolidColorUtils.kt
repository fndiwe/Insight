package com.franklinndiwe.insight.utils

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.franklinndiwe.insight.data.SolidColor
import com.franklinndiwe.insight.data.SolidColorV2
import com.franklinndiwe.insight.repository.ColorRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SolidColorUtils(
    private val viewModelScope: CoroutineScope,
    private val colorRepository: ColorRepository,
) {
    private fun getColors(
        pageSize: Int = 20,
        prefetchDistance: Int = pageSize,
        enablePlaceholders: Boolean = false,
        initialLoadSize: Int = pageSize * 3,
        maxSize: Int = Int.MAX_VALUE,
        jumpThreshold: Int = Int.MIN_VALUE,
        pagingSourceFactory: () -> PagingSource<Int, SolidColorV2>,
    ): Flow<PagingData<SolidColorV2>> {
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

    fun insertColor(color: SolidColor) = viewModelScope.async { colorRepository.insert(color) }
    fun deleteColor(color: SolidColor) = viewModelScope.launch { colorRepository.delete(color) }
    fun updateColor(color: SolidColor) =
        viewModelScope.launch(Dispatchers.IO) { colorRepository.update(color) }

    val colors = getColors { colorRepository.query() }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        PagingData.empty()
    )
    val displayColors = colorRepository.getDisplayColors().flowOn(Dispatchers.IO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    fun getColorById(id: Int) = colorRepository.queryById(id).flowOn(Dispatchers.IO)
}