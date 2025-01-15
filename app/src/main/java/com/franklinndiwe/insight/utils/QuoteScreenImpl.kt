package com.franklinndiwe.insight.utils

import com.franklinndiwe.insight.data.SortOrder
import com.franklinndiwe.insight.data.UserPreferencesRepository
import com.franklinndiwe.insight.repository.ColorRepository
import com.franklinndiwe.insight.repository.FontRepository
import com.franklinndiwe.insight.repository.GradientRepository
import com.franklinndiwe.insight.repository.QuoteImageRepository
import com.franklinndiwe.insight.repository.QuoteRepository
import com.franklinndiwe.insight.repository.SettingRepository
import com.franklinndiwe.insight.repository.SortOrderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QuoteScreenImpl(
    private val viewModelScope: CoroutineScope,
    subType: SubType?,
    quoteRepository: QuoteRepository,
    private val sortOrderRepository: SortOrderRepository,
    settingRepository: SettingRepository,
    fontRepository: FontRepository,
    gradientRepository: GradientRepository,
    colorRepository: ColorRepository,
    quoteImageRepository: QuoteImageRepository,
    userPreferencesRepository: UserPreferencesRepository,
) {
    private val dispatcher = Dispatchers.IO
    val sortOrder =
        subType?.let { type ->
            sortOrderRepository.getSortOrder(type)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)
        }
    val quoteUtils = QuoteUtils(viewModelScope, quoteRepository)
    val settingUtils = SettingUtils(viewModelScope, settingRepository)
    val appFontUtils = AppFontUtils(viewModelScope, fontRepository)
    val gradientUtils = GradientUtils(viewModelScope, gradientRepository)
    val colorUtils = SolidColorUtils(viewModelScope, colorRepository)
    val quoteImageUtils = QuoteImageUtils(viewModelScope, quoteImageRepository)
    val quotaUtils = QuotaUtils(viewModelScope, userPreferencesRepository)
    fun changeSortOrder(sortOrder: SortOrder) =
        viewModelScope.launch(dispatcher) { sortOrderRepository.update(sortOrder) }
}