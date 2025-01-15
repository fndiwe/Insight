package com.franklinndiwe.insight.viewmodel.quote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.franklinndiwe.insight.data.Author
import com.franklinndiwe.insight.data.Category
import com.franklinndiwe.insight.data.Quote
import com.franklinndiwe.insight.data.Suggestion
import com.franklinndiwe.insight.repository.AuthorRepository
import com.franklinndiwe.insight.repository.CategoryRepository
import com.franklinndiwe.insight.repository.QuoteRepository
import com.franklinndiwe.insight.ui.states.CreateQuoteUIState
import com.franklinndiwe.insight.utils.AuthorUtils
import com.franklinndiwe.insight.utils.CategoryUtils
import com.franklinndiwe.insight.utils.QuoteUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateQuoteViewModel(
    categoryRepository: CategoryRepository,
    authorRepository: AuthorRepository,
    quoteRepository: QuoteRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateQuoteUIState())
    val uiState = _uiState.asStateFlow()
    val categoryUtils = CategoryUtils(viewModelScope, categoryRepository)
    val authorUtils = AuthorUtils(viewModelScope, authorRepository)
    val quoteUtils = QuoteUtils(viewModelScope, quoteRepository)
    fun categorySuggestions(value: String) = categoryUtils.categorySuggestions(value)
    fun authorSuggestions(value: String) = authorUtils.authorSuggestions(value)
    fun onChangeCategoryValue(value: Suggestion) = _uiState.update { it.copy(category = value) }
    fun onChangeCategory2Value(value: Suggestion) = _uiState.update { it.copy(category2 = value) }
    fun onChangeAuthorValue(value: Suggestion) = _uiState.update { it.copy(author = value) }
    fun onChangeQuoteValue(value: String) =
        _uiState.update { it.copy(quote = value, isError = false) }

    private suspend fun checkAndCreateCategory(category: Suggestion): Int {
        //Check if user clicked on a suggestion
        val categoryId = category.id
        //If user didn't click on a suggestion
        return if (categoryId == 0) {
            //Check if the inputted category exists
            categoryUtils.getCategoryId(category.name.trim()).await()
                ?: //Create new category
                categoryUtils.insertCategory(
                    Category(
                        name = category.name.lowercase(),
                        unlocked = true,
                        popular = false,
                        userGenerated = System.currentTimeMillis()
                    )
                ).await().toInt()
            //If the category suggestions does contain a suggestion
            //Return the existing category id
        } else categoryId
    }

    private suspend fun checkAndCreateAuthor(author: Suggestion): Int {
        //Check if user clicked on a suggestion
        val authorId = author.id
        //If user didn't click on a suggestion
        return if (authorId == 0) {
            //Check if the inputted category exists
            authorUtils.getAuthorId(author.name.trim()).await()
                ?: //Create new author
                authorUtils.insertAuthor(
                    Author(
                        name = author.name,
                        popular = false,
                        userGenerated = System.currentTimeMillis()
                    )
                ).await().toInt()
            //If the author suggestions does contain a suggestion
        } else authorId
    }

    fun submit(finishAction: () -> Unit) {
        viewModelScope.launch {
            if (!quoteUtils.existingQuote(_uiState.value.quote)) {
                //Check for existing category, if not create new one.
                val categoryId1 = checkAndCreateCategory(_uiState.value.category)
                val category2 = _uiState.value.category2
                val categoryId2 =
                    if (category2.name.isNotBlank()) checkAndCreateCategory(category2) else null
                //Check for existing author, if not create new one.
                val authorId = checkAndCreateAuthor(_uiState.value.author)
                //Insert quote
                quoteUtils.insertQuote(
                    Quote(
                        text = _uiState.value.quote,
                        categoryId1 = categoryId1,
                        categoryId2 = categoryId2,
                        authorId = authorId,
                        userGenerated = System.currentTimeMillis()
                    )
                )
                //Perform finishAction
                finishAction()
            } else {
                _uiState.update { it.copy(isError = true) }
            }
        }
    }
}