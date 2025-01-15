package com.franklinndiwe.insight.data

import android.content.Context
import com.franklinndiwe.insight.repository.AuthorOfflineRepository
import com.franklinndiwe.insight.repository.AuthorRepository
import com.franklinndiwe.insight.repository.CategoryOfflineRepository
import com.franklinndiwe.insight.repository.CategoryRepository
import com.franklinndiwe.insight.repository.ColorOfflineRepository
import com.franklinndiwe.insight.repository.ColorRepository
import com.franklinndiwe.insight.repository.FontOfflineRepository
import com.franklinndiwe.insight.repository.FontRepository
import com.franklinndiwe.insight.repository.GradientOfflineRepository
import com.franklinndiwe.insight.repository.GradientRepository
import com.franklinndiwe.insight.repository.QuoteImageCategoryOfflineRepository
import com.franklinndiwe.insight.repository.QuoteImageCategoryRepository
import com.franklinndiwe.insight.repository.QuoteImageOfflineRepository
import com.franklinndiwe.insight.repository.QuoteImageRepository
import com.franklinndiwe.insight.repository.QuoteOfflineRepository
import com.franklinndiwe.insight.repository.QuoteRepository
import com.franklinndiwe.insight.repository.SettingOfflineRepository
import com.franklinndiwe.insight.repository.SettingRepository
import com.franklinndiwe.insight.repository.SortOrderOfflineRepository
import com.franklinndiwe.insight.repository.SortOrderRepository

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val quoteRepository: QuoteRepository
    val authorRepository: AuthorRepository
    val categoryRepository: CategoryRepository
    val settingRepository: SettingRepository
    val quoteImageRepository: QuoteImageRepository
    val quoteImageCategoryRepository: QuoteImageCategoryRepository
    val gradientRepository: GradientRepository
    val colorRepository: ColorRepository
    val fontRepository: FontRepository
    val sortOrderRepository: SortOrderRepository
}

class AppDataContainer(context: Context) : AppContainer {
    private val database = AppDatabase.getDatabase(context)
    override val quoteRepository: QuoteRepository by lazy {
        QuoteOfflineRepository(database.quoteDao())
    }
    override val categoryRepository: CategoryRepository by lazy {
        CategoryOfflineRepository(database.categoryDao())
    }
    override val authorRepository: AuthorRepository by lazy {
        AuthorOfflineRepository(database.authorDao())
    }
    override val settingRepository: SettingRepository by lazy {
        SettingOfflineRepository(database.settingDao())
    }
    override val quoteImageRepository: QuoteImageRepository by lazy {
        QuoteImageOfflineRepository(database.quoteImageDao())
    }
    override val quoteImageCategoryRepository: QuoteImageCategoryRepository by lazy {
        QuoteImageCategoryOfflineRepository(database.quoteImageCategoryDao())
    }
    override val gradientRepository: GradientRepository by lazy {
        GradientOfflineRepository(database.gradientDao())
    }
    override val colorRepository: ColorRepository by lazy {
        ColorOfflineRepository(database.colorDao())
    }
    override val fontRepository: FontRepository by lazy {
        FontOfflineRepository(database.fontDao())
    }
    override val sortOrderRepository: SortOrderRepository by lazy {
        SortOrderOfflineRepository(database.sortOrderDao())
    }
}