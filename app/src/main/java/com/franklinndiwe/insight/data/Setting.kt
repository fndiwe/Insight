package com.franklinndiwe.insight.data

import android.os.Build
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.franklinndiwe.insight.utils.SwipeDirection
import com.franklinndiwe.insight.utils.Theme
import com.franklinndiwe.insight.utils.ThemeType
import kotlinx.serialization.Serializable

@Entity("setting")
@Serializable
data class Setting(
    @PrimaryKey val id: Int = 1,
    val themeType: ThemeType = ThemeType.SystemDefault,
    val dynamicColor: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
    val fontIndex: Int = 0,
    val contrastIndex: Int = 0,
    val dailyQuote: Int? = null,
    val dailyQuoteReminderEnabled: Boolean = true,
    val reminderTimeHour: Int = 8,
    val reminderTimeMinute: Int = 0,
    val dateOfLastQuote: String = "",
    val swipeDirection: SwipeDirection = SwipeDirection.Vertical,
    val theme: Theme = Theme.Image,
    val delay: Int = 15,
    val defaultShortQuoteTextSize: Int = 24,
    val defaultLongQuoteTextSize: Int = 17,
    val defaultAuthorTextSize: Int = 17,
)
