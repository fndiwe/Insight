package com.franklinndiwe.insight.viewmodel

import android.content.Context
import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.repository.CategoryRepository
import com.franklinndiwe.insight.repository.QuoteImageCategoryRepository
import com.franklinndiwe.insight.repository.SettingRepository
import com.franklinndiwe.insight.utils.AppUtils
import com.franklinndiwe.insight.utils.CategoryUtils
import com.franklinndiwe.insight.utils.DailyQuoteNotification
import com.franklinndiwe.insight.utils.QuoteImageCategoryUtils
import com.franklinndiwe.insight.utils.SettingUtils
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SettingsViewModel(
    categoryRepository: CategoryRepository,
    quoteImageCategoryRepository: QuoteImageCategoryRepository,
    private val settingRepository: SettingRepository,
) : ViewModel() {
    val settingUtils = SettingUtils(viewModelScope, settingRepository)
    val categoryUtils = CategoryUtils(viewModelScope, categoryRepository)
    val quoteImageCategoryRepositoryUtils =
        QuoteImageCategoryUtils(viewModelScope, quoteImageCategoryRepository)

    fun rescheduleNotification(context: Context, setting: Setting) =
        DailyQuoteNotification(context, setting).scheduleNotification()

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
}