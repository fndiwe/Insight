package com.franklinndiwe.insight.ui.screens.theme

import android.content.Context
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.AppFont
import com.franklinndiwe.insight.data.Gradient
import com.franklinndiwe.insight.data.QuoteImage
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.data.SolidColor
import com.franklinndiwe.insight.ui.components.AppTopAppBar
import com.franklinndiwe.insight.ui.components.ColorPicker
import com.franklinndiwe.insight.ui.components.QuotaRenewScaffold
import com.franklinndiwe.insight.ui.components.ReusableQuoteScreen
import com.franklinndiwe.insight.ui.components.toFontFamily
import com.franklinndiwe.insight.ui.screens.SettingRightText
import com.franklinndiwe.insight.ui.screens.SettingRow
import com.franklinndiwe.insight.utils.AppUtils.CreateQuota
import com.franklinndiwe.insight.utils.AppUtils.LargeQuoteSize
import com.franklinndiwe.insight.utils.AppUtils.defaultShape
import com.franklinndiwe.insight.utils.BitmapUtils
import com.franklinndiwe.insight.utils.SwipeDirection
import com.franklinndiwe.insight.utils.Theme
import com.franklinndiwe.insight.utils.unit
import com.franklinndiwe.insight.viewmodel.AppViewModelProvider
import com.franklinndiwe.insight.viewmodel.ThemeEditorViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeEditor(
    context: Context,
    themeId: Int,
    theme: Theme,
    setting: Setting,
    navigateBack: unit,
    themeEditorViewModel: ThemeEditorViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    var id by rememberSaveable {
        mutableIntStateOf(themeId)
    }
    val scope = rememberCoroutineScope()
    val gradientUtils = themeEditorViewModel.gradientUtils
    val colorUtils = themeEditorViewModel.colorUtils
    val quoteImageUtils = themeEditorViewModel.imageUtils
    val settingUtils = themeEditorViewModel.settingUtils
    val fontUtils = themeEditorViewModel.fontUtils
    val quotaUtils = themeEditorViewModel.quotaUtils
    val quota by quotaUtils.quota.collectAsStateWithLifecycle(initialValue = 0)
    val fonts = if (theme == Theme.Card) null else fontUtils.unlockedFonts
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(canScroll = { false })
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    var showQuotaDialog by rememberSaveable {
        mutableStateOf(false)
    }

    fun performPurchaseOperation(quotaAmount: Int, operation: unit) {
        if (quotaUtils.shouldPerformOperation(
                quota, quotaAmount
            )
        ) {
            operation()
            quotaUtils.deductQuota(quota, quotaAmount)
        } else showQuotaDialog = true
    }

    QuotaRenewScaffold(context = context,
        adLoadingState = quotaUtils.adLoadingState,
        snackbarHostState = snackbarHostState,
        nestedScrollConnection = scrollBehavior.nestedScrollConnection,
        watchAd = { quotaUtils.watchAd(context) },
        showQuotaDialog = showQuotaDialog,
        onDismissQuotaDialog = { showQuotaDialog = false },
        topBar = {
            AppTopAppBar(
                title = stringResource(if (fonts != null) R.string.theme_editor else R.string.font_editor),
                scrollBehavior = scrollBehavior,
                onNavigateBack = navigateBack
            )
        }) {
        Crossfade(targetState = id, label = "") { i ->
            if (i == Int.MIN_VALUE) {
                if (theme == Theme.SolidColor) {
                    ColorCreatorScreen(editColor = {
                        performPurchaseOperation(CreateQuota) {
                            scope.launch {
                                id = colorUtils.insertColor(it).await().toInt()
                            }
                        }
                    })
                } else if (theme == Theme.Gradient) {
                    GradientCreatorScreen(listOfColors = themeEditorViewModel.colors,
                        editGradient = {
                            performPurchaseOperation(CreateQuota) {
                                scope.launch {
                                    id = gradientUtils.insertGradient(it).await().toInt()
                                }
                            }
                        })
                }
            } else {
                ReusableQuoteScreen(isEdit = true,
                    forYouQuotes = listOf(0),
                    swipeDirection = SwipeDirection.Horizontal,
                    colorUtils = colorUtils,
                    quoteImageUtils = quoteImageUtils,
                    gradientUtils = gradientUtils,
                    fontUtils = fontUtils,
                    userScrollEnabled = false,
                    themeId = i,
                    setting = setting.copy(theme = theme),
                    likeQuote = {},
                    shareQuote = {},
                    downloadQuote = {},
                    deleteQuote = null,
                    updateSetting = { settingUtils.updateSetting(it.copy(theme = setting.theme)) })
            }
        }
    }
}

