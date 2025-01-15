package com.franklinndiwe.insight.ui.screens.quote

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.utils.sortOrderTypeUnit
import com.franklinndiwe.insight.utils.unit
import com.franklinndiwe.insight.viewmodel.AppViewModelProvider
import com.franklinndiwe.insight.viewmodel.quote.CategoryQuotesViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CategoryQuotesScreen(
    categoryId: Int,
    setting: Setting,
    writeAccessState: MultiplePermissionsState,
    onSearch: sortOrderTypeUnit,
    onCreateQuote: unit,
    navigateBack: unit,
    categoryQuotesViewModel: CategoryQuotesViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val quoteScreenImpl = categoryQuotesViewModel.quoteScreenImpl
    val quoteUtils = quoteScreenImpl.quoteUtils
    val sortOrder = quoteScreenImpl.sortOrder?.collectAsStateWithLifecycle()?.value
    val categoryUtils = categoryQuotesViewModel.categoryUtils
    val category by categoryUtils.getCategoryById(categoryId)
        .collectAsStateWithLifecycle(initialValue = null)
    if (category != null && sortOrder != null) {
        ReusableQuoteScreen(
            category!!.name,
            quoteScreenImpl,
            writeAccessState,
            quoteUtils.getQuotesForCategory(categoryId, sortOrder),
            setting,
            sortOrder,
            navigateBack,
            onSearch,
            onCreateQuote,
            numberOfQuotes = categoryUtils.getQuoteCount(categoryId).collectAsStateWithLifecycle(
                initialValue = 0
            ).value
        )
    }
}