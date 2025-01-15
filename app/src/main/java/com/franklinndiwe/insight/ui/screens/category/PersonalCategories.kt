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
import com.franklinndiwe.insight.viewmodel.category.PersonalCategoriesViewModel

@Composable
fun PersonalCategories(
    setting: Setting,
    onClickCategory: categoryUnit,
    onSearch: sortOrderTypeUnit,
    navigateBack: unit,
    personalCategoriesViewModel: PersonalCategoriesViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val categoryScreenImpl = personalCategoriesViewModel.categoryScreenImpl
    val sortOrder by categoryScreenImpl.sortOrder.collectAsStateWithLifecycle()
    ReusableCategoryScreen(
        stringResource(id = R.string.personal),
        setting,
        sortOrder,
        categoryScreenImpl.categoryUtils.getPersonalCategories(sortOrder),
        categoryScreenImpl,
        onClickCategory,
        onSearch,
        navigateBack
    )
}