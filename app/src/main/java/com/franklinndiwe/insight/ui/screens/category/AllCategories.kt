package com.franklinndiwe.insight.ui.screens.category

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.utils.categoryUnit
import com.franklinndiwe.insight.utils.sortOrderTypeUnit
import com.franklinndiwe.insight.utils.unit
import com.franklinndiwe.insight.viewmodel.AppViewModelProvider
import com.franklinndiwe.insight.viewmodel.category.FreeCategoriesViewModel

@Composable
fun AllCategories(
    setting: Setting,
    onClickCategory: categoryUnit,
    onSearch: sortOrderTypeUnit,
    navigateBack: unit,
    freeCategoriesViewModel: FreeCategoriesViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val categoryScreenImpl = freeCategoriesViewModel.categoryScreenImpl
    val sortOrder by categoryScreenImpl.sortOrder.collectAsStateWithLifecycle()
    ReusableCategoryScreen(
        stringResource(id = R.string.all),
        setting,
        sortOrder,
        categoryScreenImpl.categoryUtils.getFreeCategories(sortOrder),
        categoryScreenImpl,
        onClickCategory,
        onSearch,
        navigateBack
    )
}