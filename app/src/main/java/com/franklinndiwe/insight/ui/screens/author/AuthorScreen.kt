package com.franklinndiwe.insight.ui.screens.author

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.Author
import com.franklinndiwe.insight.ui.components.AppLoadingIndicator
import com.franklinndiwe.insight.ui.components.CategoryCard
import com.franklinndiwe.insight.ui.components.ReusableLazyVerticalGrid
import com.franklinndiwe.insight.utils.authorUnit
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorScreen(
    modifier: Modifier = Modifier,
    authors: Flow<PagingData<Author>>,
    isLike: Boolean = true,
    onLikeOrFollow: authorUnit,
    onClick: authorUnit,
    onDelete: (authorUnit)? = null,
) {
    val authorsPagingItems = authors.collectAsLazyPagingItems()
    PullToRefreshBox(
        isRefreshing = authorsPagingItems.loadState.refresh is LoadState.Loading,
        onRefresh = { authorsPagingItems.refresh() }, modifier.fillMaxSize()
    ) {
        if (authorsPagingItems.itemCount >= 1) ReusableLazyVerticalGrid {
            items(authorsPagingItems.itemCount,
                key = authorsPagingItems.itemKey { it.id }) { index ->
                val author = authorsPagingItems[index]
                if (author != null) {
                    CategoryCard(text = author.name,
                        userGenerated = author.userGenerated != null,
                        isLike = isLike,
                        likedOrFollowing = if (isLike) author.liked != null else author.following,
                        onLikeOrFollow = {
                            if (isLike) onLikeOrFollow(author.copy(liked = if (it) System.currentTimeMillis() else null)) else onLikeOrFollow(
                                author.copy(following = it)
                            )
                        },
                        onClick = { onClick(author) },
                        onDelete = { onDelete?.invoke(author) })
                }
            }
            item {
                if (authorsPagingItems.loadState.append is LoadState.Loading) {
                    AppLoadingIndicator(modifier = Modifier.padding(16.dp))
                }
            }
        }
        else if (authorsPagingItems.loadState.source.refresh is LoadState.NotLoading && authorsPagingItems.itemCount < 1) Text(
            text = stringResource(id = R.string.nothing_to_show),
            Modifier.align(Alignment.Center)
        )
    }
}