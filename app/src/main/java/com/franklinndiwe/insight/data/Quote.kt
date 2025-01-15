package com.franklinndiwe.insight.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Fts4
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.serialization.Serializable

@Serializable
data class JsonQuote(
    val id: Int,
    val text: String,
    val authorId: Int,
    val categoryId1: Int,
    val categoryId2: Int?,
)

@Serializable
data class JsonAuthor(
    val id: Int,
    val name: String,
    val popular: Int,
    val following: Int,
)

@Serializable
data class JsonCategory(
    val id: Int,
    val name: String,
    val unlocked: Int,
    val popular: Int,
    val following: Int,
    val daily: Int,
)

@Entity(
    tableName = "quotes",
    foreignKeys = [
        ForeignKey(
            entity = Author::class,
            parentColumns = ["id"],
            childColumns = ["authorId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId1"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId2"],
            onDelete = ForeignKey.SET_NULL
        ),
    ],
    indices = [Index("authorId"), Index("categoryId1"), Index("categoryId2")]
)
//liked and userGenerated uses the current time in millis for sorting purposes.
@Serializable
data class Quote(
    @PrimaryKey(true) val id: Int = 0,
    val text: String,
    val authorId: Int,
    val categoryId1: Int,
    val categoryId2: Int? = null,
    val liked: Long? = null,
    val userGenerated: Long? = null,
)

@Entity("quote_fts")
@Fts4(contentEntity = Quote::class)
data class QuoteFts(
    val text: String,
)

data class QuoteV2(
    @Embedded val quote: Quote,
    @Relation(
        parentColumn = "categoryId1", entityColumn = "id",
    ) val category1: Category,
    @Relation(
        parentColumn = "categoryId2", entityColumn = "id",
    ) val category2: Category?,
    @Relation(
        parentColumn = "authorId", entityColumn = "id"
    ) val author: Author,
)

@Entity(tableName = "categories")
@Serializable
data class Category(
    @PrimaryKey(true) val id: Int = 0,
    val name: String,
    val liked: Long? = null,
    val unlocked: Boolean,
    val popular: Boolean,
    val userGenerated: Long? = null,
    val following: Boolean = false,
    val daily: Boolean = false,
)

data class Suggestion(
    val id: Int,
    val name: String,
)

@Entity("category_fts")
@Fts4(contentEntity = Category::class)
data class CategoryFts(
    val name: String,
)

@Entity(tableName = "authors")
@Serializable
data class Author(
    @PrimaryKey(true) val id: Int = 0,
    val name: String,
    val liked: Long? = null,
    val popular: Boolean,
    val following: Boolean = false,
    val userGenerated: Long? = null,
)

@Entity("author_fts")
@Fts4(contentEntity = Author::class)
data class AuthorFts(
    val name: String,
)
