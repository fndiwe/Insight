package com.franklinndiwe.insight.viewmodel.author

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.franklinndiwe.insight.data.UserPreferencesRepository
import com.franklinndiwe.insight.repository.AuthorRepository
import com.franklinndiwe.insight.repository.SettingRepository
import com.franklinndiwe.insight.repository.SortOrderRepository
import com.franklinndiwe.insight.utils.AuthorScreenImpl
import com.franklinndiwe.insight.utils.SubType

class AllAuthorsViewModel(
    authorRepository: AuthorRepository,
    sortOrderRepository: SortOrderRepository,
    userPreferencesRepository: UserPreferencesRepository,
    settingRepository: SettingRepository,
) : ViewModel() {
    val authorScreenImpl = AuthorScreenImpl(
        SubType.AllAuthors,
        viewModelScope,
        authorRepository,
        sortOrderRepository,
        userPreferencesRepository,
        settingRepository
    )
}