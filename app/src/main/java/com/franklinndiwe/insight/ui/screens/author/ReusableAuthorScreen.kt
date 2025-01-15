package com.franklinndiwe.insight.ui.screens.author

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.paging.PagingData
import com.franklinndiwe.insight.data.Author
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.data.SortOrder
import com.franklinndiwe.insight.ui.screens.ReusableViewScreen
import com.franklinndiwe.insight.utils.AuthorScreenImpl
import com.franklinndiwe.insight.utils.authorUnit
import com.franklinndiwe.insight.utils.sortOrderTypeUnit
import com.franklinndiwe.insight.utils.unit
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ReusableAuthorScreen(
    title: String,
    authors: Flow<PagingData<Author>>,
    setting: Setting,
    sortOrder: SortOrder,
    authorScreenImpl: AuthorScreenImpl,
    onSearch: sortOrderTypeUnit,
    onClickAuthor: authorUnit,
    navigateBack: unit,
) {
    ReusableViewScreen(
        name = title,
        authors = remember(sortOrder) { authors },
        quotaUtils = authorScreenImpl.quotaUtils,
        setting = setting,
        sortOrder = sortOrder,
        changeSortOrder = { authorScreenImpl.changeSortOrder(it) },
        authorUtils = authorScreenImpl.authorUtils,
        onClickAuthor = onClickAuthor,
        navigateBack = navigateBack,
        onSearch = onSearch
    ) { authorScreenImpl.settingUtils.updateSetting(it) }
}