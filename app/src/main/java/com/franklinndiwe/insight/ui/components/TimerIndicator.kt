package com.franklinndiwe.insight.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.franklinndiwe.insight.R

@Composable
fun TimerIndicator(
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    backgroundColor: Color? = null,
    progress: Float = 0f,
    autoScroll: Boolean = true,
    onToggleAutoScroll: (Boolean) -> Unit = {},
) {
    val animateProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
        label = ""
    )
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { animateProgress },
            strokeCap = StrokeCap.Round,
            color = textColor,
            trackColor = backgroundColor ?: MaterialTheme.colorScheme.surfaceVariant
        )
        IconButton(onClick = { onToggleAutoScroll(!autoScroll) }) {
            Icon(
                imageVector = if (autoScroll) ImageVector.vectorResource(R.drawable.pause) else Icons.Rounded.PlayArrow,
                contentDescription = stringResource(id = R.string.toggle_auto_scroll)
            )
        }
    }
}