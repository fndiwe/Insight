package com.franklinndiwe.insight.ui.screens.theme

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.franklinndiwe.insight.AppRoutes
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.AdaptiveNavBarScreenData
import com.franklinndiwe.insight.data.AppFont
import com.franklinndiwe.insight.data.Gradient
import com.franklinndiwe.insight.data.GradientV2
import com.franklinndiwe.insight.data.MenuItem
import com.franklinndiwe.insight.data.QuoteImage
import com.franklinndiwe.insight.data.QuoteImageCategory
import com.franklinndiwe.insight.data.QuoteImageV2
import com.franklinndiwe.insight.data.SolidColor
import com.franklinndiwe.insight.data.SolidColorV2
import com.franklinndiwe.insight.ui.components.AppLoadingIndicator
import com.franklinndiwe.insight.ui.components.AppTopAppBar
import com.franklinndiwe.insight.ui.components.FontsPreviewCard
import com.franklinndiwe.insight.ui.components.GradientThemePreviewCard
import com.franklinndiwe.insight.ui.components.ImageThemePreviewCard
import com.franklinndiwe.insight.ui.components.QuotaRenewScaffold
import com.franklinndiwe.insight.ui.components.SlidingTabs
import com.franklinndiwe.insight.ui.components.SolidColorThemePreviewCard
import com.franklinndiwe.insight.ui.screens.DeleteAlertDialog
import com.franklinndiwe.insight.ui.states.ThemeScreenUIState
import com.franklinndiwe.insight.utils.AppContract
import com.franklinndiwe.insight.utils.AppUtils.CreateQuota
import com.franklinndiwe.insight.utils.AppUtils.ImportQuota
import com.franklinndiwe.insight.utils.AppUtils.cardSize
import com.franklinndiwe.insight.utils.Theme
import com.franklinndiwe.insight.utils.unit
import com.franklinndiwe.insight.viewmodel.AppViewModelProvider
import com.franklinndiwe.insight.viewmodel.ThemeScreenViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemesScreen(
    context: Context,
    navigateBack: () -> Unit,
    editTheme: (Int, Theme) -> Unit,
    createTheme: (Theme) -> Unit,
    themeScreenViewModel: ThemeScreenViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val uiState by themeScreenViewModel.themeScreenUIState.collectAsStateWithLifecycle()
    val quotaUtils = themeScreenViewModel.quotaUtils
    val gradientUtils = themeScreenViewModel.gradientUtils
    val colorUtils = themeScreenViewModel.colorUtils
    val fontUtils = themeScreenViewModel.fontUtils
    val quota by quotaUtils.quota.collectAsStateWithLifecycle(initialValue = 0)
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    var showDeleteDialog by remember {
        mutableStateOf(false)
    }
    val quoteImage = uiState.quoteImage
    val gradient = uiState.gradient
    val color = uiState.color
    val font = uiState.font
    var showQuotaDialog by rememberSaveable {
        mutableStateOf(false)
    }
    var showPurchaseItemDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val imageCategory = uiState.imageCategory

    fun performPurchaseOperation(quotaAmount: Int, operation: unit) {
        if (quotaUtils.shouldPerformOperation(
                quota, quotaAmount
            )
        ) {
            operation()
            quotaUtils.deductQuota(quota, quotaAmount)
        } else showQuotaDialog = true
    }

    fun updateUIState(uiState: ThemeScreenUIState) = themeScreenViewModel.updateUIState(uiState)

    val quoteImageUtils = themeScreenViewModel.quoteImageUtils
    val photoPicker =
        rememberLauncherForActivityResult(contract = AppContract(), onResult = { uris ->
            if (uris.isNotEmpty()) imageCategory?.let {
                performPurchaseOperation(ImportQuota) {
                    themeScreenViewModel.processImage(
                        context, it, uris[0], snackbarHostState
                    )
                }
            }
        })
    val fontPicker =
        rememberLauncherForActivityResult(contract = AppContract(), onResult = { uris ->
            if (uris.isNotEmpty()) performPurchaseOperation(ImportQuota) {
                themeScreenViewModel.processFont(
                    context, uris[0], snackbarHostState
                )
            }
        })
    val tabsData = listOf(AdaptiveNavBarScreenData(
        MenuItem(
            0, name = R.string.images, route = AppRoutes.ImageThemes.name
        )
    ) {
        ImageThemeScreen(images = themeScreenViewModel.images.collectAsStateWithLifecycle(
            initialValue = emptyList()
        ).value, onAddImage = {
            photoPicker.launch("image/*")
            updateUIState(uiState.copy(imageCategory = it))

        }, unlockImage = {
            updateUIState(uiState.copy(quoteImage = it))
            showPurchaseItemDialog = true
        }, deleteImage = {
            updateUIState(uiState.copy(quoteImage = it))
            showDeleteDialog = true
        }, editImage = {
            editTheme(it.id, Theme.Image)
        })
    }, AdaptiveNavBarScreenData(
        MenuItem(
            1, name = R.string.gradients, route = AppRoutes.GradientThemes.name
        )
    ) {
        GradientThemeScreen(gradients = gradientUtils.gradients, unlockGradient = {
            updateUIState(uiState.copy(gradient = it))
            showPurchaseItemDialog = true
        }, deleteGradient = {
            updateUIState(uiState.copy(gradient = it))
            showDeleteDialog = true
        }, editGradient = {
            editTheme(it.id, Theme.Gradient)
        }, createGradient = {
            createTheme(Theme.Gradient)
        })
    }, AdaptiveNavBarScreenData(
        MenuItem(
            2, name = R.string.colors, route = AppRoutes.SolidColorThemes.name
        )
    ) {
        SolidColorThemeScreen(colors = colorUtils.colors, unlockColor = {
            updateUIState(uiState.copy(color = it))
            showPurchaseItemDialog = true
        }, deleteColor = {
            updateUIState(uiState.copy(color = it))
            showDeleteDialog = true
        }, editColor = {
            editTheme(it.id, Theme.SolidColor)
        }, createColor = {
            createTheme(Theme.SolidColor)
        })
    }, AdaptiveNavBarScreenData(
        MenuItem(
            3, name = R.string.fonts, route = AppRoutes.Fonts.name
        )
    ) {
        FontsListScreen(fonts = fontUtils.fonts, unlockFont = {
            updateUIState(uiState.copy(font = it))
            showPurchaseItemDialog = true
        }, deleteFont = {
            updateUIState(uiState.copy(font = it))
            showDeleteDialog = true
        }, editFont = {
            editTheme(it.id, Theme.Card)
        }, createFont = {
            fontPicker.launch("font/ttf")

        })
    })
    QuotaRenewScaffold(
        context = context,
        adLoadingState = quotaUtils.adLoadingState,
        snackbarHostState = snackbarHostState,
        nestedScrollConnection = scrollBehavior.nestedScrollConnection,
        watchAd = { quotaUtils.watchAd(context) },
        showQuotaDialog = showQuotaDialog,
        onDismissQuotaDialog = { showQuotaDialog = false },
        showPurchaseItemDialog = showPurchaseItemDialog,
        onDismissPurchaseItemDialog = { showPurchaseItemDialog = false },
        onConfirmPurchase = {
            when {
                quoteImage != null -> performPurchaseOperation(ImportQuota) {
                    quoteImageUtils.updateQuoteImage(
                        quoteImage.copy(
                            attributes = quoteImage.attributes.copy(
                                unlocked = true
                            )
                        )
                    )
                    updateUIState(uiState.copy(quoteImage = null))
                }

                gradient != null -> performPurchaseOperation(CreateQuota) {
                    gradientUtils.updateGradient(
                        gradient.copy(
                            attributes = gradient.attributes.copy(unlocked = true)
                        )
                    )
                    updateUIState(uiState.copy(gradient = null))
                }

                color != null -> performPurchaseOperation(CreateQuota) {
                    colorUtils.updateColor(color.copy(attributes = color.attributes.copy(unlocked = true)))
                    updateUIState(uiState.copy(color = null))
                }

                font != null -> performPurchaseOperation(ImportQuota) {
                    fontUtils.updateFont(font.copy(unlocked = true))
                }
            }

        },
        topBar = {
            AppTopAppBar(
                title = stringResource(R.string.themes),
                onNavigateBack = navigateBack,
                scrollBehavior = scrollBehavior
            )
        },
        isFont = font != null
    ) {
        SlidingTabs(screens = tabsData, onNavigateBack = navigateBack)
        if (showDeleteDialog) DeleteAlertDialog(text = when {
            font != null -> stringResource(id = R.string.font)
            else -> stringResource(id = R.string.theme)
        }, onDismissRequest = {
            showDeleteDialog = false
            updateUIState(
                uiState.copy(
                    quoteImage = null, gradient = null, color = null, font = null
                )
            )
        }, onConfirm = {
            when {
                quoteImage != null -> quoteImageUtils.deleteQuoteImage(quoteImage)
                gradient != null -> gradientUtils.deleteGradient(gradient)
                color != null -> colorUtils.deleteColor(color)
                font != null -> fontUtils.deleteFont(font)
            }
        })
    }
}

