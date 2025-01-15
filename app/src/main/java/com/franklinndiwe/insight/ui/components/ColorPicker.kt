package com.franklinndiwe.insight.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.utils.AppUtils.defaultShape
import com.franklinndiwe.insight.utils.unit
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorPickerController
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.ImageColorPicker
import com.github.skydoves.colorpicker.compose.PaletteContentScale
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import kotlin.math.roundToInt

private enum class ColorPickerEnum(@StringRes val text: Int) {
    Image(R.string.image), Wheel(R.string.wheel)
}

@Composable
fun ColorPicker(
    initialColor: Color,
    setColor: (Color) -> Unit,
    imageBitmap: ImageBitmap? = null,
    onDismissRequest: unit,
) {
    var color by remember {
        mutableStateOf(initialColor)
    }
    val shape = defaultShape
    val wheelController = rememberColorPickerController()
    wheelController.setWheelRadius(10.dp)
    val bitmapController = rememberColorPickerController()
    bitmapController.setWheelRadius(10.dp)

    val tileEvenColor = Color.Transparent
    val tileOddColor = Color.Transparent
    val wheelRadius = 8.dp
    val tileSize = 8.dp
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(color.toArgb(), hsv)

    var colorPickerOrdinal by rememberSaveable {
        mutableIntStateOf(ColorPickerEnum.Wheel.ordinal)
    }
    val colorPickerEnum = ColorPickerEnum.entries[colorPickerOrdinal]

    @Composable
    fun BrightnessAndOpacitySlider(controller: ColorPickerController) {
        val style = MaterialTheme.typography.bodySmall
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = stringResource(R.string.brightness), style = style)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    BrightnessSlider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .height(17.dp),
                        controller = controller,
                        borderRadius = wheelRadius,
                        wheelRadius = wheelRadius,
                        initialColor = initialColor
                    )
                    Text(
                        text = "${(hsv[2] * 100).roundToInt()}%",
                        style = MaterialTheme.typography.labelSmall
                    )
                }

            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(text = stringResource(R.string.opacity), style = style)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AlphaSlider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .height(17.dp),
                        controller = controller,
                        borderRadius = wheelRadius,
                        wheelRadius = wheelRadius,
                        tileSize = tileSize,
                        tileOddColor = Color.Gray,
                        tileEvenColor = Color.DarkGray,
                        initialColor = initialColor
                    )
                    Text(
                        text = "${(color.alpha * 100).roundToInt()}%",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }

    @Composable
    fun WheelColorPicker() {
        Column(
            Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HsvColorPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                controller = wheelController,
                onColorChanged = {
                    color = it.color
                },
                initialColor = initialColor
            )
            AlphaTile(
                modifier = Modifier
                    .clip(shape)
                    .height(40.dp)
                    .width(100.dp)
                    .align(Alignment.CenterHorizontally),
                wheelController,
                tileSize = tileSize,
                tileEvenColor = tileEvenColor,
                tileOddColor = tileOddColor
            )
            BrightnessAndOpacitySlider(wheelController)
        }
    }

    @Composable
    fun BitmapColorPicker() {
        Column(
            Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (imageBitmap != null) {
                ImageColorPicker(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(shape),
                    controller = bitmapController,
                    paletteImageBitmap = imageBitmap,
                    paletteContentScale = PaletteContentScale.CROP
                ) {
                    color = it.color
                }
            }
            AlphaTile(
                modifier = Modifier
                    .clip(shape)
                    .height(40.dp)
                    .width(100.dp)
                    .align(Alignment.CenterHorizontally),
                bitmapController,
                tileSize = tileSize,
                tileEvenColor = tileEvenColor,
                tileOddColor = tileOddColor
            )
            BrightnessAndOpacitySlider(bitmapController)
        }
    }
    AlertDialog(onDismissRequest = onDismissRequest, confirmButton = {
        TextButton(onClick = {
            setColor(color)
            onDismissRequest()
        }) {
            Text(text = stringResource(id = R.string.confirm))
        }
    }, dismissButton = {
        TextButton(onClick = onDismissRequest) {
            Text(text = stringResource(id = R.string.cancel))
        }
    }, text = {
        when (colorPickerEnum) {
            ColorPickerEnum.Image -> BitmapColorPicker()
            ColorPickerEnum.Wheel -> WheelColorPicker()
        }
    }, title = {
        if (imageBitmap != null) {
            val entries = ColorPickerEnum.entries
            AlertDialogTabRow(Pair(colorPickerEnum.ordinal, colorPickerEnum.text),
                entries.map { Pair(it.ordinal, it.text) }) {
                colorPickerOrdinal = entries[it.first].ordinal
            }
        }
    })
}

@Composable
fun AlertDialogTabRow(
    selectedItem: Pair<Int, Int>,
    items: List<Pair<Int, Int>>,
    onClick: (Pair<Int, Int>) -> Unit,
) {
    TabRow(selectedItem.first, containerColor = Color.Transparent, indicator = {}, divider = {}) {
        items.forEach {
            val selected = selectedItem == it
            val style =
                if (!selected) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                )
            Tab(
                selected = selected,
                onClick = { onClick(it) },
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Text(text = stringResource(id = it.second), style = style)
            }
        }
    }
}