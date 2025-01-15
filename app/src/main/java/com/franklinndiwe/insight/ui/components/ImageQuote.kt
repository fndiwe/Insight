package com.franklinndiwe.insight.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.AppFont
import com.franklinndiwe.insight.data.QuoteImageV2
import com.franklinndiwe.insight.data.QuoteV2
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.utils.AppUtils.ImportQuota
import com.franklinndiwe.insight.utils.AppUtils.LargeQuoteSize
import com.franklinndiwe.insight.utils.BitmapUtils.getBackgroundColor
import com.franklinndiwe.insight.utils.BitmapUtils.getSupposedTextColor
import com.franklinndiwe.insight.utils.BitmapUtils.loadBitmap
import com.franklinndiwe.insight.utils.unit

@Composable
fun ImageQuote(
    modifier: Modifier = Modifier,
    quote: QuoteV2,
    imageV2: QuoteImageV2,
    progress: Float? = 0f,
    setting: Setting = Setting(),
    autoScroll: Boolean = false,
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
    val image = imageV2.quoteImage
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val imagePath = image.path

    val bitmap = remember { loadBitmap(context, imagePath, scope) }
    val backgroundColor = remember { getBackgroundColor(bitmap) }
    val textColor = remember { Color(getSupposedTextColor(bitmap)) }
    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(context).data(imagePath).build()
    )
    CustomizableQuoteThemeCard(modifier,
        textBoxModifier = Modifier.paint(
            painter, contentScale = ContentScale.Crop, colorFilter = ColorFilter.tint(
                backgroundColor.copy(if (isThemePreview) .3f else .5f), BlendMode.Darken
            )
        ),
        font = imageV2.font,
        quotaAmount = ImportQuota,
        textColor = image.attributes.textColor?.let { Color(it) } ?: textColor,
        quote = quote,
        unlocked = image.attributes.unlocked,
        userGenerated = !image.attributes.shipped,
        progress = progress,
        setting = setting,
        backgroundColor = backgroundColor,
        bottomColor = image.attributes.tintColor?.let { Color(it) } ?: backgroundColor,
        autoScroll = autoScroll,
        isThemePreview = isThemePreview,
        onToggleAutoScroll = onToggleAutoScroll,
        likeQuote = likeQuote,
        shareQuote = shareQuote,
        downloadQuote = downloadQuote,
        deleteQuote = deleteQuote,
        onClickQuota = onClickLockedIcon,
        onDeleteTheme = onDeleteTheme,
        onEditTheme = onEditTheme,
        showThemeEditor = showThemeEditor) {
        AsyncImage(
            model = ImageRequest.Builder(context).data(imagePath).build(),
            contentDescription = null,
            it.blur(5.dp),
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(
                textColor.copy(.05f), BlendMode.Lighten
            )
        )
    }
}

@Composable
fun CustomizableQuoteThemeCard(
    modifier: Modifier = Modifier,
    textBoxModifier: Modifier = Modifier,
    font: AppFont?,
    quotaAmount: Int = 0,
    setting: Setting,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    quote: QuoteV2,
    unlocked: Boolean = true,
    userGenerated: Boolean = false,
    progress: Float? = 0f,
    backgroundColor: Color? = null,
    bottomColor: Color,
    autoScroll: Boolean = false,
    isThemePreview: Boolean = false,
    onToggleAutoScroll: (Boolean) -> Unit = {},
    likeQuote: (QuoteV2) -> Unit,
    shareQuote: (QuoteV2) -> Unit,
    downloadQuote: (GraphicsLayer) -> Unit,
    deleteQuote: ((QuoteV2) -> Unit)?,
    onClickQuota: () -> Unit = {},
    onDeleteTheme: () -> Unit = {},
    onEditTheme: () -> Unit = {},
    showThemeEditor: unit,
    backgroundContent: @Composable (Modifier) -> Unit,
) {
    Box(modifier) {
        if (!isThemePreview) backgroundContent(Modifier.fillMaxSize())
        if (isThemePreview) {
            val filledIconButtonColor = IconButtonDefaults.filledIconButtonColors(
                containerColor = bottomColor, contentColor = textColor
            )
            val filledIconModifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.TopCenter)
                .zIndex(1f)
            Row(
                filledIconModifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (!unlocked) Button(
                    onClick = onClickQuota, colors = ButtonDefaults.buttonColors(
                        containerColor = bottomColor, contentColor = textColor
                    ), contentPadding = PaddingValues(horizontal = 20.dp)
                ) {
                    Text(text = quotaAmount.toString())
                    Icon(
                        ImageVector.vectorResource(R.drawable.bolt),
                        stringResource(id = R.string.app_quota)
                    )

                } else {
                    FilledIconButton(
                        onClick = onEditTheme, colors = filledIconButtonColor
                    ) {
                        Icon(
                            Icons.Rounded.Edit, stringResource(id = R.string.edit)
                        )
                    }
                    if (userGenerated) FilledIconButton(
                        onClick = onDeleteTheme, colors = filledIconButtonColor
                    ) {
                        Icon(
                            Icons.Rounded.Delete, stringResource(id = R.string.delete)
                        )
                    }
                }
            }
        }
        Column(
            Modifier
                .fillMaxSize()
                .padding(horizontal = if (isThemePreview) 0.dp else 16.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            CompositionLocalProvider(value = LocalContentColor.provides(textColor)) {

                QuoteWithIcons(
                    textBoxModifier,
                    bottomColor = bottomColor,
                    centerQuote = progress == null && quote.quote.text.length <= LargeQuoteSize,
                    likeQuote = likeQuote,
                    shareQuote = shareQuote,
                    deleteQuote = deleteQuote,
                    isThemePreview = isThemePreview,
                    quote = quote,
                    font = font,
                    downloadQuote = downloadQuote,
                    backgroundColor = backgroundColor,
                    textColor = textColor,
                    progress = progress,
                    setting = setting,
                    autoScroll = autoScroll,
                    onToggleAutoScroll = onToggleAutoScroll,
                    showThemeEditor = showThemeEditor
                )
            }
        }
    }
}