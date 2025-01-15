package com.franklinndiwe.insight.data

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.vector.ImageVector

@Immutable
data class MenuItem(
    val id: Int,
    val outlinedIcon: ImageVector? = null,
    val filledIcon: ImageVector? = null,
    val name: Int,
    val route: String,
)
