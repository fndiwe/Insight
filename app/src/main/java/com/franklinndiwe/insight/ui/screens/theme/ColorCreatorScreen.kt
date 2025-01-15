package com.franklinndiwe.insight.ui.screens.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.BackgroundCoreAttributes
import com.franklinndiwe.insight.data.SolidColor
import com.franklinndiwe.insight.ui.components.ColorPicker

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorCreatorScreen(
    editColor: (SolidColor) -> Unit,
) {
    val m = MaterialTheme.colorScheme.secondary
    var colorInt by rememberSaveable {
        mutableStateOf<Int?>(null)
    }
    val color = colorInt?.let { Color(it) }
    var expandColorPicker by rememberSaveable {
        mutableStateOf(false)
    }
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp), verticalArrangement = Arrangement.SpaceEvenly
    ) {
        BigColorButton(color = color) {
            expandColorPicker = true
        }
        Button(onClick = {
            colorInt?.let {
                editColor(
                    SolidColor(
                        background = it, attributes = BackgroundCoreAttributes(
                            unlocked = true, shipped = false, textColor = Color.White.toArgb()
                        )
                    )
                )
            }
        }, Modifier.fillMaxWidth(), color != null) {
            Text(text = stringResource(id = R.string.next))
        }
    }
    if (expandColorPicker) ColorPicker(
        initialColor = color ?: m,
        setColor = { colorInt = it.toArgb() }) {
        expandColorPicker = false
    }
}