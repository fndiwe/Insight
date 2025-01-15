package com.franklinndiwe.insight.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.ThumbUp
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.utils.AppUtils
import com.franklinndiwe.insight.utils.AppUtils.defaultShape
import com.franklinndiwe.insight.utils.unit

@Composable
fun CategoryCard(
    text: String,
    userGenerated: Boolean = false,
    unlocked: Boolean = true,
    isLike: Boolean = true,
    likedOrFollowing: Boolean = false,
    onLikeOrFollow: (Boolean) -> Unit = {},
    onClick: unit,
    unlock: unit = {},
    onDelete: unit = {},
) {
    @Composable
    fun CardButton(
        colors: ButtonColors = ButtonDefaults.elevatedButtonColors(),
        onClick: unit,
        content: @Composable RowScope.() -> Unit,
    ) {
        ElevatedButton(
            onClick = onClick,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant),
            contentPadding = PaddingValues(4.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 2.dp, pressedElevation = 3.dp
            ),
            colors = colors,
            content = content
        )
    }
    Surface(
        onClick = onClick,
        tonalElevation = AppUtils.tonalElevation,
        shadowElevation = AppUtils.shadowElevation,
        shape = defaultShape
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = text,
                Modifier.padding(
                    vertical = 16.dp, horizontal = 8.dp
                ),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            if (unlocked) Row(
                modifier = if (userGenerated) Modifier.fillMaxWidth() else Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (userGenerated && isLike) IconButton(
                    onClick = { onLikeOrFollow(!likedOrFollowing) },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = if (likedOrFollowing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Icon(
                        imageVector = if (likedOrFollowing) Icons.Rounded.ThumbUp else Icons.Outlined.ThumbUp,
                        contentDescription = stringResource(id = if (likedOrFollowing) R.string.liked else R.string.like)
                    )
                }
                else CardButton(
                    onClick = { onLikeOrFollow(!likedOrFollowing) },
                    colors = ButtonDefaults.elevatedButtonColors(contentColor = if (likedOrFollowing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                ) {
                    Icon(
                        imageVector = if (likedOrFollowing) if (isLike) Icons.Rounded.ThumbUp else ImageVector.vectorResource(
                            R.drawable.following
                        ) else if (isLike) Icons.Outlined.ThumbUp else Icons.Rounded.Add,
                        contentDescription = stringResource(id = if (likedOrFollowing) if (isLike) R.string.liked else R.string.following else if (isLike) R.string.like else R.string.follow),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text(text = stringResource(id = if (likedOrFollowing) if (isLike) R.string.liked else R.string.following else if (isLike) R.string.like else R.string.follow))
                }
                if (userGenerated && isLike) IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Rounded.Delete,
                        contentDescription = stringResource(id = R.string.delete)
                    )
                }
            } else {
                CardButton(onClick = unlock) {
                    Text(
                        text = stringResource(id = R.string.unlock)
                    )
                }
            }
        }
    }
}