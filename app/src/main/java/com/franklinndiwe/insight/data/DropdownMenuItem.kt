package com.franklinndiwe.insight.data

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily

data class AppDropdownMenuItem(
    @StringRes val text: Int,
    val leadingIcon: ImageVector? = null,
    val trailingIcon: ImageVector? = null,
    val trailingComposable: @Composable (() -> Unit)? = null,
    val fontFamily: FontFamily? = null,
    val onClick: () -> Unit,
)
