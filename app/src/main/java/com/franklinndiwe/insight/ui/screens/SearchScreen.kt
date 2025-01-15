package com.franklinndiwe.insight.ui.screens

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.ui.components.QuoteText
import com.franklinndiwe.insight.ui.screens.author.AuthorScreen
import com.franklinndiwe.insight.ui.screens.category.CategoryScreen
import com.franklinndiwe.insight.utils.AppUtils.likedQuote
import com.franklinndiwe.insight.utils.SortOrderType
import com.franklinndiwe.insight.utils.SortOrderType.Author
import com.franklinndiwe.insight.utils.SortOrderType.Category
import com.franklinndiwe.insight.utils.SortOrderType.Quote
import com.franklinndiwe.insight.utils.authorUnit
import com.franklinndiwe.insight.utils.categoryUnit
import com.franklinndiwe.insight.viewmodel.AppViewModelProvider
import com.franklinndiwe.insight.viewmodel.SearchScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    context: Context,
    sortOrderType: SortOrderType,
    onClickCategory: categoryUnit,
    onClickAuthor: authorUnit,
    navigateBack: () -> Unit,
    searchScreenViewModel: SearchScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val uiState by searchScreenViewModel.searchUIState.collectAsStateWithLifecycle()
    Scaffold(topBar = {
        TopAppBar(title = {
            SearchField(
                value = uiState.value, placeholder = "${stringResource(id = R.string.search)} ${
                    stringResource(
                        id = when (sortOrderType) {
                            Quote -> R.string.quote
                            Category -> R.string.category
                            Author -> R.string.author
                        }
                    ).lowercase()
                }"
            ) { searchScreenViewModel.onValueChange(it) }
        }, navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = stringResource(
                        id = R.string.go_back
                    )
                )
            }
        })
    }) { paddingValues ->
        when (sortOrderType) {
            Quote -> {
                val quotes = remember(uiState.value) {
                    searchScreenViewModel.quotes(uiState.value)
                }.collectAsLazyPagingItems()
                PullToRefreshBox(
                    isRefreshing = quotes.loadState.refresh is LoadState.Loading,
                    onRefresh = { quotes.refresh() },
                    Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    if (quotes.itemCount >= 1) LazyColumn(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(quotes.itemCount, quotes.itemKey { it.quote.id }) {
                            val quoteV2 = quotes[it]
                            if (quoteV2 != null) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    QuoteText(
                                        quote = quoteV2.quote.text,
                                        author = quoteV2.author.name,
                                        isDailyQuote = true
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        val liked = quoteV2.quote.liked != null
                                        IconButton(onClick = {
                                            searchScreenViewModel.updateQuote(
                                                likedQuote(quoteV2)
                                            )
                                        }) {
                                            Icon(
                                                imageVector = if (liked) Icons.Rounded.ThumbUp else Icons.Outlined.ThumbUp,
                                                contentDescription = stringResource(id = if (liked) R.string.unlike_quote else R.string.like_quote)
                                            )
                                        }
                                        IconButton(onClick = {
                                            searchScreenViewModel.shareQuote(
                                                context, quoteV2
                                            )
                                        }) {
                                            Icon(
                                                imageVector = ImageVector.vectorResource(R.drawable.share),
                                                contentDescription = stringResource(id = R.string.share)
                                            )
                                        }
                                    }
                                    if (it != quotes.itemCount - 1) HorizontalDivider()
                                }
                            }
                        }
                    }
                    else if (quotes.loadState.source.refresh is LoadState.NotLoading && quotes.itemCount < 1) Text(
                        text = stringResource(id = R.string.nothing_to_show),
                        Modifier.align(Alignment.Center)
                    )
                }
            }

            Category -> CategoryScreen(
                Modifier.padding(paddingValues),
                categories = remember(uiState.value) {
                    searchScreenViewModel.categories(uiState.value)
                },
                onLikeOrFollow = { searchScreenViewModel.updateCategory(it) },
                onClick = onClickCategory
            )

            Author -> AuthorScreen(
                Modifier.padding(paddingValues),
                authors = remember(uiState.value) {
                    searchScreenViewModel.authors(uiState.value)
                },
                onLikeOrFollow = { searchScreenViewModel.updateAuthor(it) },
                onClick = onClickAuthor
            )
        }
    }
}

@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    isFollow: Boolean = false,
    value: String,
    placeholder: String = stringResource(R.string.search),
    onValueChange: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    LaunchedEffect(key1 = Unit) {
        if (!isFollow) focusManager.moveFocus(FocusDirection.Next)
    }
    if (!isFollow) TextField(value = value,
        onValueChange = onValueChange,
        modifier.fillMaxWidth(),
        shape = CircleShape,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent, focusedContainerColor = Color.Transparent
        ),
        trailingIcon = {
            if (value.isNotEmpty()) FilledTonalIconButton(
                onClick = { onValueChange("") }, modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        singleLine = true,
        keyboardActions = KeyboardActions(onSearch = {
            focusManager.clearFocus(true)
            keyboardController?.hide()
        }),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        placeholder = { Text(text = placeholder) })
    else OutlinedTextField(value = value,
        onValueChange = onValueChange,
        modifier.fillMaxWidth(),
        shape = CircleShape,
        trailingIcon = {
            if (value.isNotEmpty()) FilledTonalIconButton(
                onClick = { onValueChange("") }, modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        },
        singleLine = true,
        keyboardActions = KeyboardActions(onSearch = {
            focusManager.clearFocus(true)
            keyboardController?.hide()
        }),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        placeholder = { Text(text = placeholder) })
}