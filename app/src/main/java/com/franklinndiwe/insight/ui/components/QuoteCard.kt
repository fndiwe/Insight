package com.franklinndiwe.insight.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.unit.dp
import com.franklinndiwe.insight.data.AppFont
import com.franklinndiwe.insight.data.QuoteV2
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.utils.AppUtils.dimColor
import com.franklinndiwe.insight.utils.unit

@Composable
fun QuoteCard(
    modifier: Modifier = Modifier,
    quote: QuoteV2,
    progress: Float? = 0f,
    autoScroll: Boolean = false,
    setting: Setting,
    font: AppFont? = null,
    isThemePreview: Boolean = false,
    onToggleAutoScroll: (Boolean) -> Unit = {},
    likeQuote: ((QuoteV2) -> Unit),
    shareQuote: ((QuoteV2) -> Unit),
    downloadQuote: (GraphicsLayer) -> Unit,
    deleteQuote: ((QuoteV2) -> Unit)?,
    showThemeEditor: unit,
) {
    val bottomColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
    val textColor = MaterialTheme.colorScheme.onSurface
    val color = MaterialTheme.colorScheme.surface
    CustomizableQuoteThemeCard(
        modifier,
        textBoxModifier = Modifier.background(
            bottomColor
        ),
        backgroundContent = {
            Box(
                it
                    .background(color)
                    .background(dimColor)
            )
        },
        textColor = textColor,
        quote = quote,
        font = font,
        progress = progress,
        isThemePreview = isThemePreview,
        autoScroll = autoScroll,
        onToggleAutoScroll = onToggleAutoScroll,
        bottomColor = bottomColor,
        likeQuote = likeQuote,
        shareQuote = shareQuote,
        deleteQuote = deleteQuote,
        downloadQuote = downloadQuote,
        setting = setting,
        showThemeEditor = showThemeEditor
    )
}