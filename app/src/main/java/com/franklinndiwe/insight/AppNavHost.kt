package com.franklinndiwe.insight

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.ui.screens.FollowScreen
import com.franklinndiwe.insight.ui.screens.HomeScreen
import com.franklinndiwe.insight.ui.screens.SearchScreen
import com.franklinndiwe.insight.ui.screens.SettingsScreen
import com.franklinndiwe.insight.ui.screens.author.AllAuthors
import com.franklinndiwe.insight.ui.screens.author.LikedAuthors
import com.franklinndiwe.insight.ui.screens.author.PersonalAuthors
import com.franklinndiwe.insight.ui.screens.author.PopularAuthors
import com.franklinndiwe.insight.ui.screens.category.AllCategories
import com.franklinndiwe.insight.ui.screens.category.LikedCategories
import com.franklinndiwe.insight.ui.screens.category.PersonalCategories
import com.franklinndiwe.insight.ui.screens.category.PopularCategories
import com.franklinndiwe.insight.ui.screens.category.PremiumCategories
import com.franklinndiwe.insight.ui.screens.quote.AuthorQuotesScreen
import com.franklinndiwe.insight.ui.screens.quote.CategoryQuotesScreen
import com.franklinndiwe.insight.ui.screens.quote.CreateQuoteScreen
import com.franklinndiwe.insight.ui.screens.quote.ForYouQuotesScreen
import com.franklinndiwe.insight.ui.screens.quote.LikedQuotesScreen
import com.franklinndiwe.insight.ui.screens.quote.PersonalQuotesScreen
import com.franklinndiwe.insight.ui.screens.theme.ThemeEditor
import com.franklinndiwe.insight.ui.screens.theme.ThemesScreen
import com.franklinndiwe.insight.utils.FollowScreenOption
import com.franklinndiwe.insight.utils.SortOrderType
import com.franklinndiwe.insight.utils.Theme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppNavHost(
    context: Context,
    setting: Setting,
) {
    val writeStorageAccessState = rememberMultiplePermissionsState(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) emptyList() else listOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    )
    val navController = rememberNavController()
    NavHost(
        navController = navController, startDestination = AppRoutes.Home.name
    ) {
        composable(AppRoutes.Home.name) {
            HomeScreen(context,
                setting,
                { navController.navigateSingleTopTo(AppRoutes.Settings.name) },
                {
                    navController.navigateWithStringArg(AppRoutes.Follow.name, it.name)
                },
                {
                    navController.navigateSingleTopTo(it)
                },
                {
                    navController.navigateSingleTopTo(AppRoutes.CreateQuote.name)
                },
                {
                    navController.navigateWithStringArg(AppRoutes.Search.name, it)
                })
        }
        composable(
            "${AppRoutes.Follow.name}/{${AppRoutes.Follow.arg}}",
            listOf(navArgument(AppRoutes.Follow.arg) { type = NavType.StringType })
        ) {
            it.arguments?.let { it1 ->
                val followScreenOption = it1.getString(AppRoutes.Follow.arg)
                    ?.let { it2 -> FollowScreenOption.valueOf(it2) } ?: FollowScreenOption.IS_HOME
                FollowScreen(followScreenOption, onNavigateBack = {
                    when (followScreenOption) {
                        FollowScreenOption.IS_HOME -> {}
                        else -> navController.navigateUp()
                    }
                }) {
                    when (followScreenOption) {
                        FollowScreenOption.IS_FOR_YOU -> {
                            navController.navigateUp()
                        }

                        else -> navController.navigateUp()
                    }
                }
            }
        }
        composable(AppRoutes.Settings.name) {
            SettingsScreen(context, setting, {
                navController.navigateWithStringArg(
                    AppRoutes.Follow.name, it.name
                )
            }, { navController.navigateUp() })
        }
        composable(AppRoutes.CreateQuote.name) {
            CreateQuoteScreen(context, navigateBack = { navController.navigateUp() })
        }
        composable(
            "${AppRoutes.Search.name}/{${AppRoutes.Search.arg}}",
            listOf(navArgument(AppRoutes.Search.arg) { type = NavType.StringType })
        ) { backStackEntry ->
            val sortOrderType = backStackEntry.arguments?.getString(AppRoutes.Search.arg)
                ?.let { it1 -> SortOrderType.valueOf(it1) } ?: SortOrderType.Quote
            SearchScreen(context = context, sortOrderType = sortOrderType, onClickCategory = {
                navController.navigateWithIntArg(
                    AppRoutes.CategoryQuotes.name, it.id
                )
            }, onClickAuthor = {
                navController.navigateWithIntArg(
                    AppRoutes.AuthorQuotes.name, it.id
                )
            }, navigateBack = { navController.navigateUp() })
        }
        composable(AppRoutes.FollowingQuotes.name) {
            ForYouQuotesScreen(setting = setting,
                writeAccessState = writeStorageAccessState,
                onSearch = { navController.navigateWithStringArg(AppRoutes.Search.name, it.name) },
                onNavigateToForYouSettings = {
                    navController.navigateWithStringArg(
                        AppRoutes.Follow.name, it.name
                    )
                }) { navController.navigateUp() }
        }
        composable(
            "${AppRoutes.AuthorQuotes.name}/{${AppRoutes.AuthorQuotes.arg}}",
            listOf(navArgument(AppRoutes.AuthorQuotes.arg) { type = NavType.IntType })
        ) { backStackEntry ->
            val authorId = backStackEntry.arguments?.getInt(AppRoutes.AuthorQuotes.arg)
            if (authorId != null) {
                AuthorQuotesScreen(authorId = authorId,
                    setting = setting,
                    writeAccessState = writeStorageAccessState,
                    onSearch = {
                        navController.navigateWithStringArg(
                            AppRoutes.Search.name, it.name
                        )
                    },
                    onCreateQuote = { navController.navigateSingleTopTo(AppRoutes.CreateQuote.name) },
                    navigateBack = { navController.navigateUp() })
            }
        }
        composable(
            "${AppRoutes.CategoryQuotes.name}/{${AppRoutes.CategoryQuotes.arg}}",
            listOf(navArgument(AppRoutes.CategoryQuotes.arg) { type = NavType.IntType })
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt(AppRoutes.CategoryQuotes.arg)
            if (categoryId != null) {
                CategoryQuotesScreen(categoryId = categoryId,
                    setting = setting,
                    writeAccessState = writeStorageAccessState,
                    onSearch = {
                        navController.navigateWithStringArg(
                            AppRoutes.Search.name, it.name
                        )
                    },
                    onCreateQuote = { navController.navigateSingleTopTo(AppRoutes.CreateQuote.name) },
                    navigateBack = { navController.navigateUp() })
            }
        }
        composable(AppRoutes.LikedQuotes.name) {
            LikedQuotesScreen(setting = setting,
                writeAccessState = writeStorageAccessState,
                onSearch = { navController.navigateWithStringArg(AppRoutes.Search.name, it.name) },
                onCreateQuote = { navController.navigateSingleTopTo(AppRoutes.CreateQuote.name) }) {
                navController.navigateUp()
            }
        }
        composable(AppRoutes.PersonalQuotes.name) {
            PersonalQuotesScreen(setting = setting,
                writeAccessState = writeStorageAccessState,
                onSearch = { navController.navigateWithStringArg(AppRoutes.Search.name, it.name) },
                onCreateQuote = { navController.navigateSingleTopTo(AppRoutes.CreateQuote.name) }) {
                navController.navigateUp()
            }
        }
        composable(AppRoutes.FreeCategories.name) {
            AllCategories(setting = setting,
                onClickCategory = {
                    navController.navigateWithIntArg(
                        AppRoutes.CategoryQuotes.name, it.id
                    )
                },
                onSearch = { navController.navigateWithStringArg(AppRoutes.Search.name, it.name) },
                navigateBack = { navController.navigateUp() })
        }
        composable(AppRoutes.LikedCategories.name) {
            LikedCategories(setting = setting,
                onClickCategory = {
                    navController.navigateWithIntArg(
                        AppRoutes.CategoryQuotes.name, it.id
                    )
                },
                onSearch = { navController.navigateWithStringArg(AppRoutes.Search.name, it.name) },
                navigateBack = { navController.navigateUp() })
        }
        composable(AppRoutes.PersonalCategories.name) {
            PersonalCategories(setting = setting,
                onClickCategory = {
                    navController.navigateWithIntArg(
                        AppRoutes.CategoryQuotes.name, it.id
                    )
                },
                onSearch = { navController.navigateWithStringArg(AppRoutes.Search.name, it.name) },
                navigateBack = { navController.navigateUp() })
        }
        composable(AppRoutes.PopularCategories.name) {
            PopularCategories(setting = setting,
                onClickCategory = {
                    navController.navigateWithIntArg(
                        AppRoutes.CategoryQuotes.name, it.id
                    )
                },
                onSearch = { navController.navigateWithStringArg(AppRoutes.Search.name, it.name) },
                navigateBack = { navController.navigateUp() })
        }
        composable(AppRoutes.PremiumCategories.name) {
            PremiumCategories(setting = setting,
                onClickCategory = {
                    navController.navigateWithIntArg(
                        AppRoutes.CategoryQuotes.name, it.id
                    )
                },
                onSearch = { navController.navigateWithStringArg(AppRoutes.Search.name, it.name) },
                navigateBack = { navController.navigateUp() })
        }
        composable(AppRoutes.AllAuthors.name) {
            AllAuthors(setting = setting,
                onSearch = { navController.navigateWithStringArg(AppRoutes.Search.name, it.name) },
                onClickAuthor = {
                    navController.navigateWithIntArg(
                        AppRoutes.AuthorQuotes.name, it.id
                    )
                },
                navigateBack = { navController.navigateUp() })
        }
        composable(AppRoutes.LikedAuthors.name) {
            LikedAuthors(setting = setting,
                onSearch = { navController.navigateWithStringArg(AppRoutes.Search.name, it.name) },
                onClickAuthor = {
                    navController.navigateWithIntArg(
                        AppRoutes.AuthorQuotes.name, it.id
                    )
                },
                navigateBack = { navController.navigateUp() })
        }
        composable(AppRoutes.PersonalAuthors.name) {
            PersonalAuthors(setting = setting,
                onSearch = { navController.navigateWithStringArg(AppRoutes.Search.name, it.name) },
                onClickAuthor = {
                    navController.navigateWithIntArg(
                        AppRoutes.AuthorQuotes.name, it.id
                    )
                },
                navigateBack = { navController.navigateUp() })
        }
        composable(AppRoutes.PopularAuthors.name) {
            PopularAuthors(setting = setting,
                onSearch = { navController.navigateWithStringArg(AppRoutes.Search.name, it.name) },
                onClickAuthor = {
                    navController.navigateWithIntArg(
                        AppRoutes.AuthorQuotes.name, it.id
                    )
                },
                navigateBack = { navController.navigateUp() })
        }
        composable(AppRoutes.Themes.name) {
            ThemesScreen(context = context,
                navigateBack = { navController.navigateUp() },
                editTheme = { id, theme ->
                    navController.navigateWithTwoArgs(
                        AppRoutes.EditTheme.name, id, theme.ordinal
                    )
                },
                createTheme = {
                    navController.navigateWithTwoArgs(
                        AppRoutes.EditTheme.name, Int.MIN_VALUE, it.ordinal
                    )
                })
        }
        composable(
            "${AppRoutes.EditTheme.name}/{${AppRoutes.EditTheme.arg}}/{${AppRoutes.EditTheme.arg2}}",
            listOf(navArgument(AppRoutes.EditTheme.arg) {
                type = NavType.IntType
            }, navArgument(AppRoutes.EditTheme.arg2) { type = NavType.IntType })
        ) {
            val id = it.arguments?.getInt(AppRoutes.EditTheme.arg)
            val theme = Theme.entries[it.arguments?.getInt(AppRoutes.EditTheme.arg2) ?: 0]
            ThemeEditor(context, themeId = id ?: Int.MIN_VALUE,
                theme = theme,
                setting = setting,
                navigateBack = { navController.navigateUp() })
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) {
    this.navigate(route) { launchSingleTop = true }
}

fun NavHostController.navigateWithStringArg(route: String, arg: String) =
    this.navigateSingleTopTo("$route/$arg")

fun NavHostController.navigateWithIntArg(route: String, arg: Int) =
    this.navigateWithStringArg(route, arg.toString())

fun NavHostController.navigateWithTwoArgs(route: String, arg: Int, arg2: Int) =
    this.navigateSingleTopTo("$route/$arg/$arg2")