package com.franklinndiwe.insight.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.QuoteV2
import com.franklinndiwe.insight.utils.AppUtils.likedQuote

@Composable
fun RowOrColumnIcons(
    modifier: Modifier = Modifier,
    quoteV2: QuoteV2,
    isRow: Boolean = true,
    filledColor: Color = Color.Transparent,
    downloadQuote: () -> Unit = {},
    likeQuote: (QuoteV2) -> Unit,
    shareQuote: (QuoteV2) -> Unit,
    deleteQuote: ((QuoteV2) -> Unit)?,
) {

    @Composable
    fun RowIcon(onClick: () -> Unit, imageVector: ImageVector, contentDescription: String?) {
        FilledIconButton(
            onClick = onClick,
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = filledColor)
        ) {
            Icon(
                imageVector = imageVector, contentDescription = contentDescription
            )
        }
    }

    @Composable
    fun AllIcons() {
        val liked = quoteV2.quote.liked != null
        RowIcon(
            onClick = {
                likeQuote(likedQuote(quoteV2))
            },
            imageVector = if (liked) Icons.Rounded.ThumbUp else Icons.Outlined.ThumbUp,
            contentDescription = stringResource(id = if (liked) R.string.unlike_quote else R.string.like_quote)
        )
        RowIcon(
            onClick = { shareQuote(quoteV2) },
            imageVector = ImageVector.vectorResource(R.drawable.share),
            contentDescription = stringResource(id = R.string.share)
        )
        RowIcon(
            onClick = downloadQuote,
            imageVector = ImageVector.vectorResource(R.drawable.save),
            contentDescription = stringResource(id = R.string.save_quote)
        )
        if (deleteQuote != null) {
            RowIcon(
                onClick = { deleteQuote(quoteV2) },
                imageVector = Icons.Outlined.Delete,
                contentDescription = stringResource(id = R.string.delete)
            )
        }
    }
    if (isRow) Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = modifier.fillMaxWidth()
    ) {
        AllIcons()
    } else Column(
        modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AllIcons()
    }
}