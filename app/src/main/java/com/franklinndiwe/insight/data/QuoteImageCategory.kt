package com.franklinndiwe.insight.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("image_categories")
data class QuoteImageCategory(
    @PrimaryKey val name: String,
    val included: Boolean = true,
    val shipped: Boolean = true,
)