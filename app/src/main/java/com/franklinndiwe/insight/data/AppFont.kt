package com.franklinndiwe.insight.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("fonts")
data class AppFont(
    @PrimaryKey(true) val id: Int = 0,
    val name: String,
    val path: String,
    val textSizeForShortQuote: Int = 24,
    val textSizeForLongQuote: Int = textSizeForShortQuote - 7,
    val authorTextSize: Int = textSizeForLongQuote,
    val unlocked: Boolean = false,
    val shipped: Boolean = true,
)

