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
import com.franklinndiwe.insight.viewmodel.category.LikedCategoriesViewModel

@Composable
fun LikedCategories(
    setting: Setting,
    onClickCategory: categoryUnit,
    onSearch: sortOrderTypeUnit,
    navigateBack: unit,
    likedCategoriesViewModel: LikedCategoriesViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val categoryScreenImpl = likedCategoriesViewModel.categoryScreenImpl
    val sortOrder by categoryScreenImpl.sortOrder.collectAsStateWithLifecycle()
    ReusableCategoryScreen(
        stringResource(id = R.string.liked),
        setting,
        sortOrder,
        categoryScreenImpl.categoryUtils.getLikedCategories(sortOrder),
        categoryScreenImpl,
        onClickCategory,
        onSearch,
        navigateBack
    )
}