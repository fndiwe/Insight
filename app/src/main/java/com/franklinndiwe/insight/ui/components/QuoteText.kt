package com.franklinndiwe.insight.ui.components

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toFile
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.AppFont
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.utils.AppUtils.LargeQuoteSize

@Composable
fun QuoteText(
    modifier: Modifier = Modifier,
    quote: String,
    author: String?,
    themeFont: AppFont? = null,
    isThemePreview: Boolean = false,
    isFontPreview: Boolean = false,
    isDailyQuote: Boolean = false,
    setting: Setting = Setting(),
) {
    val textSize: Int = quote.length
    val fontFamily = themeFont?.toFontFamily()
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isFontPreview) Icon(
            imageVector = ImageVector.vectorResource(R.drawable.quote),
            contentDescription = null,
            modifier = Modifier
                .size(if (isDailyQuote) 30.dp else if (isThemePreview) 24.dp else 40.dp)
                .align(Alignment.CenterHorizontally)
        )
        val textColor =
            if (isFontPreview && fontFamily == null) MaterialTheme.colorScheme.error else Color.Unspecified
        val fontSize =
            if (isDailyQuote) if (textSize <= LargeQuoteSize) 20 else 18 else if (isFontPreview) 18 else if (isThemePreview) 14 else if (textSize <= LargeQuoteSize) themeFont?.textSizeForShortQuote
                ?: setting.defaultShortQuoteTextSize else themeFont?.textSizeForLongQuote
                ?: setting.defaultLongQuoteTextSize
        Text(
            text = quote,
            modifier = if (isDailyQuote || isFontPreview) Modifier else Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f, false),
            textAlign = if (textSize <= LargeQuoteSize) TextAlign.Center else TextAlign.Start,
            color = textColor,
            fontSize = fontSize.sp,
            fontFamily = fontFamily,
            lineHeight = (fontSize + 8).sp,
            maxLines = if (isFontPreview) 3 else Int.MAX_VALUE,
            overflow = TextOverflow.Ellipsis
        )
        author?.let { s ->
            Text(
                text = "${Typography.mdash} $s",
                color = textColor,
                fontSize = if (isDailyQuote) 15.sp else if (isThemePreview) 13.sp else themeFont?.authorTextSize?.sp
                    ?: setting.defaultAuthorTextSize.sp,
                fontFamily = fontFamily,
                modifier = Modifier.align(Alignment.End),
                textAlign = TextAlign.End
            )
        }
    }
}

@Composable
fun AppFont.toFontFamily(): FontFamily? {
    val assetManager = LocalContext.current.assets
    return (if (this.shipped) Font(this.path, assetManager) else try {
        Font(Uri.parse(this.path).toFile())
    } catch (_: Exception) {
        null
    })?.let { FontFamily(it) }
}