@Composable
fun ImageThemeScreen(
    images: List<Pair<QuoteImageCategory, Flow<PagingData<QuoteImageV2>>>>,
    onAddImage: (QuoteImageCategory) -> Unit,
    unlockImage: (QuoteImage) -> Unit,
    deleteImage: (QuoteImage) -> Unit,
    editImage: (QuoteImage) -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(images) { flowPair ->
            val category = flowPair.first
            val imageV2LazyPagingItems = remember {
                flowPair.second
            }.collectAsLazyPagingItems()
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = category.name, style = MaterialTheme.typography.titleMedium
                    )
                    IconButton(onClick = { onAddImage(category) }) {
                        Icon(
                            imageVector = Icons.Rounded.Add, contentDescription = stringResource(
                                id = R.string.add
                            )
                        )
                    }
                }
                if (imageV2LazyPagingItems.itemCount < 1 && imageV2LazyPagingItems.loadState.refresh is LoadState.Loading) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .size(100.dp), contentAlignment = Alignment.Center
                    ) {
                        AppLoadingIndicator()
                    }
                } else LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(imageV2LazyPagingItems.itemCount,
                        imageV2LazyPagingItems.itemKey { it.quoteImage.id }) { i ->
                        val image = imageV2LazyPagingItems[i]
                        if (image != null) {
                            ImageThemePreviewCard(image = image,
                                onClickLockIcon = { unlockImage(it.quoteImage) },
                                onDelete = { deleteImage(it.quoteImage) }) { editImage(it.quoteImage) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GradientThemeScreen(
    gradients: StateFlow<PagingData<GradientV2>>,
    unlockGradient: (Gradient) -> Unit,
    deleteGradient: (Gradient) -> Unit,
    editGradient: (Gradient) -> Unit,
    createGradient: () -> Unit,
) {
    val gradientPagingItems = gradients.collectAsLazyPagingItems()
    if (gradientPagingItems.itemCount < 1 && gradientPagingItems.loadState.refresh is LoadState.Loading) {
        Box(
            Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            AppLoadingIndicator()
        }
    } else ReusablePreviewGrid(onCreate = createGradient) {
        items(gradientPagingItems.itemCount, gradientPagingItems.itemKey { it.gradient.id }) { i ->
            val gradientV2 = gradientPagingItems[i]
            if (gradientV2 != null) {
                GradientThemePreviewCard(gradientV2 = gradientV2,
                    onClickLockIcon = { unlockGradient(it.gradient) },
                    onDelete = { deleteGradient(it.gradient) }) { editGradient(it.gradient) }
            }
        }
    }
}

@Composable
fun SolidColorThemeScreen(
    colors: StateFlow<PagingData<SolidColorV2>>,
    unlockColor: (SolidColor) -> Unit,
    deleteColor: (SolidColor) -> Unit,
    editColor: (SolidColor) -> Unit,
    createColor: () -> Unit,
) {
    val colorPagingItems = colors.collectAsLazyPagingItems()
    if (colorPagingItems.itemCount < 1 && colorPagingItems.loadState.refresh is LoadState.Loading) {
        Box(
            Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            AppLoadingIndicator()
        }
    } else ReusablePreviewGrid(onCreate = createColor) {
        items(colorPagingItems.itemCount, colorPagingItems.itemKey { it.color.id }) { i ->
            val color = colorPagingItems[i]
            if (color != null) {
                SolidColorThemePreviewCard(color,
                    onClickLockIcon = { unlockColor(it.color) },
                    onDelete = { deleteColor(it.color) }) { editColor(it.color) }
            }
        }
    }
}

@Composable
fun FontsListScreen(
    fonts: StateFlow<PagingData<AppFont>>,
    unlockFont: (AppFont) -> Unit,
    deleteFont: (AppFont) -> Unit,
    editFont: (AppFont) -> Unit,
    createFont: () -> Unit,
) {
    val fontPagingItems = fonts.collectAsLazyPagingItems()
    if (fontPagingItems.itemCount < 1 && fontPagingItems.loadState.refresh is LoadState.Loading) {
        Box(
            Modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
            AppLoadingIndicator()
        }
    } else ReusablePreviewGrid(onCreate = createFont, isFontPreview = true) {
        items(fontPagingItems.itemCount, fontPagingItems.itemKey { it.id }) {
            val font = fontPagingItems[it]
            if (font != null) {
                FontsPreviewCard(
                    font = font, onClickLockIcon = unlockFont, onDelete = deleteFont, editFont
                )
            }
        }
    }
}

@Composable
private fun ReusablePreviewGrid(
    modifier: Modifier = Modifier,
    isFontPreview: Boolean = false,
    onCreate: () -> Unit,
    content: LazyGridScope.() -> Unit,
) {
    LazyVerticalGrid(
        GridCells.Adaptive(cardSize.width),
        modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            ElevatedButton(
                onClick = onCreate,
                Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant),
                contentPadding = PaddingValues(8.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 2.dp, pressedElevation = 3.dp
                )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(if (isFontPreview) R.drawable.save else R.drawable.sparkle),
                    contentDescription = null,
                    Modifier.padding(end = 8.dp)
                )
                Text(text = stringResource(id = if (isFontPreview) R.string.import_font else R.string.create))
            }
        }
        content()
    }
}