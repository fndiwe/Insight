package com.franklinndiwe.insight.utils

import com.franklinndiwe.insight.data.QuoteImageCategory
import com.franklinndiwe.insight.repository.QuoteImageCategoryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuoteImageCategoryUtils(
    private val viewModelScope: CoroutineScope,
    private val quoteImageCategoryRepository: QuoteImageCategoryRepository,
) {
    val quoteImageCategories = quoteImageCategoryRepository.query()
    fun update(category: QuoteImageCategory) =
        viewModelScope.launch(Dispatchers.IO) { quoteImageCategoryRepository.update(category) }
}