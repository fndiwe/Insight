package com.franklinndiwe.insight.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.paging.PagingData
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.AppDropdownMenuItem
import com.franklinndiwe.insight.data.Author
import com.franklinndiwe.insight.data.Category
import com.franklinndiwe.insight.data.Quote
import com.franklinndiwe.insight.data.QuoteV2
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.data.SortOrder
import com.franklinndiwe.insight.ui.components.AppDropdownMenu
import com.franklinndiwe.insight.ui.components.AppTopAppBar
import com.franklinndiwe.insight.ui.components.QuotaRenewScaffold
import com.franklinndiwe.insight.ui.components.ReusableQuoteScreen
import com.franklinndiwe.insight.ui.screens.author.AuthorScreen
import com.franklinndiwe.insight.ui.screens.category.CategoryScreen
import com.franklinndiwe.insight.utils.AppFontUtils
import com.franklinndiwe.insight.utils.AppUtils.DownloadQuoteQuota
import com.franklinndiwe.insight.utils.AppUtils.categoryQuota
import com.franklinndiwe.insight.utils.AuthorUtils
import com.franklinndiwe.insight.utils.CategoryUtils
import com.franklinndiwe.insight.utils.GradientUtils
import com.franklinndiwe.insight.utils.QuotaUtils
import com.franklinndiwe.insight.utils.QuoteImageUtils
import com.franklinndiwe.insight.utils.QuoteUtils
import com.franklinndiwe.insight.utils.SolidColorUtils
import com.franklinndiwe.insight.utils.SortOrderType
import com.franklinndiwe.insight.utils.SortOrders
import com.franklinndiwe.insight.utils.SubType
import com.franklinndiwe.insight.utils.SwipeDirection
import com.franklinndiwe.insight.utils.Theme
import com.franklinndiwe.insight.utils.sortOrderTypeUnit
import com.franklinndiwe.insight.utils.unit
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Locale

