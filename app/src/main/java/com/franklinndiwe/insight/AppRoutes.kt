package com.franklinndiwe.insight

enum class AppRoutes(val arg: String = "", val arg2: String = "") {
    Home, Follow("follow_screen_option"), Search("sort_order_type"), CreateQuote, FollowingQuotes, LikedQuotes, PersonalQuotes, CategoryQuotes(
        "category_id"
    ),
    AuthorQuotes("author_id"), Themes, ImageThemes, GradientThemes, SolidColorThemes, Fonts, EditTheme(
        arg = "theme_id", arg2 = "theme"
    ),
    PopularCategories, LikedCategories, PersonalCategories, FreeCategories, PremiumCategories, PopularAuthors, LikedAuthors, PersonalAuthors, AllAuthors, Settings,
}