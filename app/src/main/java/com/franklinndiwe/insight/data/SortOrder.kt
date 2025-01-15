package com.franklinndiwe.insight.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.franklinndiwe.insight.utils.SortOrderType
import com.franklinndiwe.insight.utils.SubType

@Entity("sort_order")
data class SortOrder(
    @PrimaryKey(true) val id: Int = 0,
    val type: SortOrderType,
    val subType: SubType,
    val sortBy: String,
    val order: String,
)
