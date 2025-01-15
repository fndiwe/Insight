package com.franklinndiwe.insight.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    "quote_images",
    foreignKeys = [
        ForeignKey(
            entity = QuoteImageCategory::class,
            parentColumns = ["name"],
            childColumns = ["categoryName"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AppFont::class,
            parentColumns = ["id"],
            childColumns = ["fontId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("categoryName"), Index("fontId")]
)
data class QuoteImage(
    @PrimaryKey(true) val id: Int = 0,
    val path: String,
    val categoryName: String,
    @Embedded val attributes: BackgroundCoreAttributes = BackgroundCoreAttributes(),
)

data class QuoteImageV2(
    @Embedded val quoteImage: QuoteImage,
    @Relation(
        parentColumn = "fontId", entityColumn = "id"
    )
    val font: AppFont?,
)