@Composable
fun EditorCardWrapper(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(16.dp), content = content
    )
}

@Composable
internal fun FontEditorCard(
    quoteLength: Int,
    setting: Setting,
    updateSetting: (Setting) -> Unit,
    fonts: Flow<PagingData<AppFont>>?,
    font: AppFont?,
    updateFont: (AppFont) -> Unit,
    setFont: ((AppFont?) -> Unit)?,
) {
    var expandFonts by rememberSaveable { mutableStateOf(false) }
    val fontSize = when {
        quoteLength >= LargeQuoteSize -> when {
            font != null -> font.textSizeForLongQuote
            else -> setting.defaultLongQuoteTextSize
        }

        else -> when {
            font != null -> font.textSizeForShortQuote
            else -> setting.defaultShortQuoteTextSize
        }
    }

    Box {
        EditorCardWrapper {
            // If it's not Card Theme
            if (fonts != null) SettingRow {
                LeftText(text = stringResource(id = R.string.font))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SettingRightText(
                        text = font?.name ?: stringResource(id = R.string.default_font),
                        fontFamily = font?.toFontFamily()
                    )
                    IconButton(onClick = { expandFonts = !expandFonts }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = stringResource(id = R.string.select_font)
                        )
                    }
                }
            }
            EditorCardRow {
                LeftText(text = stringResource(R.string.font_size))
                EditorCardRow {
                    Slider(
                        value = fontSize.toFloat(),
                        onValueChange = {
                            val size = it.roundToInt()
                            when {
                                quoteLength >= LargeQuoteSize -> when {
                                    font != null -> updateFont(font.copy(textSizeForLongQuote = size))
                                    else -> updateSetting(setting.copy(defaultLongQuoteTextSize = size))
                                }

                                else -> when {
                                    font != null -> updateFont(font.copy(textSizeForShortQuote = size))
                                    else -> updateSetting(setting.copy(defaultShortQuoteTextSize = size))
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        valueRange = 12f..32f,
                        steps = (32 - 12) / 2
                    )
                    Text(
                        text = fontSize.toString(), style = MaterialTheme.typography.labelLarge
                    )
                }
            }
            if (expandFonts && fonts != null) setFont?.let {
                FontsList(
                    fonts = fonts, font = font, setFont = it
                ) { expandFonts = false }
            }
        }
    }
}

@Composable
fun FontsList(
    fonts: Flow<PagingData<AppFont>>,
    font: AppFont?,
    setFont: (AppFont?) -> Unit,
    onDismissRequest: unit,
) {
    val fontPagingItems = fonts.collectAsLazyPagingItems()
    AlertDialog(onDismissRequest = onDismissRequest, confirmButton = {
        TextButton(onClick = onDismissRequest) {
            Text(text = stringResource(id = R.string.okay))
        }
    }, title = {
        Text(text = stringResource(id = R.string.select_font))
    }, text = {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clip(defaultShape)
                        .clickable { setFont(null) },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    RadioButton(selected = font == null, onClick = { setFont(null) })
                    Text(text = stringResource(id = R.string.default_font))
                }
            }
            items(fontPagingItems.itemCount, fontPagingItems.itemKey { it.id }) {
                val item = fontPagingItems[it]
                if (item != null) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(defaultShape)
                            .clickable { setFont(item) },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        RadioButton(selected = font == item, onClick = { setFont(item) })
                        Text(
                            text = item.name,
                            fontFamily = item.toFontFamily(),
//                            fontWeight = FontWeight(item.weight)
                        )
                    }
                }
            }
        }
    })
}

