package com.franklinndiwe.insight.utils

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.franklinndiwe.insight.data.AppFont
import com.franklinndiwe.insight.repository.FontRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class AppFontUtils(
    private val viewModelScope: CoroutineScope,
    private val fontRepository: FontRepository,
) {
    val unlockedFonts = getFonts { fontRepository.getUnlockedFont() }
    val fonts = getFonts { fontRepository.getAllFont() }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        PagingData.empty()
    )

    fun insertFont(font: AppFont) = viewModelScope.launch { fontRepository.insert(font) }
    fun deleteFont(font: AppFont) {
        val file = File(font.path)
        if (file.exists()) file.delete()
        viewModelScope.launch { fontRepository.delete(font) }
    }

    fun updateFont(font: AppFont) =
        viewModelScope.launch(Dispatchers.IO) { fontRepository.update(font) }

    fun getFontById(id: Int) = fontRepository.getFontById(id).flowOn(Dispatchers.IO)

    private fun getFonts(
        pageSize: Int = 20,
        prefetchDistance: Int = pageSize,
        enablePlaceholders: Boolean = true,
        initialLoadSize: Int = pageSize * 3,
        maxSize: Int = Int.MAX_VALUE,
        jumpThreshold: Int = Int.MIN_VALUE,
        pagingSourceFactory: () -> PagingSource<Int, AppFont>,
    ) = Pager(
        PagingConfig(
            pageSize, prefetchDistance, enablePlaceholders, initialLoadSize, maxSize, jumpThreshold
        )
    ) {
        pagingSourceFactory()
    }.flow.flowOn(Dispatchers.IO).cachedIn(viewModelScope)

}