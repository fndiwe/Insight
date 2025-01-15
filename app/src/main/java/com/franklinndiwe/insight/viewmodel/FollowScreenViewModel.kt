package com.franklinndiwe.insight.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.franklinndiwe.insight.data.Author
import com.franklinndiwe.insight.data.Category
import com.franklinndiwe.insight.repository.AuthorRepository
import com.franklinndiwe.insight.repository.CategoryRepository
import com.franklinndiwe.insight.ui.states.FollowScreenUIState
import com.franklinndiwe.insight.utils.AuthorUtils
import com.franklinndiwe.insight.utils.CategoryUtils
import com.franklinndiwe.insight.utils.SortOrders
import com.franklinndiwe.insight.utils.SubType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FollowScreenViewModel(
    categoryRepository: CategoryRepository,
    authorRepository: AuthorRepository,
) : ViewModel() {
    private val categoryUtils = CategoryUtils(viewModelScope, categoryRepository)
    private val authorUtils = AuthorUtils(viewModelScope, authorRepository)
    private val _followScreenUIState = MutableStateFlow(FollowScreenUIState())
    val followScreenUIState = _followScreenUIState.asStateFlow()
    fun authors(value: String = "") =
        if (value.isBlank()) authorUtils.getAllAuthors(SortOrders.list.find { it.subType == SubType.AllAuthors }!!) else authorUtils.searchAuthor(
            value
        )

    fun categories(value: String = "") =
        if (value.isBlank()) categoryUtils.getFreeCategories(SortOrders.list.find { it.subType == SubType.FreeCategories }!!) else categoryUtils.searchCategory(
            value
        )

    val dailyCategories = categoryUtils.getDailyCategories()
    val followingCategories = categoryUtils.getFollowingCategories()
    val followingAuthors = authorUtils.getFollowingAuthors()

    fun onCategoryValueChange(value: String = "") {
        _followScreenUIState.update {
            it.copy(categoryValue = value)
        }
    }

    init {
        onCategoryValueChange()
    }

    fun onAuthorValueChange(value: String = _followScreenUIState.value.authorValue) {
        _followScreenUIState.update {
            it.copy(
                authorValue = value
            )
        }
    }

    fun updateScreenIndex(index: Int) = _followScreenUIState.update { it.copy(screenIndex = index) }
    fun updateCategory(category: Category) = categoryUtils.updateCategory(category)
    fun updateAuthor(author: Author) = authorUtils.updateAuthor(author)
}