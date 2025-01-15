package com.franklinndiwe.insight.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.franklinndiwe.insight.data.Author
import com.franklinndiwe.insight.data.Category
import com.franklinndiwe.insight.data.QuoteV2
import com.franklinndiwe.insight.repository.AuthorRepository
import com.franklinndiwe.insight.repository.CategoryRepository
import com.franklinndiwe.insight.repository.QuoteRepository
import com.franklinndiwe.insight.ui.states.SearchUIState
import com.franklinndiwe.insight.utils.AuthorUtils
import com.franklinndiwe.insight.utils.CategoryUtils
import com.franklinndiwe.insight.utils.QuoteUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SearchScreenViewModel(
    categoryRepository: CategoryRepository,
    authorRepository: AuthorRepository,
    quoteRepository: QuoteRepository,
) : ViewModel() {
    private val categoryUtils = CategoryUtils(viewModelScope, categoryRepository)
    private val quoteUtils = QuoteUtils(viewModelScope, quoteRepository)
    private val authorUtils = AuthorUtils(viewModelScope, authorRepository)
    private val _searchUIState = MutableStateFlow(SearchUIState())
    val searchUIState = _searchUIState.asStateFlow()
    fun quotes(value: String) = quoteUtils.searchQuotes(value)
    fun authors(value: String) = authorUtils.searchAuthor(value)
    fun categories(value: String) = categoryUtils.searchCategory(value)
    fun onValueChange(value: String) = _searchUIState.update { it.copy(value = value) }
    fun updateQuote(quote: QuoteV2) = quoteUtils.updateQuote(quote)
    fun updateCategory(category: Category) = categoryUtils.updateCategory(category)
    fun updateAuthor(author: Author) = authorUtils.updateAuthor(author)
    fun shareQuote(context: Context, quote: QuoteV2) = quoteUtils.shareQuote(context, quote)
}