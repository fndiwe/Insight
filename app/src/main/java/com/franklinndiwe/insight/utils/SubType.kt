package com.franklinndiwe.insight.utils

enum class SubType(val sortOrderType: SortOrderType = SortOrderType.Quote) {
    QuotesForCategory, QuotesForAuthor, LikedQuotes, PersonalQuotes, FreeCategories(SortOrderType.Category), PremiumCategories(
        SortOrderType.Category
    ),
    LikedCategories(SortOrderType.Category), PersonalCategories(SortOrderType.Category), PopularCategories(
        SortOrderType.Category
    ),
    AllAuthors(SortOrderType.Author), LikedAuthors(SortOrderType.Author), PersonalAuthors(
        SortOrderType.Author
    ),
    PopularAuthors(
        SortOrderType.Author
    )
}