package com.franklinndiwe.insight.ui.screens.quote

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.utils.FollowScreenOption
import com.franklinndiwe.insight.utils.sortOrderTypeUnit
import com.franklinndiwe.insight.utils.unit
import com.franklinndiwe.insight.viewmodel.AppViewModelProvider
import com.franklinndiwe.insight.viewmodel.quote.ForYouQuotesViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ForYouQuotesScreen(
    setting: Setting,
    writeAccessState: MultiplePermissionsState,
    forYouQuotesViewModel: ForYouQuotesViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onSearch: sortOrderTypeUnit,
    onNavigateToForYouSettings: (FollowScreenOption) -> Unit,
    navigateBack: unit,
) {
    val quoteScreenImpl = forYouQuotesViewModel.quoteScreenImpl
    val quotes by forYouQuotesViewModel.quotes.collectAsStateWithLifecycle(emptyList())
    val scope = rememberCoroutineScope()
    val followingCategoriesCount = forYouQuotesViewModel.followingCategoryCount
    val followingAuthorsCount = forYouQuotesViewModel.followingAuthorsCount
    var previousFollowingCategoriesCount by rememberSaveable {
        mutableIntStateOf(0)
    }
    var previousFollowingAuthorsCount by rememberSaveable {
        mutableIntStateOf(0)
    }
    // Refreshes quotes if the list is empty or any category or author was followed.
    LaunchedEffect(key1 = Unit) {
        val categoryCount = followingCategoriesCount.first()
        val authorCount = followingAuthorsCount.first()
        if (previousFollowingCategoriesCount == 0) previousFollowingCategoriesCount = categoryCount
        if (previousFollowingAuthorsCount == 0) previousFollowingAuthorsCount = authorCount
        if (quotes.isEmpty() || categoryCount != previousFollowingCategoriesCount || authorCount != previousFollowingAuthorsCount) forYouQuotesViewModel.refreshQuotes()
    }
    ReusableQuoteScreen(
        stringResource(id = R.string.following),
        quoteScreenImpl,
        writeAccessState,
        null,
        setting,
        null,
        navigateBack,
        onSearch,
        null,
        {
            scope.launch {
                previousFollowingCategoriesCount = followingCategoriesCount.first()
            }
            onNavigateToForYouSettings(FollowScreenOption.IS_FOR_YOU)
        },
        forYouQuotes = quotes,
        refreshingForYouQuotes = forYouQuotesViewModel.refreshingQuotes.collectAsStateWithLifecycle().value
    ) { forYouQuotesViewModel.refreshQuotes() }
}