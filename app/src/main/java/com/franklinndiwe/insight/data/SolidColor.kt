package com.franklinndiwe.insight.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    "colors", foreignKeys = [ForeignKey(
        entity = AppFont::class,
        parentColumns = ["id"],
        childColumns = ["fontId"],
        onDelete = ForeignKey.SET_NULL
    )], indices = [Index("fontId")]
)
data class SolidColor(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val background: Int,
    @Embedded val attributes: BackgroundCoreAttributes = BackgroundCoreAttributes(tintColor = background),
)

data class SolidColorV2(
    @Embedded val color: SolidColor,
    @Relation(
        parentColumn = "fontId", entityColumn = "id"
    ) val font: AppFont?,
)