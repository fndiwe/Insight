package com.franklinndiwe.insight.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.AppFont
import com.franklinndiwe.insight.data.Gradient
import com.franklinndiwe.insight.data.GradientV2
import com.franklinndiwe.insight.data.QuoteImage
import com.franklinndiwe.insight.data.QuoteImageV2
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.data.SolidColor
import com.franklinndiwe.insight.data.SolidColorV2
import com.franklinndiwe.insight.ui.screens.theme.ColorEditorCard
import com.franklinndiwe.insight.ui.screens.theme.FontEditorCard
import com.franklinndiwe.insight.ui.screens.theme.GradientEditorCard
import com.franklinndiwe.insight.ui.screens.theme.ImageEditorCard
import com.franklinndiwe.insight.utils.unit
import kotlinx.coroutines.flow.Flow

private enum class ThemeEditorEnum(@StringRes val text: Int) {
    Font(R.string.font), Color(R.string.colors)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeEditorBottomSheet(
    gradientV2: GradientV2?,
    colorV2: SolidColorV2?,
    imageV2: QuoteImageV2?,
    quoteLength: Int,
    setting: Setting,
    updateSetting: (Setting) -> Unit,
    fonts: Flow<PagingData<AppFont>>?,
    font: AppFont?,
    updateFont: (AppFont) -> Unit,
    setFont: ((AppFont?) -> Unit)?,
    saveGradient: (Gradient) -> Unit,
    saveColor: (SolidColor) -> Unit,
    saveQuoteImage: (QuoteImage) -> Unit,
    onDismissRequest: unit,
) {
    ModalBottomSheet(onDismissRequest = onDismissRequest) {
        ThemeEditorControls(
            gradientV2,
            colorV2,
            imageV2,
            quoteLength,
            setting,
            updateSetting,
            fonts,
            font,
            updateFont,
            setFont,
            saveGradient,
            saveColor,
            saveQuoteImage
        )
    }
}

@Composable
fun ThemeEditorControls(
    gradientV2: GradientV2?,
    colorV2: SolidColorV2?,
    imageV2: QuoteImageV2?,
    quoteLength: Int,
    setting: Setting,
    updateSetting: (Setting) -> Unit,
    fonts: Flow<PagingData<AppFont>>?,
    font: AppFont?,
    updateFont: (AppFont) -> Unit,
    setFont: ((AppFont?) -> Unit)?,
    saveGradient: (Gradient) -> Unit = {},
    saveColor: (SolidColor) -> Unit = {},
    saveQuoteImage: (QuoteImage) -> Unit = {},
) {
    var themeEditorOrdinal by rememberSaveable {
        mutableIntStateOf(ThemeEditorEnum.Font.ordinal)
    }
    val entries = ThemeEditorEnum.entries
    val themeEditorEnum = entries[themeEditorOrdinal]
    val isSurface = gradientV2 == null && colorV2 == null && imageV2 == null

    Column(
        Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!isSurface) AlertDialogTabRow(Pair(themeEditorEnum.ordinal, themeEditorEnum.text),
            entries.map { Pair(it.ordinal, it.text) }) {
            themeEditorOrdinal = entries[it.first].ordinal
        }
        when (themeEditorEnum) {
            ThemeEditorEnum.Font -> FontEditorCard(
                quoteLength, setting, updateSetting, fonts, font, updateFont, setFont
            )

            ThemeEditorEnum.Color -> when {
                gradientV2 != null -> GradientEditorCard(gradientV2.gradient, saveGradient)
                colorV2 != null -> ColorEditorCard(colorV2.color, saveColor)
                imageV2 != null -> ImageEditorCard(imageV2.quoteImage, saveQuoteImage)
            }
        }
    }
}