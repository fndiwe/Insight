package com.franklinndiwe.insight.utils

import android.content.Context
import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.Author
import com.franklinndiwe.insight.data.Category
import com.franklinndiwe.insight.data.QuoteV2
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.repository.SettingRepository
import com.franklinndiwe.insight.ui.theme.GolosText
import com.franklinndiwe.insight.ui.theme.MerriweatherSans
import com.franklinndiwe.insight.ui.theme.Montserrat
import com.franklinndiwe.insight.ui.theme.NotoSans
import com.franklinndiwe.insight.ui.theme.OpenSans
import com.franklinndiwe.insight.ui.theme.darkScheme
import com.franklinndiwe.insight.ui.theme.highContrastDarkColorScheme
import com.franklinndiwe.insight.ui.theme.highContrastLightColorScheme
import com.franklinndiwe.insight.ui.theme.lightScheme
import com.franklinndiwe.insight.ui.theme.mediumContrastDarkColorScheme
import com.franklinndiwe.insight.ui.theme.mediumContrastLightColorScheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

typealias unit = () -> Unit
typealias unit2 = (() -> Unit)
typealias categoryUnit = (Category) -> Unit
typealias authorUnit = (Author) -> Unit
typealias sortOrderTypeUnit = (SortOrderType) -> Unit

object AppUtils {
    val cardSize = DpSize(width = 170.dp, height = 190.dp)
    const val LargeQuoteSize = 80
    val listOfFonts = listOf(
        Pair(R.string.montserrat, Montserrat),
        Pair(R.string.merriweather_sans, MerriweatherSans),
        Pair(R.string.golos_text, GolosText),
        Pair(R.string.noto_sans, NotoSans),
        Pair(R.string.open_sans, OpenSans)
    )
    val listOfContrast = listOf(
        Triple(Contrast.Low, lightScheme, darkScheme),
        Triple(Contrast.Medium, mediumContrastLightColorScheme, mediumContrastDarkColorScheme),
        Triple(Contrast.High, highContrastLightColorScheme, highContrastDarkColorScheme)
    )

    fun isDarkTheme(themeType: ThemeType): Boolean {
        return when (themeType) {
            ThemeType.DarkTheme -> true
            else -> false
        }
    }

    val defaultShape @Composable get() = MaterialTheme.shapes.large

    fun annotatedString(quote: QuoteV2): AnnotatedString {
        val quoteText = quote.quote.text
        val quoteAuthor = "${Typography.mdash} ${quote.author.name}"
        val quoteLength = quoteText.length
        return buildAnnotatedString {
            append(quoteText)
            addStyle(
                ParagraphStyle(textAlign = if (quoteText.length <= LargeQuoteSize) TextAlign.Center else TextAlign.Start),
                start = 0,
                end = quoteLength
            )
            appendLine()
            append(quoteAuthor)
        }
    }

    suspend fun updateDailyQuoteReminder(
        enable: Boolean,
        settingRepository: SettingRepository,
        dailyQuoteNotification: DailyQuoteNotification,
        setting: Setting,
    ) = coroutineScope {
        val dailyQuoteReminderEnabled = setting.dailyQuoteReminderEnabled
        // If user intends to turn on daily quote reminder
        if (enable) {
            // Check if daily quote reminder is off previously
            if (dailyQuoteReminderEnabled.not()) async {
                // Then turn it on
                settingRepository.update(
                    setting.copy(
                        dailyQuoteReminderEnabled = true
                    )
                )
            }.await()
            // And if next notification is not scheduled, scheduled it.
            if (dailyQuoteNotification.isScheduled.not()) dailyQuoteNotification.scheduleNotification()
        }
        // If user intends to to turn off daily quote reminder
        else {
            // Check if it has been enabled previously
            if (dailyQuoteReminderEnabled) async {
                // Then turn it off
                settingRepository.update(
                    setting.copy(
                        dailyQuoteReminderEnabled = false
                    )
                )
            }.await()
            // If there is any scheduled notification, cancel it
            if (dailyQuoteNotification.isScheduled) dailyQuoteNotification.cancelSchedule()
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    suspend fun runDailyQuoteReminderCheck(
        context: Context,
        enable: Boolean = true,
        setting: Setting,
        settingRepository: SettingRepository,
        permissionsState: PermissionState,
        snackbarHostState: SnackbarHostState,
        dailyQuoteNotification: DailyQuoteNotification,
    ) = withContext(Dispatchers.IO) {
        suspend fun updateDailyQuoteReminder(value: Boolean = enable) {
            updateDailyQuoteReminder(
                value, settingRepository, dailyQuoteNotification, setting
            )
        }
        // If the user intends to turn on notifications or if the LaunchedEffect wants to initialize notifications
        if (enable) {
            // First check if it's Android 13 and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Then check if the user has initially granted the permissions
                if (permissionsState.status.isGranted) {
                    // Turn on daily quote reminder and schedule notification
                    async { updateDailyQuoteReminder(true) }.await()
                }
                // If we can ask the user the grant the permission to post notification
                else if (permissionsState.status.shouldShowRationale) {
                    val result = snackbarHostState.showSnackbar(
                        context.getString(R.string.post_notification_access_note),
                        actionLabel = context.getString(
                            R.string.grant_access
                        )
                    )
                    // If the user clicks on the snackbar action for granting access
                    if (result == SnackbarResult.ActionPerformed) {
                        // Ask for the permission
                        permissionsState.launchPermissionRequest()
                    }
                } else permissionsState.launchPermissionRequest()
            }
            // If it's other Android versions below 13 just enable
            else {
                async { updateDailyQuoteReminder() }.await()
            }
        }
        // If the user intends to turn daily quote reminder off
        else async { updateDailyQuoteReminder() }.await()
    }


    fun likedQuote(quoteV2: QuoteV2): QuoteV2 {
        val liked = quoteV2.quote.liked
        return quoteV2.copy(quote = quoteV2.quote.copy(liked = if (liked == null) System.currentTimeMillis() else null))
    }

    fun categoryQuota(noOfQuotes: Int) = (noOfQuotes * 0.1).roundToInt()
    val tonalElevation = 1.dp
    val shadowElevation = 1.dp
    val dimColor = Color.Black.copy(.2f)
    const val ImportQuota = 10
    const val CreateQuota = 5
    const val DownloadQuoteQuota = 3
}