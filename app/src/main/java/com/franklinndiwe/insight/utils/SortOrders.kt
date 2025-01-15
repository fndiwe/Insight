package com.franklinndiwe.insight.utils

import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.SortOrder

object SortOrders {
    private const val Text = "text"
    private const val Name = "name"
    private const val Category = "categoryId1"
    private const val Author = "authorId"
    private const val Liked = "liked"
    private fun recentlyAdded(subType: SubType) =
        when {
            subType.name.contains("Liked") -> Liked
            subType.name.contains("Personal") -> UserGenerated
            else -> "id"
        }

    private const val UserGenerated = "userGenerated"
    private const val Descending = "DESC"
    private const val Ascending = "ASC"
    val list = SubType.entries.map {
        SortOrder(
            type = it.sortOrderType, subType = it, sortBy = recentlyAdded(it), order = Ascending
        )
    }

    fun getQuotesSortOrder(subType: SubType) = listOfNotNull(
        Pair(Text, R.string.alphabetically),
        Pair(Category, R.string.category),
        Pair(Author, R.string.author),
        if (subType.name.contains("Liked").not()) Pair(Liked, R.string.liked) else null,
        Pair(recentlyAdded(subType), R.string.recently_added),
        if (subType.name.contains("Personal").not()) Pair(
            UserGenerated,
            R.string.personal
        ) else null
    )

    fun getCategoriesSortOrder(subType: SubType) = listOfNotNull(
        Pair(Name, R.string.alphabetically),
        if (subType.name.contains("Liked").not() && subType.name.contains("Premium").not()) Pair(
            Liked,
            R.string.liked
        ) else null,
        Pair(recentlyAdded(subType), R.string.recently_added),
        if (subType.name.contains("Personal").not()) Pair(
            UserGenerated,
            R.string.personal
        ) else null
    )

    fun getAuthorsSortOrder(subType: SubType) = listOfNotNull(
        Pair(Name, R.string.alphabetically),
        if (subType.name.contains("Liked").not()) Pair(Liked, R.string.liked) else null,
        Pair(recentlyAdded(subType), R.string.recently_added),
        if (subType.name.contains("Personal").not()) Pair(
            UserGenerated,
            R.string.personal
        ) else null
    )

    val order = listOf(Pair(Descending, R.string.descending), Pair(Ascending, R.string.ascending))
}