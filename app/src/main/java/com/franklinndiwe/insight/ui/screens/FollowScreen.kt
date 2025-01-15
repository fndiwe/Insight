package com.franklinndiwe.insight.ui.screens

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.filter
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.AdaptiveNavBarScreenData
import com.franklinndiwe.insight.data.MenuItem
import com.franklinndiwe.insight.ui.components.SlidingTabs
import com.franklinndiwe.insight.ui.screens.author.AuthorScreen
import com.franklinndiwe.insight.ui.screens.category.CategoryScreen
import com.franklinndiwe.insight.utils.FollowScreenOption
import com.franklinndiwe.insight.utils.unit
import com.franklinndiwe.insight.viewmodel.AppViewModelProvider
import com.franklinndiwe.insight.viewmodel.FollowScreenViewModel
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowScreen(
    followScreenOption: FollowScreenOption,
    followScreenViewModel: FollowScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
    onNavigateBack: unit,
    onFinish: unit,
) {
    val isDailyQuote = when (followScreenOption) {
        FollowScreenOption.IS_FOR_YOU -> false
        else -> true
    }
    val dailyQuoteCategories =
        if (isDailyQuote) followScreenViewModel.dailyCategories.collectAsLazyPagingItems() else null
    val followingCategories =
        if (followScreenOption == FollowScreenOption.IS_FOR_YOU) followScreenViewModel.followingCategories.collectAsLazyPagingItems() else null
    val followingAuthors =
        if (followScreenOption == FollowScreenOption.IS_FOR_YOU) followScreenViewModel.followingAuthors.collectAsLazyPagingItems() else null
    val enabled =
        if (dailyQuoteCategories != null) dailyQuoteCategories.itemCount >= 1 else if (followingCategories != null && followingAuthors != null) followingCategories.itemCount >= 1 || followingAuthors.itemCount >= 1 else true
    val lastIndex = 2
    val textList = listOf(
        if (isDailyQuote) R.string.choose_categories_for_daily_quote else R.string.category_follow_question,
        R.string.author_follow_question,
        R.string.you_are_set
    )
    val followScreenUIState by followScreenViewModel.followScreenUIState.collectAsState()
    val currentScreenIndex = followScreenUIState.screenIndex
    val scrollBehavior =
        if (currentScreenIndex == lastIndex) TopAppBarDefaults.pinnedScrollBehavior() else TopAppBarDefaults.enterAlwaysScrollBehavior()

    @Composable
    fun SearchBar(index: Int = currentScreenIndex) {
        if (index != lastIndex) SearchField(
            Modifier.padding(top = 16.dp, end = 16.dp, start = 16.dp, bottom = 8.dp),
            isFollow = true,
            value = if (index == 0) followScreenUIState.categoryValue else followScreenUIState.authorValue
        ) {
            if (index == 0) followScreenViewModel.onCategoryValueChange(it) else followScreenViewModel.onAuthorValueChange(
                it
            )
        }
    }

    val categorySlidingTabData = listOf(AdaptiveNavBarScreenData(
        MenuItem(0, name = R.string.all, route = "all")
    ) {
        Column {
            SearchBar()
            CategoryScreen(categories = remember(followScreenUIState.categoryValue) {
                followScreenViewModel.categories(followScreenUIState.categoryValue)
                    .map { pagingData -> pagingData.filter { it.userGenerated == null } }
            },
                isLike = false,
                isDailyQuote = isDailyQuote,
                onLikeOrFollow = { followScreenViewModel.updateCategory(it) },
                onClick = {})
        }

    }, AdaptiveNavBarScreenData(
        MenuItem(1, name = R.string.following, route = "following")
    ) {
        CategoryScreen(categories = remember {
            followScreenViewModel.followingCategories
        },
            isLike = false,
            isDailyQuote = isDailyQuote,
            onLikeOrFollow = { followScreenViewModel.updateCategory(it) },
            onClick = {})
    })
    val authorSlidingTabs = listOf(AdaptiveNavBarScreenData(
        MenuItem(0, name = R.string.all, route = "all")
    ) {
        Column {
            SearchBar()
            AuthorScreen(authors = remember(followScreenUIState.authorValue) {
                followScreenViewModel.authors(followScreenUIState.authorValue)
                    .map { pagingData -> pagingData.filter { it.userGenerated == null } }
            },
                isLike = false,
                onLikeOrFollow = { followScreenViewModel.updateAuthor(it) },
                onClick = {})
        }
    }, AdaptiveNavBarScreenData(
        MenuItem(1, name = R.string.following, route = "following")
    ) {
        AuthorScreen(authors = remember {
            followScreenViewModel.followingAuthors
        },
            isLike = false,
            onLikeOrFollow = { followScreenViewModel.updateAuthor(it) },
            onClick = {})
    })

    @Composable
    fun topBar(index: Int) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (index != lastIndex) CenterAlignedTopAppBar(title = {
                Text(
                    text = stringResource(id = textList[index]),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Medium, fontSize = 22.sp
                    )
                )
            }, scrollBehavior = scrollBehavior)
        }
    }

    fun updateScreenIndex(isNavigateBack: Boolean) {
        val index =
            if (isNavigateBack) if (!isDailyQuote) currentScreenIndex - 1 else currentScreenIndex - 2 else if (!isDailyQuote) currentScreenIndex + 1 else currentScreenIndex + 2
        followScreenViewModel.updateScreenIndex(index)
    }

    fun navigateBack() {
        if (currentScreenIndex != 0) updateScreenIndex(true) else if (enabled) onNavigateBack()
    }

    fun navigateForward() {
        if (currentScreenIndex != lastIndex) updateScreenIndex(false) else onFinish()
    }

    @Composable
    fun bottomBar(modifier: Modifier = Modifier, index: Int) {
        @Composable
        fun BottomButton(
            modifier: Modifier = Modifier,
            @StringRes text: Int,
            icon: ImageVector?,
            positionIconAtStart: Boolean = true,
            isRight: Boolean = true,
            onClick: () -> Unit,
        ) {
            @Composable
            fun RowItem() {
                if (positionIconAtStart) icon?.let {
                    Icon(
                        imageVector = it, contentDescription = null, Modifier.padding(end = 4.dp)
                    )
                }
                Text(text = stringResource(id = text))
                if (!positionIconAtStart) icon?.let {
                    Icon(
                        imageVector = it, contentDescription = null
                    )
                }
            }

            val buttonModifier = modifier
                .fillMaxWidth(.5f)
                .padding(horizontal = 16.dp)
            if (isRight) {
                Button(enabled = enabled,
                    onClick = onClick,
                    modifier = buttonModifier,
                    contentPadding = PaddingValues(vertical = 8.dp),
                    content = { RowItem() })
            } else ElevatedButton(onClick = onClick,
                buttonModifier,
                contentPadding = PaddingValues(vertical = 8.dp),
                content = { RowItem() })
        }
        Row(modifier) {
            if (index > 0) BottomButton(Modifier.weight(1f),
                text = R.string.back,
                icon = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                isRight = false,
                onClick = { navigateBack() })
            BottomButton(text = if (index != lastIndex) R.string.next else R.string.finish,
                icon = if (index != lastIndex) Icons.AutoMirrored.Rounded.KeyboardArrowRight else null,
                positionIconAtStart = index == lastIndex,
                onClick = { navigateForward() })
        }

    }
    Scaffold(Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = { topBar(index = currentScreenIndex) }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedContent(targetState = currentScreenIndex,
                    label = "",
                    modifier = Modifier.weight(1f),
                    transitionSpec = {
                        ContentTransform(
                            targetContentEnter = fadeIn(tween(300, 300)),
                            initialContentExit = fadeOut(tween(300))
                        )
                    }) { index ->
                    when (index) {
                        0 -> {
                            if (isDailyQuote) Column {
                                SearchBar()
                                CategoryScreen(categories = remember(
                                    followScreenUIState.categoryValue
                                ) {
                                    followScreenViewModel.categories(followScreenUIState.categoryValue)
                                },
                                    isLike = false,
                                    isDailyQuote = true,
                                    onLikeOrFollow = { followScreenViewModel.updateCategory(it) },
                                    onClick = {})
                            } else SlidingTabs(screens = remember { categorySlidingTabData }) {
                                navigateBack()
                            }
                        }

                        1 -> {
                            SlidingTabs(screens = remember { authorSlidingTabs }) {
                                navigateBack()
                            }
                        }

                        else -> {
                            Column(
                                Modifier.offset(y = 60.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (index == lastIndex) Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = null,
                                    Modifier.size(30.dp)
                                )
                                Text(
                                    text = stringResource(id = textList[index]),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Medium, fontSize = 30.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
            bottomBar(
                Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-16).dp), currentScreenIndex
            )
        }
    }
    BackHandler(onBack = { navigateBack() })
}