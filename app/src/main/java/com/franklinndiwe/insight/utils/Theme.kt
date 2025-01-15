package com.franklinndiwe.insight.utils

import androidx.annotation.StringRes
import com.franklinndiwe.insight.R

enum class Theme(@StringRes val text: Int) {
    Card(R.string.card), SolidColor(R.string.solid_color), Gradient(R.string.gradient), Image(R.string.image)
}