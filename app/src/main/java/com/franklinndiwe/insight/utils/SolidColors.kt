package com.franklinndiwe.insight.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.franklinndiwe.insight.data.BackgroundCoreAttributes
import com.franklinndiwe.insight.data.SolidColor

object SolidColors {
    private val WhiteColor = Color.White.toArgb()
    private val attributes = BackgroundCoreAttributes(textColor = WhiteColor)
    val list = listOf(
        SolidColor(
            background = Color(0xFF512DA8).toArgb(),
            attributes = BackgroundCoreAttributes(unlocked = true, textColor = WhiteColor)
        ),
        SolidColor(
            background = Color(0xFF11520A).toArgb(),
            attributes = BackgroundCoreAttributes(unlocked = true, textColor = WhiteColor)
        ),
        SolidColor(background = Color(0xFF5A0768).toArgb(), attributes = attributes),
        SolidColor(background = Color(0xFF816813).toArgb(), attributes = attributes),
        SolidColor(background = Color(0xFF6F1232).toArgb(), attributes = attributes),
        SolidColor(background = Color(0xFF4A640C).toArgb(), attributes = attributes),
        SolidColor(background = Color(0xFF1D60A2).toArgb(), attributes = attributes),
        SolidColor(background = Colors.Purple, attributes = attributes),
        SolidColor(background = Color(0xFF270E72).toArgb(), attributes = attributes),
        SolidColor(background = Colors.Ochre, attributes = attributes),
        SolidColor(background = Colors.WarmBrown, attributes = attributes),
        SolidColor(background = Colors.Teal, attributes = attributes),
        SolidColor(background = Colors.Mint, attributes = attributes),
    )
}