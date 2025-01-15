package com.franklinndiwe.insight.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.AppFont
import com.franklinndiwe.insight.data.GradientV2
import com.franklinndiwe.insight.data.QuoteImageV2
import com.franklinndiwe.insight.data.SolidColorV2
import com.franklinndiwe.insight.utils.AppUtils.ImportQuota
import com.franklinndiwe.insight.utils.AppUtils.cardSize
import com.franklinndiwe.insight.utils.AppUtils.defaultShape
import com.franklinndiwe.insight.utils.AppUtils.shadowElevation
import com.franklinndiwe.insight.utils.AppUtils.tonalElevation

@Composable
fun ImageThemePreviewCard(
    image: QuoteImageV2,
    onClickLockIcon: (QuoteImageV2) -> Unit,
    onDelete: (QuoteImageV2) -> Unit,
    onEdit: (QuoteImageV2) -> Unit,
) {
    Box(
        Modifier
            .size(cardSize)
            .clip(defaultShape)
    ) {
        ImageQuote(quote = getQuoteV2List()[0],
            imageV2 = image,
            progress = null,
            isThemePreview = true,
            likeQuote = {},
            shareQuote = {},
            deleteQuote = null,
            onClickLockedIcon = { onClickLockIcon(image) },
            onEditTheme = { onEdit(image) }, onDeleteTheme = { onDelete(image) })
    }
}

@Composable
fun GradientThemePreviewCard(
    modifier: Modifier = Modifier,
    gradientV2: GradientV2,
    onClickLockIcon: (GradientV2) -> Unit,
    onDelete: (GradientV2) -> Unit,
    onEdit: (GradientV2) -> Unit,
) {
    Box(
        modifier
            .size(cardSize)
            .clip(defaultShape)
    ) {
        GradientQuote(quote = getQuoteV2List()[0],
            gradientV2 = gradientV2,
            progress = null,
            isThemePreview = true,
            likeQuote = {},
            shareQuote = {},
            deleteQuote = null,
            onClickLockedIcon = { onClickLockIcon(gradientV2) },
            onEditTheme = { onEdit(gradientV2) }, onDeleteTheme = { onDelete(gradientV2) })
    }
}

@Composable
fun SolidColorThemePreviewCard(
    colorV2: SolidColorV2,
    onClickLockIcon: (SolidColorV2) -> Unit,
    onDelete: (SolidColorV2) -> Unit,
    onEdit: (SolidColorV2) -> Unit,
) {
    Box(
        Modifier
            .size(cardSize)
            .clip(defaultShape)
    ) {
        SolidColorQuote(quote = getQuoteV2List()[0],
            colorV2 = colorV2,
            progress = null,
            isThemePreview = true,
            likeQuote = {},
            shareQuote = {},
            deleteQuote = null,
            onClickLockedIcon = { onClickLockIcon(colorV2) },
            onEditTheme = { onEdit(colorV2) }, onDeleteTheme = { onDelete(colorV2) })
    }
}

@Composable
fun FontsPreviewCard(
    font: AppFont,
    onClickLockIcon: (AppFont) -> Unit,
    onDelete: (AppFont) -> Unit,
    onEdit: (AppFont) -> Unit,
) {
    Surface(
        Modifier.size(cardSize),
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation,
        shape = defaultShape
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (font.unlocked) FilledIconButton(onClick = { onEdit(font) }) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = stringResource(id = R.string.edit)
                    )
                }
                if (!font.unlocked) Button(
                    onClick = { onClickLockIcon(font) },
                    contentPadding = PaddingValues()
                ) {
                    Icon(
                        ImageVector.vectorResource(R.drawable.bolt),
                        stringResource(id = R.string.app_quota)
                    )
                    Text(text = ImportQuota.toString())
                } else if (!font.shipped) {
                    FilledIconButton(onClick = { onDelete(font) }) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = stringResource(id = R.string.delete)
                        )
                    }
                }
            }

            QuoteText(
                quote = font.name,
                author = null,
                themeFont = font,
                isFontPreview = true,
                isThemePreview = true
            )
        }
    }
}