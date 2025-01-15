package com.franklinndiwe.insight.ui.screens.quote

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.utils.sortOrderTypeUnit
import com.franklinndiwe.insight.utils.unit
import com.franklinndiwe.insight.viewmodel.AppViewModelProvider
import com.franklinndiwe.insight.viewmodel.quote.AuthorQuotesViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AuthorQuotesScreen(
    authorId: Int,
    setting: Setting,
    writeAccessState: MultiplePermissionsState,
    onSearch: sortOrderTypeUnit,
    onCreateQuote: unit,
    navigateBack: unit,
    authorQuotesViewModel: AuthorQuotesViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val quoteScreenImpl = authorQuotesViewModel.quoteScreenImpl
    val quoteUtils = quoteScreenImpl.quoteUtils
    val sortOrder = quoteScreenImpl.sortOrder?.collectAsStateWithLifecycle()?.value
    val authorUtils = authorQuotesViewModel.authorUtils
    val author by authorUtils.getAuthorById(authorId)
        .collectAsStateWithLifecycle(initialValue = null)
    if (author != null && sortOrder != null) {
        ReusableQuoteScreen(
            author!!.name,
            quoteScreenImpl,
            writeAccessState,
            quoteUtils.getQuotesForAuthor(authorId, sortOrder),
            setting,
            sortOrder,
            navigateBack,
            onSearch,
            onCreateQuote,
            numberOfQuotes = authorUtils.getQuoteCount(authorId).collectAsStateWithLifecycle(
                initialValue = 0
            ).value
        )
    }
}