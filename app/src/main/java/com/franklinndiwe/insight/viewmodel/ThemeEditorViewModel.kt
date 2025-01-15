package com.franklinndiwe.insight.viewmodel

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.franklinndiwe.insight.data.UserPreferencesRepository
import com.franklinndiwe.insight.repository.ColorRepository
import com.franklinndiwe.insight.repository.FontRepository
import com.franklinndiwe.insight.repository.GradientRepository
import com.franklinndiwe.insight.repository.QuoteImageRepository
import com.franklinndiwe.insight.repository.SettingRepository
import com.franklinndiwe.insight.utils.AppFontUtils
import com.franklinndiwe.insight.utils.GradientUtils
import com.franklinndiwe.insight.utils.QuotaUtils
import com.franklinndiwe.insight.utils.QuoteImageUtils
import com.franklinndiwe.insight.utils.SettingUtils
import com.franklinndiwe.insight.utils.SolidColorUtils

class ThemeEditorViewModel(
    colorRepository: ColorRepository,
    gradientRepository: GradientRepository,
    imageRepository: QuoteImageRepository,
    settingRepository: SettingRepository,
    fontRepository: FontRepository,
    userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    val colors = List<Int?>(4) { null }.toMutableStateList()
    val colorUtils = SolidColorUtils(viewModelScope, colorRepository)
    val gradientUtils = GradientUtils(viewModelScope, gradientRepository)
    val imageUtils = QuoteImageUtils(viewModelScope, imageRepository)
    val settingUtils = SettingUtils(viewModelScope, settingRepository)
    val fontUtils = AppFontUtils(viewModelScope, fontRepository)
    val quotaUtils = QuotaUtils(viewModelScope, userPreferencesRepository)
}