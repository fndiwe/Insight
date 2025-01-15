package com.franklinndiwe.insight.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.AppFont
import com.franklinndiwe.insight.data.Author
import com.franklinndiwe.insight.data.Category
import com.franklinndiwe.insight.data.Quote
import com.franklinndiwe.insight.data.QuoteV2
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.utils.AppUtils.defaultShape
import com.franklinndiwe.insight.utils.unit

@Composable
fun QuoteWithIcons(
    textBoxModifier: Modifier = Modifier,
    bottomColor: Color = MaterialTheme.colorScheme.surface,
    centerQuote: Boolean = false,
    quote: QuoteV2,
    setting: Setting,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color? = null,
    progress: Float? = 0f,
    autoScroll: Boolean = true,
    onToggleAutoScroll: (Boolean) -> Unit = {},
    font: AppFont?,
    isThemePreview: Boolean = false,
    likeQuote: (QuoteV2) -> Unit,
    shareQuote: (QuoteV2) -> Unit,
    deleteQuote: ((QuoteV2) -> Unit)?,
    downloadQuote: (GraphicsLayer) -> Unit,
    showThemeEditor: unit,
) {
    val graphicsLayer = rememberGraphicsLayer()

    @Composable
    fun QuoteTextComposable(modifier: Modifier = Modifier, textBoxModifier: Modifier = Modifier) {
        Column(
            modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (!isThemePreview) Surface(
                shape = CircleShape,
                color = bottomColor,
                contentColor = textColor
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (progress != null) {
                        TimerIndicator(
                            textColor = textColor,
                            backgroundColor = backgroundColor,
                            progress = progress,
                            autoScroll = autoScroll,
                            onToggleAutoScroll = onToggleAutoScroll
                        )
                    }
                    IconButton(onClick = showThemeEditor) {
                        Icon(
                            imageVector = ImageVector.vectorResource(R.drawable.color_lens),
                            contentDescription = stringResource(id = R.string.customize_theme)
                        )
                    }
                }
            }
            Box(
                Modifier
                    .drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                    }
                    .fillMaxHeight(if (isThemePreview) 1f else .75f)
                    .clip(defaultShape),
            ) {
                Box(
                    textBoxModifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    QuoteText(
                        Modifier.padding(if (!isThemePreview) 16.dp else 8.dp),
                        quote = quote.quote.text,
                        author = quote.author.name,
                        themeFont = font,
                        isThemePreview,
                        setting = setting
                    )
                }
            }
        }


        if (!isThemePreview) RowOrColumnIcons(
            quoteV2 = quote,
            likeQuote = likeQuote,
            filledColor = bottomColor,
            downloadQuote = { downloadQuote(graphicsLayer) },
            shareQuote = shareQuote,
            deleteQuote = if (setting.dailyQuote == quote.quote.id || quote.quote.userGenerated == null) null else deleteQuote
        )
    }
    if (centerQuote) Box(contentAlignment = Alignment.Center) {
        QuoteTextComposable(Modifier.align(Alignment.BottomCenter), textBoxModifier)
    } else QuoteTextComposable(textBoxModifier = textBoxModifier)
}

fun getQuoteV2List() = List(30) {
    QuoteV2(
        quote = Quote(
            it, "Wisdom is too high for a fool.", 2, 3, 4
        ), categories[3], category2 = categories[4], authors[2]
    )
}

val authors = List(30) {
    Author(it, "Proverbs", popular = true)
}

val categories = List(30) {
    Category(it, "Wisdom", unlocked = true, popular = true)
}