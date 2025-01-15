package com.franklinndiwe.insight.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.GraphicsLayer
import com.franklinndiwe.insight.data.QuoteV2
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.data.SolidColorV2
import com.franklinndiwe.insight.utils.AppUtils.CreateQuota
import com.franklinndiwe.insight.utils.AppUtils.dimColor
import com.franklinndiwe.insight.utils.unit
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun SolidColorQuote(
    modifier: Modifier = Modifier,
    quote: QuoteV2,
    colorV2: SolidColorV2,
    progress: Float? = 0f,
    autoScroll: Boolean = false,
    setting: Setting = Setting(),
    isThemePreview: Boolean = false,
    onToggleAutoScroll: (Boolean) -> Unit = {},
    likeQuote: (QuoteV2) -> Unit,
    shareQuote: (QuoteV2) -> Unit,
    downloadQuote: (GraphicsLayer) -> Unit = {},
    deleteQuote: ((QuoteV2) -> Unit)?,
    onClickLockedIcon: () -> Unit = {},
    onEditTheme: () -> Unit = {},
    onDeleteTheme: () -> Unit = {},
    showThemeEditor: unit = {},
) {
    val color = colorV2.color
    val textColor = color.attributes.textColor?.let { Color(it) } ?: Color.White
    CustomizableQuoteThemeCard(
        modifier,
        textBoxModifier = Modifier.background(
            Color(color.background)
        ),
        font = colorV2.font,
        quotaAmount = CreateQuota,
        textColor = textColor,
        quote = quote,
        unlocked = color.attributes.unlocked,
        userGenerated = !color.attributes.shipped,
        progress = progress,
        bottomColor = Color(color.attributes.tintColor ?: color.background),
        autoScroll = autoScroll,
        setting = setting,
        isThemePreview = isThemePreview,
        onToggleAutoScroll = onToggleAutoScroll,
        likeQuote = likeQuote,
        shareQuote = shareQuote,
        downloadQuote = downloadQuote,
        deleteQuote = deleteQuote,
        onClickQuota = onClickLockedIcon,
        onDeleteTheme = onDeleteTheme,
        onEditTheme = onEditTheme,
        showThemeEditor = showThemeEditor
    ) {
        Box(
            it
                .background(
                    color = Color(color.background)
                )
                .background(dimColor)
        )
    }
}