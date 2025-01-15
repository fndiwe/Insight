package com.franklinndiwe.insight.ui.screens.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.paging.PagingData
import com.franklinndiwe.insight.data.Category
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.data.SortOrder
import com.franklinndiwe.insight.ui.screens.ReusableViewScreen
import com.franklinndiwe.insight.utils.CategoryScreenImpl
import com.franklinndiwe.insight.utils.categoryUnit
import com.franklinndiwe.insight.utils.sortOrderTypeUnit
import com.franklinndiwe.insight.utils.unit
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReusableCategoryScreen(
    title: String,
    setting: Setting,
    sortOrder: SortOrder,
    categories: Flow<PagingData<Category>>,
    categoryScreenImpl: CategoryScreenImpl,
    onClickCategory: categoryUnit,
    onSearch: sortOrderTypeUnit,
    navigateBack: unit,
) {
    val categoryUtils = categoryScreenImpl.categoryUtils
    ReusableViewScreen(
        name = title,
        categories = remember(sortOrder) { categories },
        quotaUtils = categoryScreenImpl.quotaUtils,
        setting = setting,
        sortOrder = sortOrder,
        changeSortOrder = { categoryScreenImpl.changeSortOrder(it) },
        categoryUtils = categoryUtils,
        onClickCategory = onClickCategory,
        navigateBack = navigateBack,
        onSearch = onSearch
    ) { categoryScreenImpl.settingUtils.updateSetting(it) }
}