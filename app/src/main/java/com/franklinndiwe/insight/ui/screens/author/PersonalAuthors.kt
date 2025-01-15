package com.franklinndiwe.insight.ui.screens.author

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.utils.authorUnit
import com.franklinndiwe.insight.utils.sortOrderTypeUnit
import com.franklinndiwe.insight.utils.unit
import com.franklinndiwe.insight.viewmodel.AppViewModelProvider
import com.franklinndiwe.insight.viewmodel.author.PersonalAuthorsViewModel

@Composable
fun PersonalAuthors(
    setting: Setting,
    onSearch: sortOrderTypeUnit,
    onClickAuthor: authorUnit,
    navigateBack: unit,
    personalAuthorsViewModel: PersonalAuthorsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val authorScreenImpl = personalAuthorsViewModel.authorScreenImpl
    val sortOrder by authorScreenImpl.sortOrder.collectAsStateWithLifecycle()
    ReusableAuthorScreen(
        title = stringResource(id = R.string.personal),
        authors = authorScreenImpl.authorUtils.getPersonalAuthors(sortOrder),
        setting = setting,
        sortOrder = sortOrder,
        authorScreenImpl = authorScreenImpl,
        onSearch = onSearch,
        onClickAuthor = onClickAuthor,
        navigateBack = navigateBack
    )
}