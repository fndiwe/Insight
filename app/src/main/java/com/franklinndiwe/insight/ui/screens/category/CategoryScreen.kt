package com.franklinndiwe.insight.ui.screens.category

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.Category
import com.franklinndiwe.insight.ui.components.AppLoadingIndicator
import com.franklinndiwe.insight.ui.components.CategoryCard
import com.franklinndiwe.insight.ui.components.ReusableLazyVerticalGrid
import com.franklinndiwe.insight.utils.categoryUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    modifier: Modifier = Modifier,
    categories: Flow<PagingData<Category>>,
    isLike: Boolean = true,
    isDailyQuote: Boolean = false,
    onLikeOrFollow: categoryUnit,
    onClick: categoryUnit,
    unlock: (Category, Int) -> Unit = { _: Category, _: Int -> },
    getCount: (Int) -> Flow<Int> = { emptyFlow() },
    onDelete: (categoryUnit)? = null,
) {
    val categoriesPagingItems = categories.collectAsLazyPagingItems()
    PullToRefreshBox(
        isRefreshing = categoriesPagingItems.loadState.source.refresh is LoadState.Loading,
        onRefresh = { categoriesPagingItems.refresh() },
        modifier.fillMaxSize()
    ) {
        val scope = rememberCoroutineScope()
        if (categoriesPagingItems.itemCount >= 1) ReusableLazyVerticalGrid {
            items(categoriesPagingItems.itemCount,
                key = categoriesPagingItems.itemKey { it.id }) { index ->
                val category = categoriesPagingItems[index]
                if (category != null) {
                    CategoryCard(text = category.name.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.ROOT
                        ) else it.toString()
                    },
                        category.userGenerated != null,
                        category.unlocked,
                        isLike,
                        if (isLike) category.liked != null else if (isDailyQuote) category.daily else category.following,
                        {
                            if (isLike) onLikeOrFollow(category.copy(liked = if (it) System.currentTimeMillis() else null)) else if (isDailyQuote) onLikeOrFollow(
                                category.copy(daily = it)
                            ) else onLikeOrFollow(
                                category.copy(following = it)
                            )
                        },
                        { if (category.unlocked) onClick(category) },
                        {
                            scope.launch {
                                unlock(
                                    category.copy(unlocked = true),
                                    getCount(category.id).first()
                                )
                            }

                        },
                        { onDelete?.invoke(category) })
                }
            }
            item(span = StaggeredGridItemSpan.FullLine) {
                if (categoriesPagingItems.loadState.append is LoadState.Loading) {
                    AppLoadingIndicator(modifier = Modifier.padding(16.dp))
                }
            }
        }
        else if (categoriesPagingItems.loadState.source.refresh is LoadState.NotLoading && categoriesPagingItems.itemCount < 1) {
            Text(
                text = stringResource(id = R.string.nothing_to_show),
                Modifier.align(Alignment.Center)
            )
        }
    }
}
