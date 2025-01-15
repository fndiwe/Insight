package com.franklinndiwe.insight.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.GraphicsLayer
import com.franklinndiwe.insight.data.GradientV2
import com.franklinndiwe.insight.data.QuoteV2
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.utils.AppUtils.CreateQuota
import com.franklinndiwe.insight.utils.AppUtils.dimColor
import com.franklinndiwe.insight.utils.unit

@Composable
fun GradientQuote(
    modifier: Modifier = Modifier,
    quote: QuoteV2,
    gradientV2: GradientV2,
    progress: Float? = 0f,
    autoScroll: Boolean = false,
    setting: Setting = Setting(),
    isThemePreview: Boolean = false,
    onToggleAutoScroll: (Boolean) -> Unit = {},
    likeQuote: (QuoteV2) -> Unit,
    shareQuote: (QuoteV2) -> Unit,
    deleteQuote: ((QuoteV2) -> Unit)?,
    downloadQuote: (GraphicsLayer) -> Unit = {},
    onClickLockedIcon: () -> Unit = {},
    onEditTheme: () -> Unit = {},
    onDeleteTheme: () -> Unit = {},
    showThemeEditor: unit = {},
) {
    val gradient = gradientV2.gradient
    val textColor = gradient.attributes.textColor?.let { Color(it) } ?: Color.White
    val colors = gradient.colors.map { Color(it) }
    val brush = Brush.linearGradient(colors)
    CustomizableQuoteThemeCard(modifier,
        textBoxModifier = Modifier.background(
            brush
        ),
        font = gradientV2.font,
        quotaAmount = CreateQuota,
        textColor = textColor,
        quote = quote,
        unlocked = gradient.attributes.unlocked,
        userGenerated = !gradient.attributes.shipped,
        progress = progress,
        bottomColor = gradient.attributes.tintColor?.let { Color(it) }
            ?: colors[colors.lastIndex],
        autoScroll = autoScroll,
        isThemePreview = isThemePreview,
        setting = setting,
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
                .background(brush)
                .background(dimColor)
        )
    }
}