package com.franklinndiwe.insight.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.franklinndiwe.insight.data.BackgroundCoreAttributes
import com.franklinndiwe.insight.data.Gradient

object Gradients {
    private val ColorBlue = Color.Blue.toArgb()
    private val ColorRed = Color.Red.toArgb()
    private val ColorYellow = Color.Yellow.toArgb()
    private val ColorGreen = Color.Green.toArgb()
    private val ColorDarkGray = Color.DarkGray.toArgb()
    private val ColorCyan = Color.Cyan.toArgb()
    val list = listOf(
        Gradient(
            colors = listOf(
                ColorBlue, Colors.Pink, Colors.Lavender, Colors.Mint
            ),
            attributes = BackgroundCoreAttributes(unlocked = true, textColor = ColorBlue),
        ), Gradient(
            colors = listOf(
                Colors.Orange, Colors.Purple, ColorYellow
            ),
            attributes = BackgroundCoreAttributes(
                unlocked = true,
                textColor = Colors.Orange,
                tintColor = Colors.Purple
            )
        ), Gradient(
            colors = listOf(
                ColorBlue, Colors.Turquoise, Colors.Teal
            ),
            attributes = BackgroundCoreAttributes(textColor = Color.Black.toArgb()),
        ), Gradient(
            colors = listOf(
                Colors.WarmBrown, ColorGreen, Colors.Ochre
            ),
            attributes = BackgroundCoreAttributes(
                textColor = Color.Black.toArgb(),
                tintColor = Colors.WarmBrown
            )
        ), Gradient(
            colors = listOf(
                ColorRed, Colors.Purple
            ), attributes = BackgroundCoreAttributes(textColor = ColorRed)
        ), Gradient(
            colors = listOf(
                ColorYellow, ColorGreen
            ), attributes = BackgroundCoreAttributes(textColor = ColorYellow)
        ), Gradient(
            colors = listOf(
                Colors.Orange, ColorCyan, Colors.Purple
            ), attributes = BackgroundCoreAttributes(textColor = Colors.Orange)
        ), Gradient(
            colors = listOf(
                ColorDarkGray, Colors.Orange
            ),
            attributes = BackgroundCoreAttributes(
                textColor = ColorDarkGray,
                tintColor = Colors.Orange
            )
        ), Gradient(
            colors = listOf(
                ColorDarkGray, Colors.Lavender
            ),
            attributes = BackgroundCoreAttributes(
                textColor = Colors.Lavender,
                tintColor = ColorDarkGray
            )
        ), Gradient(
            colors = listOf(
                Color(0xFF0F2027).toArgb(),
                Color(0xFF203A43).toArgb(),
                Color(0xFF2C5364).toArgb()
            ), attributes = BackgroundCoreAttributes(textColor = Color(0xFF82BDD6).toArgb())
        ), Gradient(
            colors = listOf(Color(0xFFb92b27).toArgb(), Color(0xFF1565C0).toArgb()),
            attributes = BackgroundCoreAttributes(textColor = Color(0xFFD27A78).toArgb())
        ), Gradient(
            colors = listOf(Color(0xFF373B44).toArgb(), Color(0xFF4286f4).toArgb()),
            attributes = BackgroundCoreAttributes(
                textColor = Color(0xFF76A3EB).toArgb(),
                tintColor = Color(0xFF373B44).toArgb()
            )
        ), Gradient(
            colors = listOf(Color(0xFFFF0099).toArgb(), Color(0xFF493240).toArgb())
        ), Gradient(
            colors = listOf(Color(0xFF1f4037).toArgb(), Color(0xFF99f2c8).toArgb()),
            attributes = BackgroundCoreAttributes(
                textColor = Color(0xFF99f2c8).toArgb(),
                tintColor = Color(0xFF1f4037).toArgb()
            )
        ), Gradient(
            colors = listOf(Color(0xFFc31432).toArgb(), Color(0xFF240b36).toArgb()),
            attributes = BackgroundCoreAttributes(textColor = Color(0xFFD63D58).toArgb())
        ), Gradient(
            colors = listOf(ColorGreen, ColorYellow, ColorRed),
            attributes = BackgroundCoreAttributes(textColor = Color.Black.toArgb())
        ), Gradient(
            colors = listOf(Color(0xFFad5389).toArgb(), Color(0xFF3c1053).toArgb()),
            attributes = BackgroundCoreAttributes(textColor = Color(0xFFD882B5).toArgb())
        ), Gradient(
            colors = listOf(Color(0xFF40E0D0).toArgb(), Color(0xFFFF8C00).toArgb(), Colors.Pink),
            attributes = BackgroundCoreAttributes(
                textColor = Color(0xFF40E0D0).toArgb(),
                tintColor = Color(0xFFFF8C00).toArgb()
            )
        ), Gradient(
            colors = listOf(
                Color(0xFF0f0c29).toArgb(),
                Color(0xFF302b63).toArgb(),
                Color(0xFF24243e).toArgb()
            ), attributes = BackgroundCoreAttributes(textColor = Color(0xFF9591C7).toArgb())
        )
    )
}