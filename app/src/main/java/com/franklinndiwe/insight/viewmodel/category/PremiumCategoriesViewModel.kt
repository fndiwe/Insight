package com.franklinndiwe.insight.viewmodel.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.franklinndiwe.insight.data.UserPreferencesRepository
import com.franklinndiwe.insight.repository.CategoryRepository
import com.franklinndiwe.insight.repository.SettingRepository
import com.franklinndiwe.insight.repository.SortOrderRepository
import com.franklinndiwe.insight.utils.CategoryScreenImpl
import com.franklinndiwe.insight.utils.SubType

class PremiumCategoriesViewModel(
    categoryRepository: CategoryRepository,
    sortOrderRepository: SortOrderRepository,
    userPreferencesRepository: UserPreferencesRepository,
    settingRepository: SettingRepository,
) : ViewModel() {
    val categoryScreenImpl = CategoryScreenImpl(
        SubType.PremiumCategories,
        viewModelScope,
        categoryRepository,
        sortOrderRepository,
        userPreferencesRepository,
        settingRepository
    )
}