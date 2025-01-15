package com.franklinndiwe.insight.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.BackgroundCoreAttributes
import com.franklinndiwe.insight.data.QuoteImage
import com.franklinndiwe.insight.data.QuoteImageCategory
import com.franklinndiwe.insight.data.UserPreferencesRepository
import com.franklinndiwe.insight.repository.ColorRepository
import com.franklinndiwe.insight.repository.FontRepository
import com.franklinndiwe.insight.repository.GradientRepository
import com.franklinndiwe.insight.repository.QuoteImageCategoryRepository
import com.franklinndiwe.insight.repository.QuoteImageRepository
import com.franklinndiwe.insight.ui.states.ThemeScreenUIState
import com.franklinndiwe.insight.utils.AppFontUtils
import com.franklinndiwe.insight.utils.FontUtils.getFileFromUri
import com.franklinndiwe.insight.utils.FontUtils.getFontFromPhone
import com.franklinndiwe.insight.utils.GradientUtils
import com.franklinndiwe.insight.utils.QuotaUtils
import com.franklinndiwe.insight.utils.QuoteImageCategoryUtils
import com.franklinndiwe.insight.utils.QuoteImageUtils
import com.franklinndiwe.insight.utils.SolidColorUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ThemeScreenViewModel(
    fontRepository: FontRepository,
    colorRepository: ColorRepository,
    gradientRepository: GradientRepository,
    quoteImageRepository: QuoteImageRepository,
    quoteImageCategoryRepository: QuoteImageCategoryRepository,
    userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    private val _themeScreenUIState = MutableStateFlow(ThemeScreenUIState())
    val themeScreenUIState = _themeScreenUIState.asStateFlow()
    val quoteImageUtils = QuoteImageUtils(viewModelScope, quoteImageRepository)
    val gradientUtils = GradientUtils(viewModelScope, gradientRepository)
    val colorUtils = SolidColorUtils(viewModelScope, colorRepository)
    val fontUtils = AppFontUtils(viewModelScope, fontRepository)
    private val quoteImageCategoryUtils =
        QuoteImageCategoryUtils(viewModelScope, quoteImageCategoryRepository)
    private val imageCategories = quoteImageCategoryUtils.quoteImageCategories
    val images = imageCategories.map { quoteImageCategories ->
        quoteImageCategories.map {
            Pair(
                it, quoteImageUtils.getImagesForCategory(it.name)
            )
        }
    }
    val quotaUtils = QuotaUtils(viewModelScope, userPreferencesRepository)
    fun processImage(
        context: Context,
        quoteImageCategory: QuoteImageCategory,
        uri: Uri,
        snackbarHostState: SnackbarHostState,
    ) {
        quoteImageUtils.insertQuoteImage(
            QuoteImage(
                path = getFileFromUri(context, uri).path,
                categoryName = quoteImageCategory.name,
                attributes = BackgroundCoreAttributes(unlocked = true, shipped = false)
            )
        )
        viewModelScope.launch { snackbarHostState.showSnackbar(context.getString(R.string.image_imported_successfully)) }
    }

    fun processFont(
        context: Context,
        uri: Uri,
        snackbarHostState: SnackbarHostState,
    ) {
        getFontFromPhone(context, uri)?.let {
            fontUtils.insertFont(
                it
            )
            viewModelScope.launch { snackbarHostState.showSnackbar(context.getString(R.string.font_imported_successfully)) }
        }
    }

    fun updateUIState(uiState: ThemeScreenUIState) = _themeScreenUIState.update { uiState }
}