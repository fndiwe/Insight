package com.franklinndiwe.insight.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.Gradient
import com.franklinndiwe.insight.data.QuoteImage
import com.franklinndiwe.insight.data.QuoteV2
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.data.SolidColor
import com.franklinndiwe.insight.utils.AppFontUtils
import com.franklinndiwe.insight.utils.GradientUtils
import com.franklinndiwe.insight.utils.QuoteImageUtils
import com.franklinndiwe.insight.utils.SolidColorUtils
import com.franklinndiwe.insight.utils.SwipeDirection
import com.franklinndiwe.insight.utils.Theme
import com.franklinndiwe.insight.utils.unit
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReusableQuoteScreen(
    isEdit: Boolean = false,
    quotes: Flow<PagingData<QuoteV2>>? = null,
    forYouQuotes: List<Int> = emptyList(),
    getQuote: (Int) -> Flow<QuoteV2?> = { emptyFlow() },
    swipeDirection: SwipeDirection,
    colorUtils: SolidColorUtils?,
    quoteImageUtils: QuoteImageUtils?,
    gradientUtils: GradientUtils?,
    fontUtils: AppFontUtils?,
    userScrollEnabled: Boolean = true,
    refreshingForYouQuotes: Boolean = false,
    themeId: Int? = null,
    setting: Setting,
    likeQuote: (QuoteV2) -> Unit,
    shareQuote: (QuoteV2) -> Unit,
    downloadQuote: (GraphicsLayer) -> Unit,
    deleteQuote: ((QuoteV2) -> Unit)?,
    updateSetting: (Setting) -> Unit,
    refreshForYouQuotes: () -> Unit = {},
    onFollow: unit? = null,
) {
    val quotesPagingItems = quotes?.collectAsLazyPagingItems()
    val scope = rememberCoroutineScope()
    var autoScroll by rememberSaveable { mutableStateOf(false) }
    var showThemeEditor by rememberSaveable {
        mutableStateOf(themeId != null)
    }
    val oneSec = (1.00f / setting.delay)
    var progress by rememberSaveable {
        mutableFloatStateOf(0f)
    }
    var shouldContinueScroll by rememberSaveable {
        mutableStateOf(autoScroll)
    }
    val pagerState = rememberPagerState {
        quotesPagingItems?.itemCount ?: forYouQuotes.size
    }
    val currentPage by remember {
        derivedStateOf { pagerState.currentPage }
    }

    fun saveGradient(gradient: Gradient) = gradientUtils?.updateGradient(gradient)
    fun saveColor(color: SolidColor) = colorUtils?.updateColor(color)
    fun saveQuoteImage(image: QuoteImage) = quoteImageUtils?.updateQuoteImage(image)

    val dragged by pagerState.interactionSource.collectIsDraggedAsState()

    suspend fun changeQuote() {
        if (progress >= 1f && autoScroll && !dragged) {
            pagerState.animateScrollToPage(
                if (currentPage == pagerState.pageCount - 1) 0 else currentPage + 1,
                animationSpec = spring(
                    stiffness = 30f, visibilityThreshold = Spring.DefaultDisplacementThreshold
                )
            )
        }
    }

    fun autoScrollQuote() {
        scope.launch {
            changeQuote()
            while (autoScroll && progress < 1f && !dragged) {
                if (currentPage == pagerState.pageCount - 1) autoScroll = false else {
                    delay(1000)
                    progress += oneSec
                }
            }
        }
    }

    LaunchedEffect(key1 = currentPage) {
        progress = 0f
        autoScrollQuote()
    }
    LaunchedEffect(dragged) {
        if (dragged) autoScroll = false
    }
    LaunchedEffect(key1 = progress) {
        scope.launch {
            changeQuote()
        }
    }
    LaunchedEffect(key1 = showThemeEditor) {
        if (showThemeEditor) autoScroll = false else {
            if (!autoScroll && shouldContinueScroll) {
                autoScroll = true
                autoScrollQuote()
            }
        }
    }
    //If forYou Quotes has refreshed.
    LaunchedEffect(key1 = forYouQuotes) {
        if (forYouQuotes.isNotEmpty()) pagerState.scrollToPage(0)
    }
    LifecycleResumeEffect(key1 = Unit) {
        if (!showThemeEditor && shouldContinueScroll) {
            autoScroll = true
            autoScrollQuote()
        }

        onPauseOrDispose {
            autoScroll = false
            shouldContinueScroll = false
        }
    }
    DisposableEffect(key1 = Unit) {
        onDispose {
            progress = 0f
        }
    }
    PullToRefreshBox(
        isRefreshing = if (quotesPagingItems != null) quotesPagingItems.loadState.refresh is LoadState.Loading else refreshingForYouQuotes,
        onRefresh = { if (quotesPagingItems != null) quotesPagingItems.refresh() else refreshForYouQuotes() },
        Modifier.fillMaxSize()
    ) {
        var quoteLength by rememberSaveable {
            mutableIntStateOf(0)
        }
        var imageId by rememberSaveable {
            mutableStateOf(themeId)
        }
        val imageEdit =
            imageId?.let { quoteImageUtils?.getImageById(it) }?.collectAsStateWithLifecycle(
                initialValue = null
            )?.value
        var gradientId by rememberSaveable {
            mutableStateOf(themeId)
        }
        val gradientEdit = gradientId?.let {
            gradientUtils?.getGradientById(it)?.collectAsStateWithLifecycle(
                initialValue = null
            )?.value
        }
        var solidColorId by rememberSaveable {
            mutableStateOf(themeId)
        }
        val solidColorEdit = solidColorId?.let {
            colorUtils?.getColorById(it)?.collectAsStateWithLifecycle(
                initialValue = null
            )?.value
        }
        val font = if (setting.theme == Theme.Card) themeId?.let {
            fontUtils?.getFontById(it)?.collectAsStateWithLifecycle(
                initialValue = null
            )?.value
        } else null
        if (pagerState.pageCount >= 1) {
            val images =
                if (themeId == null && setting.theme == Theme.Image) quoteImageUtils?.displayImages?.collectAsStateWithLifecycle()?.value else null
            val colors =
                if (themeId == null && setting.theme == Theme.SolidColor) colorUtils?.displayColors?.collectAsStateWithLifecycle()?.value else null
            val gradients =
                if (themeId == null && setting.theme == Theme.Gradient) gradientUtils?.displayGradients?.collectAsStateWithLifecycle()?.value else null
            AdaptivePager(swipeDirection = swipeDirection,
                pagerState = pagerState,
                userScrollEnabled = userScrollEnabled,
                key = quotesPagingItems?.itemKey { it.quote.id } ?: { it }) { value, modifier ->
                val quote =
                    if (isEdit) getQuoteV2List()[0] else if (quotesPagingItems != null) quotesPagingItems[value] else getQuote(
                        forYouQuotes[value]
                    ).collectAsStateWithLifecycle(
                        initialValue = null
                    ).value
                if (quote != null) {
                    val random = Random
                    val imageRandomIndex by rememberSaveable(images?.size) {
                        mutableStateOf(
                            if (images.isNullOrEmpty()) null else random.nextInt(
                                0, images.size
                            )
                        )
                    }
                    val colorRandomIndex by rememberSaveable(colors?.size) {
                        mutableStateOf(
                            if (colors.isNullOrEmpty()) null else random.nextInt(
                                0, colors.size
                            )
                        )
                    }
                    val gradientRandomIndex by rememberSaveable(gradients?.size) {
                        mutableStateOf(
                            if (gradients.isNullOrEmpty()) null else random.nextInt(
                                0, gradients.size
                            )
                        )
                    }
                    val image = if (setting.theme == Theme.Image) (themeId?.let {
                        quoteImageUtils?.getImageById(it)
                    })?.collectAsStateWithLifecycle(initialValue = null)?.value
                        ?: imageRandomIndex?.let {
                            if (images?.isNotEmpty() == true) images[it] else null
                        } else null
                    val solidColor = if (setting.theme == Theme.SolidColor) (themeId?.let {
                        colorUtils?.getColorById(it)
                    })?.collectAsStateWithLifecycle(initialValue = null)?.value
                        ?: colorRandomIndex?.let {
                            if (colors?.isNotEmpty() == true) colors[it] else null
                        } else null
                    val gradient = if (setting.theme == Theme.Gradient) (themeId?.let {
                        gradientUtils?.getGradientById(it)
                    })?.collectAsStateWithLifecycle(null)?.value
                        ?: gradientRandomIndex?.let { if (gradients?.isNotEmpty() == true) gradients[it] else null } else null

                    fun launchEditor() {
                        quoteLength = quote.quote.text.length
                        when {
                            image != null -> {
                                imageId = image.quoteImage.id
                            }

                            gradient != null -> {
                                gradientId = gradient.gradient.id
                            }

                            solidColor != null -> {
                                solidColorId = solidColor.color.id
                            }
                        }
                        showThemeEditor = true
                    }
                    CustomizableQuoteView(
                        modifier,
                        quote,
                        setting = setting,
                        color = solidColor,
                        image = image,
                        gradient = gradient,
                        font = font,
                        autoScroll = autoScroll,
                        progress = if (userScrollEnabled) progress else .5f,
                        onToggleAutoScroll = {
                            if (userScrollEnabled) {
                                autoScroll = it
                                shouldContinueScroll = it
                                autoScrollQuote()
                            }
                        },
                        likeQuote = likeQuote,
                        deleteQuote = deleteQuote,
                        shareQuote = shareQuote,
                        downloadQuote = downloadQuote
                    ) {
                        launchEditor()
                    }
                }
            }
        }
        if (showThemeEditor) {
            ThemeEditorBottomSheet(gradientV2 = gradientEdit,
                colorV2 = solidColorEdit,
                imageV2 = imageEdit,
                quoteLength = quoteLength,
                setting = setting,
                updateSetting = updateSetting,
                fonts = if (setting.theme == Theme.Card) null else fontUtils?.unlockedFonts,
                font = when (setting.theme) {
                    Theme.Card -> font
                    Theme.SolidColor -> solidColorEdit?.font
                    Theme.Gradient -> gradientEdit?.font
                    Theme.Image -> imageEdit?.font
                },
                updateFont = { fontUtils?.updateFont(it) },
                setFont = {
                    when (setting.theme) {
                        Theme.Gradient -> gradientEdit?.gradient?.let { it1 ->
                            saveGradient(
                                it1.copy(
                                    attributes = it1.attributes.copy(
                                        fontId = it?.id
                                    )
                                )
                            )
                        }

                        Theme.SolidColor -> solidColorEdit?.color?.let { it1 ->
                            saveColor(
                                it1.copy(
                                    attributes = it1.attributes.copy(
                                        fontId = it?.id
                                    )
                                )
                            )
                        }

                        Theme.Image -> imageEdit?.quoteImage?.let { it1 ->
                            saveQuoteImage(
                                it1.copy(
                                    attributes = it1.attributes.copy(
                                        fontId = it?.id
                                    )
                                )
                            )
                        }

                        else -> {}
                    }
                },
                saveGradient = { saveGradient(it) },
                saveColor = { saveColor(it) },
                saveQuoteImage = { saveQuoteImage(it) }) {
                showThemeEditor = false
            }
        }
        @Composable
        fun NothingToShow() {
            Column(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.nothing_to_show)
                )
                if (quotesPagingItems == null && forYouQuotes.isEmpty() && onFollow != null) ElevatedButton(
                    onClick = onFollow
                ) {
                    Text(text = stringResource(R.string.follow_categories_and_authors))
                }
            }
        }
        if (quotesPagingItems != null) {
            if (quotesPagingItems.loadState.refresh is LoadState.NotLoading && quotesPagingItems.itemCount < 1) NothingToShow()
        } else if (!refreshingForYouQuotes && pagerState.pageCount < 1) {
            NothingToShow()
        }
    }
}