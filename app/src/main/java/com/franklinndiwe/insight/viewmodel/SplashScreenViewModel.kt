package com.franklinndiwe.insight.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.franklinndiwe.insight.data.AppFont
import com.franklinndiwe.insight.data.BackgroundCoreAttributes
import com.franklinndiwe.insight.data.QuoteImage
import com.franklinndiwe.insight.data.QuoteImageCategory
import com.franklinndiwe.insight.data.Setting
import com.franklinndiwe.insight.data.UserPreferencesRepository
import com.franklinndiwe.insight.repository.ColorRepository
import com.franklinndiwe.insight.repository.FontRepository
import com.franklinndiwe.insight.repository.GradientRepository
import com.franklinndiwe.insight.repository.QuoteImageCategoryRepository
import com.franklinndiwe.insight.repository.QuoteImageRepository
import com.franklinndiwe.insight.repository.SettingRepository
import com.franklinndiwe.insight.repository.SortOrderRepository
import com.franklinndiwe.insight.utils.FontUtils.getFontInfo
import com.franklinndiwe.insight.utils.Gradients
import com.franklinndiwe.insight.utils.SolidColors
import com.franklinndiwe.insight.utils.SortOrders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import java.util.Locale

class SplashScreenViewModel(
    context: Context,
    private val settingRepository: SettingRepository,
    private val quoteImageRepository: QuoteImageRepository,
    private val quoteImageCategoryRepository: QuoteImageCategoryRepository,
    private val gradientRepository: GradientRepository,
    private val colorRepository: ColorRepository,
    private val fontRepository: FontRepository,
    private val sortOrderRepository: SortOrderRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    private val assetManager = context.assets
    private suspend fun initDatabase() = supervisorScope {
        listOf(
//            async { readCategories(context) },
//            async { readAuthors(context) },
//            async { readQuotes(context) },
            async { settingRepository.insert(Setting()) },
            async { fontRepository.insert(*assetFonts().toTypedArray()) },
            async { insertQuoteImages() },
            async {
                gradientRepository.insert(*Gradients.list.map { gradient ->
                    gradient.copy(
                        attributes = gradient.attributes.copy(
                            tintColor = gradient.attributes.tintColor
                                ?: gradient.colors[gradient.colors.lastIndex],
                            textColor = gradient.attributes.textColor ?: gradient.colors[0]
                        )
                    )
                }.toTypedArray())
            },
            async { colorRepository.insert(*SolidColors.list.toTypedArray()) },
            async { sortOrderRepository.insert(SortOrders.list) }).awaitAll()
    }

    suspend fun setupApp() = viewModelScope.async {
        withContext(Dispatchers.IO) {
            initDatabase()
            userPreferencesRepository.saveFirstInstall(false)
        }
    }
//
//    private suspend fun readJsonFile(
//        context: Context,
//        resourceId: Int,
//        function: suspend (InputStream) -> Unit,
//    ) {
//        context.resources.openRawResource(resourceId).use { inputStream ->
//            function(inputStream)
//        }
//    }
//
//    @OptIn(ExperimentalSerializationApi::class)
//    suspend fun readQuotes(context: Context) {
//        readJsonFile(context, R.raw.quotes) { inputStream ->
//            val quotes = try {
//                Json.decodeFromStream<List<JsonQuote>>(inputStream).map {
//                    Quote(
//                        id = it.id,
//                        text = it.text,
//                        authorId = it.authorId,
//                        categoryId1 = it.categoryId1,
//                        categoryId2 = it.categoryId2
//                    )
//                }
//            } catch (e: Exception) {
//                emptyList()
//            }
//            quoteRepository.insert(quotes)
//        }
//    }
//
//    private fun Int.toBoolean() = this == 1
//
//    @OptIn(ExperimentalSerializationApi::class)
//    suspend fun readCategories(context: Context) {
//        readJsonFile(context, R.raw.categories) { inputStream ->
//            val categories = try {
//                Json.decodeFromStream<List<JsonCategory>>(inputStream).map {
//                    Category(
//                        it.id,
//                        it.name,
//                        unlocked = it.unlocked.toBoolean(),
//                        popular = it.popular.toBoolean()
//                    )
//                }
//            } catch (e: Exception) {
//                emptyList()
//            }
//            categoryRepository.insert(categories)
//        }
//    }
//
//    @OptIn(ExperimentalSerializationApi::class)
//    suspend fun readAuthors(context: Context) {
//        readJsonFile(context, R.raw.authors) { inputStream ->
//            val authors = try {
//                Json.decodeFromStream<List<JsonAuthor>>(inputStream).map {
//                    Author(
//                        it.id,
//                        it.name,
//                        popular = it.popular.toBoolean()
//                    )
//                }
//            } catch (e: Exception) {
//                emptyList()
//            }
//            authorRepository.insert(authors)
//        }
//    }


    private fun assetFonts(): List<AppFont> {
        val fontFolder = "fonts"
        val fonts = mutableListOf<AppFont>()
        assetManager.list(fontFolder)?.forEach { fontFile ->
            val fileName = "$fontFolder/$fontFile"
            val font = getFontInfo(assetManager.open(fileName), fileName)
            if (font != null) fonts += font
        }
        return fonts
    }

    private fun assetImages(): List<Pair<String, List<String>>> {
        val imageFolder = "images"
        val categoriesWithImages = mutableListOf<Pair<String, List<String>>>()
        //Get all the image folders
        assetManager.list(imageFolder)?.filter { !it.contains(".") }?.forEach { categoryFolder ->
            val folderPath = "$imageFolder/$categoryFolder"
            //Loop through the folder to get the images
            val imageList = assetManager.list(folderPath)
            val listOfPaths =
                List(imageList!!.size) { "file:///android_asset/$folderPath/${imageList[it]}" }
            categoriesWithImages.add(Pair(categoryFolder.replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.getDefault()
                ) else it.toString()
            }.replace("_", " "), listOfPaths))
        }
        return categoriesWithImages
    }

    private suspend fun insertQuoteImages() = assetImages().map {
        Pair(
            QuoteImageCategory(name = it.first), it.second
        )
    }.forEach { imageCategoryListPair ->
        quoteImageCategoryRepository.insert(imageCategoryListPair.first)
        val range = 1..2
        val images = imageCategoryListPair.second.map {
            QuoteImage(
                path = it,
                categoryName = imageCategoryListPair.first.name,
                attributes = BackgroundCoreAttributes(
                    unlocked = imageCategoryListPair.second.indexOf(it) in range
                )
            )
        }
        quoteImageRepository.insert(*images.toTypedArray())
    }
}