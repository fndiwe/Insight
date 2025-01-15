package com.franklinndiwe.insight.utils

import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.repository.SettingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingUtils(
    private val viewModelScope: CoroutineScope,
    private val settingRepository: SettingRepository,
) {
    fun updateSetting(setting: Setting) =
        viewModelScope.launch(Dispatchers.IO) { settingRepository.update(setting) }
}