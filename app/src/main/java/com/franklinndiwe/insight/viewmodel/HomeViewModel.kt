package com.franklinndiwe.insight.viewmodel

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.franklinndiwe.insight.data.QuoteV2
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.data.UserPreferencesRepository
import com.franklinndiwe.insight.repository.QuoteRepository
import com.franklinndiwe.insight.repository.SettingRepository
import com.franklinndiwe.insight.utils.AppUtils
import com.franklinndiwe.insight.utils.DailyQuoteImpl
import com.franklinndiwe.insight.utils.DailyQuoteNotification
import com.franklinndiwe.insight.utils.QuotaUtils
import com.franklinndiwe.insight.utils.QuoteUtils
import com.franklinndiwe.insight.utils.SettingUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    quoteRepository: QuoteRepository,
    private val settingRepository: SettingRepository,
    userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    val quotaUtils = QuotaUtils(viewModelScope, userPreferencesRepository)
    val quoteUtils = QuoteUtils(viewModelScope, quoteRepository)
    private val dailyQuoteImpl = DailyQuoteImpl(quoteRepository, settingRepository)
    private val _dailyQuote = MutableStateFlow<QuoteV2?>(null)
    val dailyQuote = _dailyQuote.asStateFlow()

    fun updateDailyQuoteReminder(context: Context, setting: Setting, enable: Boolean) {
        viewModelScope.launch {
            AppUtils.updateDailyQuoteReminder(
                enable, settingRepository, DailyQuoteNotification(context, setting), setting
            )
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    fun runDailyQuoteReminderCheck(
        context: Context,
        enable: Boolean = true,
        setting: Setting,
        permissionsState: PermissionState,
        snackbarHostState: SnackbarHostState,
    ) = viewModelScope.async(Dispatchers.IO) {
        AppUtils.runDailyQuoteReminderCheck(
            context,
            enable,
            setting,
            settingRepository,
            permissionsState,
            snackbarHostState,
            DailyQuoteNotification(context, setting)
        )
    }

    fun generateDailyQuote() = viewModelScope.async {
        _dailyQuote.update { async { dailyQuoteImpl.generateQuoteOfTheDay() }.await() }
        return@async _dailyQuote.value != null
    }

    fun updateQuote(quoteV2: QuoteV2) = _dailyQuote.update { quoteV2 }

    val settingUtils = SettingUtils(viewModelScope, settingRepository)
}