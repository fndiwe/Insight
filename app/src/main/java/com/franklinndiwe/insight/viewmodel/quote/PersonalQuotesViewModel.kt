package com.franklinndiwe.insight.viewmodel.quote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.franklinndiwe.insight.data.UserPreferencesRepository
import com.franklinndiwe.insight.repository.ColorRepository
import com.franklinndiwe.insight.repository.FontRepository
import com.franklinndiwe.insight.repository.GradientRepository
import com.franklinndiwe.insight.repository.QuoteImageRepository
import com.franklinndiwe.insight.repository.QuoteRepository
import com.franklinndiwe.insight.repository.SettingRepository
import com.franklinndiwe.insight.repository.SortOrderRepository
import com.franklinndiwe.insight.utils.QuoteScreenImpl
import com.franklinndiwe.insight.utils.SubType

class PersonalQuotesViewModel(
    quoteRepository: QuoteRepository,
    sortOrderRepository: SortOrderRepository,
    settingRepository: SettingRepository,
    fontRepository: FontRepository,
    gradientRepository: GradientRepository,
    colorRepository: ColorRepository,
    quoteImageRepository: QuoteImageRepository,
    userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    val quoteScreenImpl = QuoteScreenImpl(
        viewModelScope,
        SubType.PersonalQuotes,
        quoteRepository,
        sortOrderRepository,
        settingRepository,
        fontRepository,
        gradientRepository,
        colorRepository,
        quoteImageRepository,
        userPreferencesRepository
    )
}