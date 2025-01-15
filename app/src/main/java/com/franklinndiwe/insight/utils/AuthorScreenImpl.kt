package com.franklinndiwe.insight.utils

import com.franklinndiwe.insight.data.SortOrder
import com.franklinndiwe.insight.data.UserPreferencesRepository
import com.franklinndiwe.insight.repository.AuthorRepository
import com.franklinndiwe.insight.repository.SettingRepository
import com.franklinndiwe.insight.repository.SortOrderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthorScreenImpl(
    private val subType: SubType,
    private val viewModelScope: CoroutineScope,
    authorRepository: AuthorRepository,
    private val sortOrderRepository: SortOrderRepository,
    userPreferencesRepository: UserPreferencesRepository,
    settingRepository: SettingRepository,
) {
    private val dispatcher = Dispatchers.IO
    val sortOrder = sortOrderRepository.getSortOrder(subType).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SortOrders.list.find { it.subType == subType }!!
    )
    val authorUtils = AuthorUtils(viewModelScope, authorRepository)
    val quotaUtils = QuotaUtils(viewModelScope, userPreferencesRepository)
    val settingUtils = SettingUtils(viewModelScope, settingRepository)
    fun changeSortOrder(sortOrder: SortOrder) =
        viewModelScope.launch(dispatcher) { sortOrderRepository.update(sortOrder) }
}