@Composable
private fun EditorCardRow(content: @Composable RowScope.() -> Unit) = Row(
    Modifier.wrapContentWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    content = content
)

private enum class SolidColorType {
    Background, Text
}

@Composable
internal fun ColorEditorCard(
    color: SolidColor,
    saveColor: (SolidColor) -> Unit,
) {
    var backgroundColor by rememberSaveable {
        mutableIntStateOf(color.background)
    }
    var textColor by rememberSaveable {
        mutableIntStateOf(color.attributes.textColor!!)
    }
    var showColorPicker by rememberSaveable {
        mutableStateOf(false)
    }
    var solidColorType by rememberSaveable {
        mutableIntStateOf(SolidColorType.Background.ordinal)
    }

    fun color1() = color.copy(
        background = backgroundColor, attributes = color.attributes.copy(textColor = textColor)
    )
    Box {
        EditorCardWrapper {
            ColorRow(listOfColorButtons = listOf(Triple(
                R.string.background, Color(backgroundColor)
            ) {
                solidColorType = SolidColorType.Background.ordinal
                showColorPicker = true
            }, Triple(R.string.text, Color(textColor)) {
                solidColorType = SolidColorType.Text.ordinal
                showColorPicker = true
            })
            )
        }
        if (showColorPicker) {
            val currentSolidColorType = SolidColorType.entries[solidColorType]
            ColorPicker(initialColor = Color(
                when (currentSolidColorType) {
                    SolidColorType.Background -> backgroundColor
                    SolidColorType.Text -> textColor
                }
            ), setColor = {
                when (currentSolidColorType) {
                    SolidColorType.Background -> backgroundColor = it.toArgb()
                    SolidColorType.Text -> textColor = it.toArgb()
                }
            }) {
                saveColor(color1())
                showColorPicker = false
            }
        }
    }
}

@Composable
private fun DividerForEditorCard() {
    VerticalDivider(Modifier.height(40.dp), thickness = 3.dp)
}

@Composable
private fun ColorButton(color: Color, onClick: (Color) -> Unit = {}) =
    Box(modifier = Modifier
        .size(35.dp)
        .background(color, defaultShape)
        .clip(defaultShape)
        .clickable { onClick(color) })

private enum class GradientColorType {
    List, Text, Tint
}

@Composable
internal fun GradientEditorCard(
    gradient: Gradient,
    saveGradient: (Gradient) -> Unit,
) {
    var showColorPicker by rememberSaveable {
        mutableStateOf(false)
    }
    var currentColorIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    var gradientColorType by rememberSaveable {
        mutableIntStateOf(GradientColorType.List.ordinal)
    }
    val colors = remember {
        gradient.colors.toMutableStateList()
    }
    var textColor by rememberSaveable {
        mutableIntStateOf(gradient.attributes.textColor!!)
    }
    var tintColor by rememberSaveable {
        mutableIntStateOf(gradient.attributes.tintColor!!)
    }

    fun gradient1() = gradient.copy(
        colors = colors,
        attributes = gradient.attributes.copy(textColor = textColor, tintColor = tintColor)
    )

    fun addColor(color: Color) = colors.add(color.toArgb())
    fun updateColor(index: Int, color: Color) {
        colors[index] = color.toArgb()
    }

    Box {
        EditorCardWrapper {
            EditorCardRow {
                LeftText(text = stringResource(id = R.string.colors))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    colors.forEachIndexed { index, color ->
                        ColorButton(color = Color(color)) {
                            currentColorIndex = index
                            showColorPicker = true
                        }
                    }
                    if (colors.size < 4) FilledTonalIconButton(onClick = {
                        addColor(Color.White)
                        currentColorIndex = colors.lastIndex
                        showColorPicker = true
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = stringResource(id = R.string.add)
                        )
                    }
                }
            }
            ColorRow(
                listOfColorButtons = listOf(Triple(R.string.text, Color(textColor)) {
                    gradientColorType = GradientColorType.Text.ordinal
                    showColorPicker = true
                }, Triple(R.string.tint, Color(tintColor)) {
                    gradientColorType = GradientColorType.Tint.ordinal
                    showColorPicker = true
                })
            )
        }
        if (showColorPicker) {
            val currentGradientColorType = GradientColorType.entries[gradientColorType]
            ColorPicker(initialColor = Color(
                when (currentGradientColorType) {
                    GradientColorType.List -> colors[currentColorIndex]
                    GradientColorType.Text -> textColor
                    GradientColorType.Tint -> tintColor
                }
            ), setColor = {
                when (currentGradientColorType) {
                    GradientColorType.List -> updateColor(currentColorIndex, it)
                    GradientColorType.Text -> textColor = it.toArgb()
                    GradientColorType.Tint -> tintColor = it.toArgb()
                }
            }) {
                saveGradient(gradient1())
                showColorPicker = false
            }
        }
    }
}

