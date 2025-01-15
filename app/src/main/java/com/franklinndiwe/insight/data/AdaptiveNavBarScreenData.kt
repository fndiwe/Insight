package com.franklinndiwe.insight.data

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable

@Immutable
data class AdaptiveNavBarScreenData(val menuItem: MenuItem, val component: @Composable () -> Unit)
