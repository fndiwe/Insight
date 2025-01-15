package com.franklinndiwe.insight.viewmodel.quote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.franklinndiwe.insight.data.UserPreferencesRepository
import com.franklinndiwe.insight.repository.AuthorRepository
import com.franklinndiwe.insight.repository.CategoryRepository
import com.franklinndiwe.insight.repository.ColorRepository
import com.franklinndiwe.insight.repository.FontRepository
import com.franklinndiwe.insight.repository.GradientRepository
import com.franklinndiwe.insight.repository.QuoteImageRepository
import com.franklinndiwe.insight.repository.QuoteRepository
import com.franklinndiwe.insight.repository.SettingRepository
import com.franklinndiwe.insight.repository.SortOrderRepository
import com.franklinndiwe.insight.utils.QuoteScreenImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ForYouQuotesViewModel(
    quoteRepository: QuoteRepository,
    categoryRepository: CategoryRepository,
    authorRepository: AuthorRepository,
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
        null,
        quoteRepository,
        sortOrderRepository,
        settingRepository,
        fontRepository,
        gradientRepository,
        colorRepository,
        quoteImageRepository,
        userPreferencesRepository
    )

    private val recommendedQuotes = quoteScreenImpl.quoteUtils.getRecommendedQuotes()
    private val _quotes = MutableStateFlow<List<Int>>(emptyList())
    val quotes = _quotes.asStateFlow()
    private val _refreshingQuotes = MutableStateFlow(false)
    val followingCategoryCount = categoryRepository.getFollowingCategoriesCount()
    val followingAuthorsCount = authorRepository.getFollowingAuthorsCount()
    val refreshingQuotes = _refreshingQuotes.asStateFlow()
    fun refreshQuotes() = viewModelScope.launch {
        _refreshingQuotes.update { true }
        _quotes.update { recommendedQuotes.first() }
        _refreshingQuotes.update { false }
    }
}