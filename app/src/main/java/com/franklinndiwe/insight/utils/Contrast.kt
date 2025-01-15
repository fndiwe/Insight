package com.franklinndiwe.insight.utils

import androidx.annotation.StringRes
import com.franklinndiwe.insight.R

enum class Contrast(@StringRes val text: Int) {
    Low(R.string.low), Medium(R.string.medium), High(R.string.high)
}