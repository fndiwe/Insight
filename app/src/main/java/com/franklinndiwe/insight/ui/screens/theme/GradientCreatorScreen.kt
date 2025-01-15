package com.franklinndiwe.insight.ui.screens.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.BackgroundCoreAttributes
import com.franklinndiwe.insight.data.Gradient
import com.franklinndiwe.insight.ui.components.ColorPicker
import com.franklinndiwe.insight.utils.AppUtils.defaultShape


@Composable
fun GradientCreatorScreen(
    listOfColors: SnapshotStateList<Int?>,
    editGradient: (Gradient) -> Unit,
) {
    var currentIndex by rememberSaveable {
        mutableIntStateOf(0)
    }
    val surface = MaterialTheme.colorScheme.surface
    val borderStroke = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface)
    var showColorPicker by rememberSaveable {
        mutableStateOf(false)
    }
    Column(
        Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(36.dp)
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .size(250.dp)
            .border(borderStroke, defaultShape)
            .background(brush = Brush.linearGradient(if (listOfColors.filterNotNull().size < 2) List(
                2
            ) { surface } else listOfColors
                .filterNotNull()
                .map {
                    Color(
                        it
                    )
                }), defaultShape
            ), contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(id = R.string.preview),
                style = MaterialTheme.typography.titleLarge
            )
        }
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(4) { i ->
                val currentColor = listOfColors[i]
                val backgroundColor = currentColor?.let { it1 -> Color(it1) }
                Box {
                    if (currentColor != null) FilledIconButton(
                        onClick = {
                            listOfColors[i] = null
                            listOfColors.sortBy { it == null }
                        },
                        Modifier
                            .align(
                                Alignment.TopEnd
                            )
                            .offset(x = 7.dp, y = (-8).dp)
                            .zIndex(1f)
                            .size(22.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = stringResource(id = R.string.remove_color),
                            Modifier.size(18.dp)
                        )
                    }
                    OutlinedButton(
                        contentPadding = PaddingValues(20.dp),
                        shape = defaultShape,
                        onClick = {
                            currentIndex = i
                            showColorPicker = true
                        },
                        enabled = i <= 1 || listOfColors[i - 1] != null,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = backgroundColor ?: surface,
                            contentColor = backgroundColor
                                ?: MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = ImageVector.vectorResource(R.drawable.color_lens),
                                contentDescription = stringResource(id = R.string.click_to_add_color),
                                Modifier.size(30.dp)
                            )
                        }
                    }
                }
            }
        }
        Button(
            onClick = {
                val colors = listOfColors.filterNotNull()
                if (colors.isNotEmpty()) editGradient(
                    Gradient(
                        colors = colors, attributes = BackgroundCoreAttributes(
                            unlocked = true,
                            shipped = false,
                            textColor = Color.White.toArgb(),
                            tintColor = colors[colors.lastIndex]
                        )
                    )
                )
            }, Modifier.fillMaxWidth(), listOfColors[0] != null && listOfColors[1] != null
        ) {
            Text(text = stringResource(id = R.string.next))
        }
    }
    if (showColorPicker) ColorPicker(initialColor = listOfColors[currentIndex]?.let {
        Color(
            it
        )
    } ?: Color.White, setColor = {
        listOfColors[currentIndex] = it.toArgb()
    }) {
        showColorPicker = false
    }
}