@Composable
private fun ColorRow(listOfColorButtons: List<Triple<Int, Color, (Color) -> Unit>>) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        listOfColorButtons.forEach {
            EditorCardRow {
                LeftText(text = stringResource(id = it.first))
                ColorButton(color = it.second, it.third)
            }
            if (listOfColorButtons.indexOf(it) < listOfColorButtons.lastIndex) DividerForEditorCard()
        }
    }
}

private enum class ImageColorType {
    Text, Tint
}

@Composable
internal fun ImageEditorCard(quoteImage: QuoteImage, saveQuoteImage: (QuoteImage) -> Unit) {
    val context = LocalContext.current
    val imagePath = quoteImage.path
    val scope = rememberCoroutineScope()
    val bitmap = remember { BitmapUtils.loadBitmap(context, imagePath, scope) }
    val backgroundColor = remember { BitmapUtils.getBackgroundColor(bitmap) }
    val bitmapTextColor = remember {
        BitmapUtils.getSupposedTextColor(bitmap)
    }
    var imageColorType by rememberSaveable {
        mutableIntStateOf(ImageColorType.Text.ordinal)
    }
    var showColorPicker by rememberSaveable {
        mutableStateOf(false)
    }
    var textColor by rememberSaveable {
        mutableIntStateOf(quoteImage.attributes.textColor ?: bitmapTextColor)
    }
    var tintColor by rememberSaveable {
        mutableIntStateOf(quoteImage.attributes.tintColor ?: backgroundColor.toArgb())
    }

    fun quoteImage1() = quoteImage.copy(
        attributes = quoteImage.attributes.copy(
            textColor = textColor, tintColor = tintColor
        )
    )
    Box {
        EditorCardWrapper {
            ColorRow(
                listOfColorButtons = listOf(Triple(R.string.text, Color(textColor)) {
                    imageColorType = ImageColorType.Text.ordinal
                    showColorPicker = true
                }, Triple(R.string.tint, Color(tintColor)) {
                    imageColorType = ImageColorType.Tint.ordinal
                    showColorPicker = true
                })
            )
        }
        if (showColorPicker) {
            val currentImageColorType = ImageColorType.entries[imageColorType]
            ColorPicker(
                initialColor = Color(
                    when (currentImageColorType) {
                        ImageColorType.Text -> textColor
                        ImageColorType.Tint -> tintColor
                    }
                ), setColor = {
                    when (currentImageColorType) {
                        ImageColorType.Text -> textColor = it.toArgb()
                        ImageColorType.Tint -> tintColor = it.toArgb()
                    }
                }, imageBitmap = bitmap?.asImageBitmap()
            ) {
                saveQuoteImage(quoteImage1())
                showColorPicker = false
            }
        }
    }
}

@Composable
internal fun BigColorButton(color: Color?, onClick: unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp)
            .border(1.dp, color ?: MaterialTheme.colorScheme.secondary, defaultShape)
            .background(color ?: MaterialTheme.colorScheme.surface, defaultShape)
            .clickable(onClick = onClick), contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.color_lens),
                contentDescription = null,
                Modifier.size(45.dp),
                tint = MaterialTheme.colorScheme.surfaceTint
            )
            Text(text = stringResource(R.string.click_to_add_color))
        }
    }
}

@Composable
private fun LeftText(text: String) = Text(text = text, style = MaterialTheme.typography.bodyMedium)