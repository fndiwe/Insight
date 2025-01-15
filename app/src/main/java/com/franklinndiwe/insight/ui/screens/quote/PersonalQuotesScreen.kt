package com.franklinndiwe.insight.ui.screens.quote

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.utils.sortOrderTypeUnit
import com.franklinndiwe.insight.utils.unit
import com.franklinndiwe.insight.viewmodel.AppViewModelProvider
import com.franklinndiwe.insight.viewmodel.quote.PersonalQuotesViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PersonalQuotesScreen(
    setting: Setting,
    writeAccessState: MultiplePermissionsState,
    personalQuotesViewModel: PersonalQuotesViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onSearch: sortOrderTypeUnit,
    onCreateQuote: unit,
    navigateBack: unit,
) {
    val quoteScreenImpl = personalQuotesViewModel.quoteScreenImpl
    val quoteUtils = quoteScreenImpl.quoteUtils
    val sortOrder = quoteScreenImpl.sortOrder?.collectAsStateWithLifecycle()?.value
    sortOrder?.let { quoteUtils.getPersonalQuotes(it) }?.let {
        ReusableQuoteScreen(
            stringResource(id = R.string.personal),
            quoteScreenImpl,
            writeAccessState,
            it,
            setting,
            sortOrder,
            navigateBack,
            onSearch,
            onCreateQuote
        )
    }
}