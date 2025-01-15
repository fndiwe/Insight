package com.franklinndiwe.insight

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.ui.screens.AppSplashScreen
import com.franklinndiwe.insight.ui.theme.AppTheme
import com.franklinndiwe.insight.utils.AppUtils
import com.franklinndiwe.insight.utils.ThemeType
import com.google.android.gms.ads.MobileAds
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        setContent {
            val application = (this@MainActivity.application as InsightApplication)
            val container = application.container
            val firstInstall by application.userPreferencesRepository.firstInstall.collectAsState(
                initial = null
            )
            val setting = remember {
                try {
                    container.settingRepository.getSetting()
                } catch (_: NullPointerException) {
                    null
                }
            }
            val settingState = setting?.collectAsStateWithLifecycle(
                initialValue = null
            )?.value
            var settings by rememberSaveable(stateSaver = Saver(save = {
                Json.encodeToString(it)
            }, restore = {
                Json.decodeFromString<Setting>(it)
            })) {
                mutableStateOf(settingState ?: Setting())
            }
            LaunchedEffect(key1 = settingState) {
                if (settingState != null) {
                    settings = settingState
                }
            }
            val themeType = settings.themeType
            var showLandingScreen by rememberSaveable { mutableStateOf(true) }
            val darkTheme =
                if (themeType == ThemeType.SystemDefault) isSystemInDarkTheme() else AppUtils.isDarkTheme(
                    themeType
                )

            AppTheme(
                darkTheme, settings.dynamicColor, settings.fontIndex, settings.contrastIndex
            ) {
                val defaultScrim = MaterialTheme.colorScheme.surface.toArgb()

                if (darkTheme) enableEdgeToEdge(
                    SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { true },
                    SystemBarStyle.dark(Color.TRANSPARENT)
                ) else enableEdgeToEdge(
                    SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { false },
                    SystemBarStyle.light(defaultScrim, defaultScrim)
                )
                Crossfade(targetState = showLandingScreen, label = "") { showSplashScreen ->
                    if (showSplashScreen) {
                        firstInstall?.let {
                            AppSplashScreen(it, { showLandingScreen = false })
                        }
                    } else AppNavHost(
                        this@MainActivity, settings
                    )
                }
            }
        }
    }
}