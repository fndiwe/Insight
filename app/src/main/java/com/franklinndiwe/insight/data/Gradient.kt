package com.franklinndiwe.insight.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    "gradients",
    foreignKeys = [
        ForeignKey(
            entity = AppFont::class,
            parentColumns = ["id"],
            childColumns = ["fontId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("fontId")]
)
data class Gradient(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val colors: List<Int> = emptyList(),
    @Embedded val attributes: BackgroundCoreAttributes = BackgroundCoreAttributes().copy(
        textColor = colors[0],
        tintColor = colors[colors.lastIndex]
    ),
)

data class GradientV2(
    @Embedded val gradient: Gradient,
    @Relation(
        parentColumn = "fontId", entityColumn = "id"
    )
    val font: AppFont?,
)