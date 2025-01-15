package com.franklinndiwe.insight.viewmodel

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.franklinndiwe.insight.InsightApplication
import com.franklinndiwe.insight.viewmodel.author.AllAuthorsViewModel
import com.franklinndiwe.insight.viewmodel.author.LikedAuthorsViewModel
import com.franklinndiwe.insight.viewmodel.author.PersonalAuthorsViewModel
import com.franklinndiwe.insight.viewmodel.author.PopularAuthorsViewModel
import com.franklinndiwe.insight.viewmodel.category.FreeCategoriesViewModel
import com.franklinndiwe.insight.viewmodel.category.LikedCategoriesViewModel
import com.franklinndiwe.insight.viewmodel.category.PersonalCategoriesViewModel
import com.franklinndiwe.insight.viewmodel.category.PopularCategoriesViewModel
import com.franklinndiwe.insight.viewmodel.category.PremiumCategoriesViewModel
import com.franklinndiwe.insight.viewmodel.quote.AuthorQuotesViewModel
import com.franklinndiwe.insight.viewmodel.quote.CategoryQuotesViewModel
import com.franklinndiwe.insight.viewmodel.quote.CreateQuoteViewModel
import com.franklinndiwe.insight.viewmodel.quote.ForYouQuotesViewModel
import com.franklinndiwe.insight.viewmodel.quote.LikedQuotesViewModel
import com.franklinndiwe.insight.viewmodel.quote.PersonalQuotesViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            val application =
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InsightApplication)
            val container = application.container
            SplashScreenViewModel(
                application,
                container.settingRepository,
                container.quoteImageRepository,
                container.quoteImageCategoryRepository,
                container.gradientRepository,
                container.colorRepository,
                container.fontRepository,
                container.sortOrderRepository,
                application.userPreferencesRepository
            )
        }
        initializer {
            val application =
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InsightApplication)
            val container = application.container
            HomeViewModel(
                container.quoteRepository,
                container.settingRepository,
                application.userPreferencesRepository
            )
        }
        initializer {
            val application =
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InsightApplication)
            val container = application.container
            SearchScreenViewModel(
                container.categoryRepository, container.authorRepository, container.quoteRepository
            )
        }
        initializer {
            val application =
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InsightApplication)
            val container = application.container
            FollowScreenViewModel(
                container.categoryRepository,
                container.authorRepository
            )
        }
        initializer {
            val application =
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InsightApplication)
            val container = application.container
            SettingsViewModel(
                container.categoryRepository,
                container.quoteImageCategoryRepository,
                container.settingRepository
            )
        }
        initializer {
            val application =
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InsightApplication)
            val container = application.container
            ForYouQuotesViewModel(
                container.quoteRepository,
                container.categoryRepository,
                container.authorRepository,
                container.sortOrderRepository,
                container.settingRepository,
                container.fontRepository,
                container.gradientRepository,
                container.colorRepository,
                container.quoteImageRepository,
                application.userPreferencesRepository
            )
        }
        initializer {
            val application =
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InsightApplication)
            val container = application.container
            CategoryQuotesViewModel(
                container.quoteRepository,
                container.categoryRepository,
                container.sortOrderRepository,
                container.settingRepository,
                container.fontRepository,
                container.gradientRepository,
                container.colorRepository,
                container.quoteImageRepository,
                application.userPreferencesRepository
            )
        }
        initializer {
            val application =
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InsightApplication)
            val container = application.container
            AuthorQuotesViewModel(
                container.quoteRepository,
                container.authorRepository,
                container.sortOrderRepository,
                container.settingRepository,
                container.fontRepository,
                container.gradientRepository,
                container.colorRepository,
                container.quoteImageRepository,
                application.userPreferencesRepository
            )
        }
        initializer {
            val application =
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InsightApplication)
            val container = application.container
            LikedQuotesViewModel(
                container.quoteRepository,
                container.sortOrderRepository,
                container.settingRepository,
                container.fontRepository,
                container.gradientRepository,
                container.colorRepository,
                container.quoteImageRepository,
                application.userPreferencesRepository
            )
        }
        initializer {
            val application =
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InsightApplication)
            val container = application.container
            PersonalQuotesViewModel(
                container.quoteRepository,
                container.sortOrderRepository,
                container.settingRepository,
                container.fontRepository,
                container.gradientRepository,
                container.colorRepository,
                container.quoteImageRepository,
                application.userPreferencesRepository
            )
        }
        initializer {
            val application =
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InsightApplication)
            val container = application.container
            PopularCategoriesViewModel(
                container.categoryRepository,
                container.sortOrderRepository,
                application.userPreferencesRepository,
                container.settingRepository
            )
        }
        initializer {
            val application =
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InsightApplication)
            val container = application.container
            LikedCategoriesViewModel(
                container.categoryRepository,
                container.sortOrderRepository,
                application.userPreferencesRepository,
                container.settingRepository
            )
        }
        initializer {
            val application =
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InsightApplication)
            val container = application.container
            PersonalCategoriesViewModel(
                container.categoryRepository,
                container.sortOrderRepository,
                application.userPreferencesRepository,
                container.settingRepository
            )
        }
        initializer {
            val application =
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InsightApplication)
            val container = application.container
            FreeCategoriesViewModel(
                container.categoryRepository,
                container.sortOrderRepository,
                application.userPreferencesRepository,
                container.settingRepository
            )
        }
        initializer {
            val application =
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InsightApplication)
            val container = application.container
            PremiumCategoriesViewModel(
                container.categoryRepository,
                container.sortOrderRepository,
                application.userPreferencesRepository,
                container.settingRepository
            )
        }
        initializer {
            val application =
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InsightApplication)
            val container = application.container
            PopularAuthorsViewModel(
                container.authorRepository,
                container.sortOrderRepository,
                application.userPreferencesRepository,
                container.settingRepository
            )
        }
        initializer {
            val application =
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InsightApplication)
            val container = application.container
            LikedAuthorsViewModel(
                container.authorRepository,
                container.sortOrderRepository,
                application.userPreferencesRepository,
                container.settingRepository
            )
        }
        initializer {
            val application =
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InsightApplication)
            val container = application.container
            PersonalAuthorsViewModel(
                container.authorRepository,
                container.sortOrderRepository,
                application.userPreferencesRepository,
                container.settingRepository
            )
        }
        initializer {
            val application =
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InsightApplication)
            val container = application.container
            AllAuthorsViewModel(
                container.authorRepository,
                container.sortOrderRepository,
                application.userPreferencesRepository,
                container.settingRepository
            )
        }
        initializer {
            val application =
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InsightApplication)
            val container = application.container
            ThemeEditorViewModel(
                container.colorRepository,
                container.gradientRepository,
                container.quoteImageRepository,
                container.settingRepository,
                container.fontRepository,
                application.userPreferencesRepository
            )
        }
        initializer {
            val application =
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InsightApplication)
            val container = application.container
            CreateQuoteViewModel(
                container.categoryRepository,
                container.authorRepository,
                container.quoteRepository
            )
        }
        initializer {
            val application =
                (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as InsightApplication)
            val container = application.container
            ThemeScreenViewModel(
                container.fontRepository,
                container.colorRepository,
                container.gradientRepository,
                container.quoteImageRepository,
                container.quoteImageCategoryRepository,
                application.userPreferencesRepository
            )
        }
    }
}