/**
 * Reusable screen for displaying quotes, categories and authors
 * @param name The title of the screen
 * @param setting The [Setting] class for accessing some app general properties
 * @param onAdd Navigates to where personal quotes are added.
 * @param sortOrder [SortOrder] for quotes, categories or authors.
 * @param changeSortOrder Function that changes the [sortOrder]
 * @param navigateBack Function that navigates Up
 * @param onSearch Navigates to search screen using the provided [SortOrderType]
 * @param onNavigateToForYouSettings Navigates to the screen for following and unfollowing categories and authors
 * @param updateSetting Updates App's general settings
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ReusableViewScreen(
    name: String,
    numberOfQuotes: Int? = null,
    quotes: Flow<PagingData<QuoteV2>>? = null,
    forYouQuotes: List<Int> = emptyList(),
    categories: Flow<PagingData<Category>>? = null,
    authors: Flow<PagingData<Author>>? = null,
    colorUtils: SolidColorUtils? = null,
    quoteImageUtils: QuoteImageUtils? = null,
    gradientUtils: GradientUtils? = null,
    quotaUtils: QuotaUtils,
    fontUtils: AppFontUtils? = null,
    setting: Setting,
    onAdd: unit? = null,
    refreshingForYouQuotes: Boolean = false,
    refreshForYouQuotes: unit = {},
    sortOrder: SortOrder?,
    changeSortOrder: (SortOrder) -> Unit,
    quoteUtils: QuoteUtils? = null,
    categoryUtils: CategoryUtils? = null,
    authorUtils: AuthorUtils? = null,
    onClickCategory: ((Category) -> Unit)? = null,
    onClickAuthor: ((Author) -> Unit)? = null,
    navigateBack: unit,
    onSearch: sortOrderTypeUnit,
    writeAccessState: MultiplePermissionsState? = null,
    onNavigateToForYouSettings: unit? = null,
    updateSetting: (Setting) -> Unit,
) {
    val quota by quotaUtils.quota.collectAsState(initial = 0)
    val context = LocalContext.current
    val sortOrderType = sortOrder?.type ?: SortOrderType.Quote
    val scrollBehavior =
        TopAppBarDefaults.enterAlwaysScrollBehavior(canScroll = { if (quotes != null || forYouQuotes.isNotEmpty()) setting.swipeDirection == SwipeDirection.Vertical else true })
    var showThemesDropdown by rememberSaveable {
        mutableStateOf(false)
    }
    var showSortDropdown by rememberSaveable {
        mutableStateOf(false)
    }
    // Show default options for all screens
    var showGeneralDropdown by rememberSaveable {
        mutableStateOf(false)
    }
    var showQuotaDialog by rememberSaveable {
        mutableStateOf(false)
    }
    // Show the dialog with question whether to perform purchase operation
    var showPurchaseItemDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    fun performPurchaseOperation(quotaAmount: Int, operation: unit) {
        if (quotaUtils.shouldPerformOperation(
                quota, quotaAmount
            )
        ) {
            operation()
        } else {
            showQuotaDialog = true
        }
    }

    var category by rememberSaveable(stateSaver = Saver(save = {
        it?.let { Json.encodeToString(it) }
    }, restore = {
        Json.decodeFromString<Category>(it)
    })) {
        mutableStateOf<Category?>(null)
    }
    var quote by rememberSaveable(stateSaver = Saver(save = {
        it?.let { Json.encodeToString(it) }
    }, restore = {
        Json.decodeFromString<Quote>(it)
    })) {
        mutableStateOf<Quote?>(null)
    }
    var author by rememberSaveable(stateSaver = Saver(save = {
        it?.let { Json.encodeToString(it) }
    }, restore = {
        Json.decodeFromString<Author>(it)
    })) {
        mutableStateOf<Author?>(null)
    }
    var showDeleteAlertDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var text by rememberSaveable {
        mutableStateOf("")
    }
    var noOfQuotes by rememberSaveable {
        mutableStateOf<Int?>(null)
    }
    val authorText = stringResource(id = R.string.author)
    val categoryText = stringResource(id = R.string.category)
    val quoteText = stringResource(id = R.string.quote)

    //Dropdown list for all screens
    val generalDropdownList = listOf(AppDropdownMenuItem(
        R.string.theme,
        Icons.AutoMirrored.Rounded.KeyboardArrowRight,
        ImageVector.vectorResource(R.drawable.theme)
    ) {
        showThemesDropdown = !showThemesDropdown
    }, AppDropdownMenuItem(
        R.string.sort,
        Icons.AutoMirrored.Rounded.KeyboardArrowRight,
        ImageVector.vectorResource(R.drawable.sort)
    ) {
        showSortDropdown = !showSortDropdown
    }, AppDropdownMenuItem(R.string.search, trailingIcon = Icons.Rounded.Search) {
        onSearch(sortOrderType)
    })
    //Dropdown list specifically for For You screen
    val forYouDropdownList = generalDropdownList.toMutableList().apply {
        removeAt(1)
    }

    /** Dropdown list for changing quote [Theme] */
    val themeDropdownList = listOf(
        AppDropdownMenuItem(R.string.image,
            trailingIcon = if (setting.theme == Theme.Image) Icons.Rounded.Check else null,
            onClick = { updateSetting(setting.copy(theme = Theme.Image)) }),
        AppDropdownMenuItem(R.string.gradient,
            trailingIcon = if (setting.theme == Theme.Gradient) Icons.Rounded.Check else null,
            onClick = { updateSetting(setting.copy(theme = Theme.Gradient)) }),
        AppDropdownMenuItem(R.string.solid_color,
            trailingIcon = if (setting.theme == Theme.SolidColor) Icons.Rounded.Check else null,
            onClick = { updateSetting(setting.copy(theme = Theme.SolidColor)) }),
        AppDropdownMenuItem(R.string.card,
            trailingIcon = if (setting.theme == Theme.Card) Icons.Rounded.Check else null,
            onClick = { updateSetting(setting.copy(theme = Theme.Card)) })
    )
    //Adaptable dropdown list
    val dropdownList = when {
        onNavigateToForYouSettings != null -> forYouDropdownList
        else -> generalDropdownList
    }

    /** Function that converts the sort dropdown list to [AppDropdownMenuItem] */
    fun sortDropdownListConverter(pair: Pair<String, Int>) =
        AppDropdownMenuItem(text = pair.second, trailingComposable = {
            if (sortOrder != null) {
                RadioButton(selected = sortOrder.sortBy == pair.first,
                    onClick = { changeSortOrder(sortOrder.copy(sortBy = pair.first)) })
            }
        }) {
            if (sortOrder != null) {
                changeSortOrder(sortOrder.copy(sortBy = pair.first))
            }
        }

    val descendingOrder = SortOrders.order[0]
    val ascendingOrder = SortOrders.order[1]
    val descendingOrderDropdownItem =
        AppDropdownMenuItem(text = descendingOrder.second, trailingComposable = {
            if (sortOrder != null) {
                Checkbox(checked = sortOrder.order == descendingOrder.first,
                    onCheckedChange = { changeSortOrder(sortOrder.copy(order = if (it) descendingOrder.first else ascendingOrder.first)) })
            }
        }, onClick = { })
    val subType = sortOrder?.subType
    fun sortDropdownList(subType: SubType) = when (sortOrderType) {
        SortOrderType.Quote -> SortOrders.getQuotesSortOrder(subType).map {
            sortDropdownListConverter(it)
        }.plusElement(descendingOrderDropdownItem)

        SortOrderType.Category -> SortOrders.getCategoriesSortOrder(subType).map {
            sortDropdownListConverter(it)
        }.plusElement(descendingOrderDropdownItem)

        SortOrderType.Author -> SortOrders.getAuthorsSortOrder(subType).map {
            sortDropdownListConverter(it)
        }.plusElement(descendingOrderDropdownItem)
    }

    val swipeDirection = setting.swipeDirection
    QuotaRenewScaffold(nestedScrollConnection = scrollBehavior.nestedScrollConnection,
        snackbarHostState = snackbarHostState,
        showPurchaseItemDialog = showPurchaseItemDialog,
        onDismissPurchaseItemDialog = { showPurchaseItemDialog = false },
        showQuotaDialog = showQuotaDialog,
        numberOfQuotes = noOfQuotes,
        onConfirmPurchase = {
            noOfQuotes?.let { i ->
                val price = categoryQuota(i)
                performPurchaseOperation(price) {
                    category?.let { categoryUtils?.updateCategory(it) }
                    quotaUtils.deductQuota(
                        quota, price
                    )
                }
            }
            category = null
            noOfQuotes = null
        },
        topBar = {
            AppTopAppBar(title = "${
                name.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.getDefault()
                    ) else it.toString()
                }
            } ${
                if (onNavigateToForYouSettings != null || numberOfQuotes != null) "" else stringResource(
                    id = when (sortOrderType) {
                        SortOrderType.Quote -> R.string.quotes
                        SortOrderType.Author -> R.string.authors
                        SortOrderType.Category -> R.string.categories
                    }
                )
            }",
                numberOfQuotes = numberOfQuotes,
                scrollBehavior = scrollBehavior,
                onNavigateBack = navigateBack,
                actions = {
                    if (onAdd != null) IconButton(onClick = onAdd) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = stringResource(id = R.string.create)
                        )
                    }
                    if (onNavigateToForYouSettings != null) IconButton(onClick = onNavigateToForYouSettings) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.following),
                            contentDescription = stringResource(id = R.string.following)
                        )
                    }
                    if (sortOrderType == SortOrderType.Quote) IconButton(onClick = {
                        updateSetting(
                            setting.copy(
                                swipeDirection = if (swipeDirection == SwipeDirection.Horizontal) SwipeDirection.Vertical else SwipeDirection.Horizontal
                            )
                        )
                    }) {
                        Icon(
                            imageVector = ImageVector.vectorResource(if (swipeDirection == SwipeDirection.Horizontal) R.drawable.swipe_vertical else R.drawable.swipe_horizontal),
                            contentDescription = stringResource(id = if (swipeDirection == SwipeDirection.Horizontal) R.string.switch_to_vertical_swipe else R.string.switch_to_horizontal_swipe)
                        )
                    }
                    if (sortOrderType != SortOrderType.Quote) {
                        IconButton(onClick = { onSearch(sortOrderType) }) {
                            Icon(
                                imageVector = Icons.Rounded.Search,
                                contentDescription = stringResource(
                                    id = R.string.search
                                )
                            )
                        }
                        IconButton(onClick = { showSortDropdown = !showSortDropdown }) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.sort),
                                contentDescription = stringResource(
                                    id = R.string.sort
                                )
                            )
                        }
                    }
                    Box {
                        if (sortOrderType == SortOrderType.Quote) IconButton(onClick = {
                            showGeneralDropdown = !showGeneralDropdown
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.MoreVert,
                                contentDescription = stringResource(
                                    id = R.string.more_options
                                )
                            )
                        }
                        AppDropdownMenu(dropdownList, showGeneralDropdown) {
                            showGeneralDropdown = false
                        }
                        AppDropdownMenu(themeDropdownList, showThemesDropdown) {
                            showThemesDropdown = false
                        }
                        if (subType != null) AppDropdownMenu(
                            sortDropdownList(subType),
                            showSortDropdown
                        ) {
                            showSortDropdown = false
                        }
                    }

                })
        },
        adLoadingState = quotaUtils.adLoadingState,
        context = context,
        watchAd = { quotaUtils.watchAd(context) },
        onDismissQuotaDialog = { showQuotaDialog = false }) {
        if (quoteUtils != null && writeAccessState != null) {
            ReusableQuoteScreen(
                quotes = quotes,
                forYouQuotes = forYouQuotes,
                getQuote = { quoteUtils.getQuoteById(it) },
                swipeDirection = swipeDirection,
                colorUtils = colorUtils,
                quoteImageUtils = quoteImageUtils,
                gradientUtils = gradientUtils,
                fontUtils = fontUtils,
                refreshingForYouQuotes = refreshingForYouQuotes,
                setting = setting,
                likeQuote = { quoteUtils.updateQuote(it) },
                shareQuote = { quoteUtils.shareQuote(context, it) },
                downloadQuote = {
                    performPurchaseOperation(DownloadQuoteQuota) {
                        quoteUtils.saveBitmapFromComposable(
                            context,
                            snackbarHostState,
                            writeAccessState,
                            it
                        ) { quotaUtils.deductQuota(quota, DownloadQuoteQuota).start() }
                    }
                },
                deleteQuote = {
                    quote = it.quote
                    text = quoteText
                    showDeleteAlertDialog = true
                },
                updateSetting = updateSetting,
                refreshForYouQuotes = refreshForYouQuotes,
                onFollow = onNavigateToForYouSettings
            )
        }
        if (categories != null && categoryUtils != null) {
            CategoryScreen(categories = categories,
                onLikeOrFollow = { categoryUtils.updateCategory(it) },
                onClick = onClickCategory!!,
                unlock = { c, i ->
                    category = c
                    noOfQuotes = i
                    showPurchaseItemDialog = true
                },
                getCount = { categoryUtils.getQuoteCount(it) },
                onDelete = {
                    category = it
                    text = categoryText
                    showDeleteAlertDialog = true
                })
        }
        if (authors != null && authorUtils != null) {
            AuthorScreen(
                authors = authors,
                onLikeOrFollow = { authorUtils.updateAuthor(it) },
                onClick = onClickAuthor!!
            ) {
                author = it
                text = authorText
                showDeleteAlertDialog = true
            }
        }
        if (showDeleteAlertDialog) {
            DeleteAlertDialog(text = text, onDismissRequest = {
                showDeleteAlertDialog = false
                category = null
                author = null
                quote = null
            }) {
                when (text) {
                    authorText -> {
                        author?.let { authorUtils?.deleteAuthor(it) }
                    }

                    quoteText -> {
                        quote?.let { quoteUtils?.deleteQuote(it) }
                    }

                    else -> {
                        if (categoryUtils != null) {
                            category?.let { categoryUtils.deleteCategory(it) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeleteAlertDialog(text: String, onDismissRequest: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(onDismissRequest = onDismissRequest, confirmButton = {
        TextButton(
            onClick = {
                onConfirm()
                onDismissRequest()
            }, colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent, contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text(text = stringResource(R.string.delete))
        }
    }, dismissButton = {
        TextButton(onClick = onDismissRequest) {
            Text(text = stringResource(R.string.cancel))
        }
    }, title = {
        Text(text = stringResource(R.string.delete_text, text.lowercase()))
    }, text = {
        Text(text = stringResource(R.string.are_you_sure_you_want_to_delete_this, text.lowercase()))
    })
}