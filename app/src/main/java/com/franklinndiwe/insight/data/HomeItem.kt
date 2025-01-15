package com.franklinndiwe.insight.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
data class HomeItem(@DrawableRes val icon: Int, @StringRes val name: Int, val route: String)
