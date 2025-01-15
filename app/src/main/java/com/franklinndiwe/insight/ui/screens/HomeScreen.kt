package com.franklinndiwe.insight.ui.screens

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.franklinndiwe.insight.AppRoutes
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.HomeItem
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.ui.components.AppLoadingIndicator
import com.franklinndiwe.insight.ui.components.AppTopAppBar
import com.franklinndiwe.insight.ui.components.HomeCard
import com.franklinndiwe.insight.ui.components.Quota
import com.franklinndiwe.insight.ui.components.QuotaRenewScaffold
import com.franklinndiwe.insight.ui.components.QuoteText
import com.franklinndiwe.insight.utils.AppUtils
import com.franklinndiwe.insight.utils.FollowScreenOption
import com.franklinndiwe.insight.utils.unit
import com.franklinndiwe.insight.viewmodel.AppViewModelProvider
import com.franklinndiwe.insight.viewmodel.HomeViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun HomeScreen(
    context: Context,
    setting: Setting,
    navigateToSettings: () -> Unit,
    navigateToFollowScreen: (FollowScreenOption) -> Unit,
    navigateToScreen: (String) -> Unit,
    onCreateQuote: unit,
    onSearch: (String) -> Unit,
    homeViewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    var showQuotaDialog by remember {
        mutableStateOf(false)
    }
    val dailyQuote by homeViewModel.dailyQuote.collectAsStateWithLifecycle()
    val quota by homeViewModel.quotaUtils.quota.collectAsState(initial = 0)
    val adLoadingState = homeViewModel.quotaUtils.adLoadingState
    val postNotificationsState = rememberPermissionState(
        permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else ""
    ) {
        // If the user does not accept the request to post notifications turn off daily quote reminder and cancel schedule or vise versa
        homeViewModel.updateDailyQuoteReminder(context, setting, it)
    }
    LaunchedEffect(key1 = dailyQuote == null) {
        if (!homeViewModel.generateDailyQuote().await()) navigateToFollowScreen(
            FollowScreenOption.IS_HOME
        )
    }
    LaunchedEffect(key1 = dailyQuote, setting.dailyQuoteReminderEnabled) {
        if (dailyQuote != null && setting.dailyQuoteReminderEnabled) {
            homeViewModel.runDailyQuoteReminderCheck(
                context,
                setting = setting,
                permissionsState = postNotificationsState,
                snackbarHostState = snackbarHostState
            ).await()
        }
    }

    QuotaRenewScaffold(context,
        adLoadingState,
        snackbarHostState,
        scrollBehavior.nestedScrollConnection,
        { homeViewModel.quotaUtils.watchAd(context) },
        showQuotaDialog,
        { showQuotaDialog = false },
        isInsufficient = false,
        topBar = {
            AppTopAppBar(title = stringResource(R.string.app_name),
                true,
                scrollBehavior = scrollBehavior,
                actions = {
                    Quota(quota = quota) {
                        showQuotaDialog = true
                    }
                    IconButton(onClick = navigateToSettings) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = stringResource(
                                id = R.string.settings
                            ),
                            Modifier.size(24.dp)
                        )
                    }
                })
        }) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Daily Quote Section
            item {
                if (dailyQuote != null) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(id = R.string.daily_quote),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Row {
                                val liked = dailyQuote!!.quote.liked != null
                                IconButton(onClick = {
                                    val quote = AppUtils.likedQuote(
                                        dailyQuote!!
                                    )
                                    homeViewModel.quoteUtils.updateQuote(
                                        quote
                                    )
                                    homeViewModel.updateQuote(quote)
                                }) {
                                    Icon(
                                        imageVector = if (liked) Icons.Rounded.ThumbUp else Icons.Outlined.ThumbUp,
                                        contentDescription = stringResource(id = if (liked) R.string.unlike_quote else R.string.like_quote)
                                    )
                                }

                                IconButton(onClick = {
                                    homeViewModel.quoteUtils.shareQuote(
                                        context, dailyQuote!!
                                    )
                                }) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(R.drawable.share),
                                        contentDescription = stringResource(id = R.string.share)
                                    )
                                }
                            }

                        }
                        QuoteText(
                            quote = dailyQuote!!.quote.text,
                            author = dailyQuote!!.author.name,
                            isDailyQuote = true
                        )
                    }
                } else Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AppLoadingIndicator()
                }
            }
            val quoteItems = Pair(
                R.string.quotes, listOf(
                    HomeItem(
                        R.drawable.following, R.string.following, AppRoutes.FollowingQuotes.name
                    ),
                    HomeItem(R.drawable.liked, R.string.liked, AppRoutes.LikedQuotes.name),
                    HomeItem(
                        R.drawable.user, R.string.personal, AppRoutes.PersonalQuotes.name
                    ),
                    HomeItem(
                        R.drawable.theme, R.string.themes, AppRoutes.Themes.name
                    ),
                )
            )
            val categoryItems = Pair(
                R.string.categories, listOf(
                    HomeItem(
                        R.drawable.popular, R.string.popular, AppRoutes.PopularCategories.name
                    ), HomeItem(
                        R.drawable.liked, R.string.liked, AppRoutes.LikedCategories.name
                    ), HomeItem(
                        R.drawable.user, R.string.personal, AppRoutes.PersonalCategories.name
                    ), HomeItem(
                        R.drawable.category, R.string.all, AppRoutes.FreeCategories.name
                    ), HomeItem(
                        R.drawable.premium, R.string.premium, AppRoutes.PremiumCategories.name
                    )
                )
            )
            val authorItems = Pair(
                R.string.authors, listOf(
                    HomeItem(
                        R.drawable.popular, R.string.popular, AppRoutes.PopularAuthors.name
                    ), HomeItem(
                        R.drawable.liked, R.string.liked, AppRoutes.LikedAuthors.name
                    ), HomeItem(
                        R.drawable.user, R.string.personal, AppRoutes.PersonalAuthors.name
                    ), HomeItem(R.drawable.authors, R.string.all, AppRoutes.AllAuthors.name)
                )
            )
            val homeItems = listOf(quoteItems, authorItems, categoryItems)
            items(homeItems) { listPair ->
                HomeCard(title = listPair.first,
                    list = listPair.second,
                    onClickHomeItem = { navigateToScreen(it) },
                    onCreateQuote = onCreateQuote,
                    onSearch = { onSearch(it.name) })
            }
        }
    }
}
