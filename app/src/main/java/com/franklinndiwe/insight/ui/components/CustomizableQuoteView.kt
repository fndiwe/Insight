package com.franklinndiwe.insight.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.layer.GraphicsLayer
import com.franklinndiwe.insight.data.AppFont
import com.franklinndiwe.insight.data.GradientV2
import com.franklinndiwe.insight.data.QuoteImageV2
import com.franklinndiwe.insight.data.QuoteV2
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.data.SolidColorV2
import com.franklinndiwe.insight.utils.Theme
import com.franklinndiwe.insight.utils.unit

@Composable
fun CustomizableQuoteView(
    modifier: Modifier = Modifier,
    quote: QuoteV2,
    setting: Setting,
    color: SolidColorV2?,
    image: QuoteImageV2?,
    gradient: GradientV2?,
    font: AppFont? = null,
    progress: Float?,
    autoScroll: Boolean,
    onToggleAutoScroll: (Boolean) -> Unit,
    likeQuote: (QuoteV2) -> Unit,
    shareQuote: (QuoteV2) -> Unit,
    downloadQuote: (GraphicsLayer) -> Unit,
    deleteQuote: ((QuoteV2) -> Unit)?,
    showThemeEditor: unit,
) {
    when (setting.theme) {
        Theme.Card -> QuoteCard(
            modifier,
            quote,
            progress = progress,
            autoScroll = autoScroll,
            onToggleAutoScroll = onToggleAutoScroll,
            likeQuote = likeQuote,
            shareQuote = shareQuote,
            deleteQuote = deleteQuote,
            downloadQuote = downloadQuote,
            setting = setting,
            font = font,
            showThemeEditor = showThemeEditor
        )

        Theme.SolidColor -> color?.let {
            SolidColorQuote(
                modifier = modifier,
                quote = quote,
                colorV2 = it,
                progress = progress,
                autoScroll = autoScroll,
                onToggleAutoScroll = onToggleAutoScroll,
                likeQuote = likeQuote,
                shareQuote = shareQuote,
                deleteQuote = deleteQuote,
                downloadQuote = downloadQuote,
                setting = setting,
                showThemeEditor = showThemeEditor
            )
        }

        Theme.Gradient -> gradient?.let {
            GradientQuote(
                modifier = modifier,
                quote = quote,
                gradientV2 = it,
                progress = progress,
                autoScroll = autoScroll,
                onToggleAutoScroll = onToggleAutoScroll,
                likeQuote = likeQuote,
                shareQuote = shareQuote,
                deleteQuote = deleteQuote,
                downloadQuote = downloadQuote,
                setting = setting,
                showThemeEditor = showThemeEditor
            )
        }

        Theme.Image -> image?.let {
            ImageQuote(
                modifier = modifier,
                quote = quote,
                imageV2 = it,
                progress = progress,
                autoScroll = autoScroll,
                onToggleAutoScroll = onToggleAutoScroll,
                likeQuote = likeQuote,
                shareQuote = shareQuote,
                deleteQuote = deleteQuote,
                downloadQuote = downloadQuote,
                setting = setting,
                showThemeEditor = showThemeEditor
            )
        }
    }
}