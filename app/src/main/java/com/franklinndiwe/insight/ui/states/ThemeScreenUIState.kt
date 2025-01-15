package com.franklinndiwe.insight.ui.states

import com.franklinndiwe.insight.data.AppFont
import com.franklinndiwe.insight.data.Gradient
import com.franklinndiwe.insight.data.QuoteImage
import com.franklinndiwe.insight.data.QuoteImageCategory
import com.franklinndiwe.insight.data.SolidColor

data class ThemeScreenUIState(
    val quoteImage: QuoteImage? = null,
    val gradient: Gradient? = null,
    val color: SolidColor? = null,
    val font: AppFont? = null,
    val imageCategory: QuoteImageCategory? = null,
)
