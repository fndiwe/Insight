package com.franklinndiwe.insight.utils

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.franklinndiwe.insight.data.QuoteImage
import com.franklinndiwe.insight.data.QuoteImageV2
import com.franklinndiwe.insight.repository.QuoteImageRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File

class QuoteImageUtils(
    private val viewModelScope: CoroutineScope,
    private val quoteImageRepository: QuoteImageRepository,
) {
    private fun getQuoteImages(
        pageSize: Int = 20,
        prefetchDistance: Int = pageSize,
        enablePlaceholders: Boolean = false,
        initialLoadSize: Int = pageSize * 3,
        maxSize: Int = Int.MAX_VALUE,
        jumpThreshold: Int = Int.MIN_VALUE,
        pagingSourceFactory: () -> PagingSource<Int, QuoteImageV2>,
    ): Flow<PagingData<QuoteImageV2>> {
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

    fun insertQuoteImage(image: QuoteImage) =
        viewModelScope.launch { quoteImageRepository.insert(image) }

    fun deleteQuoteImage(image: QuoteImage) {
        val file = File(image.path)
        if (file.exists()) file.delete()
        viewModelScope.launch {
            quoteImageRepository.delete(image)
        }
    }

    fun updateQuoteImage(image: QuoteImage) =
        viewModelScope.launch(Dispatchers.IO) { quoteImageRepository.update(image) }

    val displayImages = quoteImageRepository.getDisplayImages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun getImagesForCategory(categoryName: String) =
        getQuoteImages { quoteImageRepository.getQuoteImageForCategory(categoryName) }

    fun getImageById(id: Int) = quoteImageRepository.getQuoteImageById(id).flowOn(Dispatchers.IO)
}