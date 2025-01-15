package com.franklinndiwe.insight.ui.screens.quote

import android.app.Activity
import android.os.Build
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.paging.PagingData
import com.franklinndiwe.insight.data.QuoteV2
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.data.SortOrder
import com.franklinndiwe.insight.ui.screens.ReusableViewScreen
import com.franklinndiwe.insight.utils.QuoteScreenImpl
import com.franklinndiwe.insight.utils.SwipeDirection
import com.franklinndiwe.insight.utils.Theme
import com.franklinndiwe.insight.utils.sortOrderTypeUnit
import com.franklinndiwe.insight.utils.unit
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import kotlinx.coroutines.flow.Flow

@Suppress("DEPRECATION")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReusableQuoteScreen(
    title: String,
    quoteScreenImpl: QuoteScreenImpl,
    writeAccessState: MultiplePermissionsState,
    quotes: Flow<PagingData<QuoteV2>>?,
    setting: Setting,
    sortOrder: SortOrder?,
    navigateBack: unit,
    onSearch: sortOrderTypeUnit,
    onCreateQuote: unit?,
    onNavigateToForYouSettings: unit? = null,
    numberOfQuotes: Int? = null,
    forYouQuotes: List<Int> = emptyList(),
    refreshingForYouQuotes: Boolean = false,
    refreshForYouQuotes: unit = {},
) {
    val view = LocalView.current
    DisposableEffect(key1 = setting.swipeDirection) {
        val window = (view.context as Activity).window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        fun onDispose() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
            } else {
                window.insetsController?.apply {
                    show(WindowInsets.Type.statusBars())
                }
            }
        }

        if (setting.swipeDirection == SwipeDirection.Vertical) if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        } else {
            window.insetsController?.apply {
                hide(WindowInsets.Type.statusBars())
                systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else onDispose()
        onDispose {
            if (setting.swipeDirection == SwipeDirection.Vertical) onDispose()
        }
    }
    val quoteUtils = quoteScreenImpl.quoteUtils
    val appFontUtils = quoteScreenImpl.appFontUtils
    val gradientUtils = quoteScreenImpl.gradientUtils
    val solidColorUtils = quoteScreenImpl.colorUtils
    val quoteImageUtils = quoteScreenImpl.quoteImageUtils
    ReusableViewScreen(
        name = title,
        quotes = remember(sortOrder) {
            quotes
        },
        forYouQuotes = forYouQuotes,
        refreshingForYouQuotes = refreshingForYouQuotes,
        refreshForYouQuotes = refreshForYouQuotes,
        colorUtils = if (setting.theme == Theme.SolidColor) solidColorUtils else null,
        quoteImageUtils = if (setting.theme == Theme.Image) quoteImageUtils else null,
        gradientUtils = if (setting.theme == Theme.Gradient) gradientUtils else null,
        fontUtils = appFontUtils,
        quotaUtils = quoteScreenImpl.quotaUtils,
        setting = setting,
        onAdd = onCreateQuote,
        sortOrder = sortOrder,
        changeSortOrder = { quoteScreenImpl.changeSortOrder(it) },
        quoteUtils = quoteUtils,
        navigateBack = navigateBack,
        onSearch = onSearch,
        writeAccessState = writeAccessState,
        onNavigateToForYouSettings = onNavigateToForYouSettings,
        numberOfQuotes = numberOfQuotes
    ) { quoteScreenImpl.settingUtils.updateSetting(it) }
}