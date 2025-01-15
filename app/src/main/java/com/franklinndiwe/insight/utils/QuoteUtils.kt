package com.franklinndiwe.insight.utils

import android.content.Context
import android.content.Intent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import com.franklinndiwe.insight.R
import com.franklinndiwe.insight.data.Quote
import com.franklinndiwe.insight.data.QuoteV2
import com.franklinndiwe.insight.data.SortOrder
import com.franklinndiwe.insight.repository.QuoteRepository
import com.franklinndiwe.insight.utils.AppUtils.annotatedString
import com.franklinndiwe.insight.utils.BitmapUtils.saveToDisk
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuoteUtils(
    private val viewModelScope: CoroutineScope,
    private val quoteRepository: QuoteRepository,
) {

    private fun getQuotes(
        pageSize: Int = 10,
        prefetchDistance: Int = pageSize,
        enablePlaceholders: Boolean = false,
        initialLoadSize: Int = pageSize * 3,
        maxSize: Int = Int.MAX_VALUE,
        jumpThreshold: Int = Int.MIN_VALUE,
        pagingSourceFactory: () -> PagingSource<Int, QuoteV2>,
    ): Flow<PagingData<QuoteV2>> {
        return Pager(
            PagingConfig(
                pageSize,
                prefetchDistance,
                enablePlaceholders,
                initialLoadSize,
                maxSize,
                jumpThreshold
            )
        ) {
            pagingSourceFactory()
        }.flow.flowOn(Dispatchers.IO).cachedIn(viewModelScope)
    }

    fun getQuoteById(id: Int) = quoteRepository.getQuoteById(id).flowOn(Dispatchers.IO)
    suspend fun existingQuote(quote: String) =
        quoteRepository.checkForExistingQuote(quote).first() > 0

    suspend fun insertQuote(quote: Quote) = viewModelScope.launch { quoteRepository.insert(quote) }

    fun getQuotesForCategory(categoryId: Int, sortOrder: SortOrder) = getQuotes {
        quoteRepository.getQuotesForCategory(
            categoryId, sortOrder.sortBy, sortOrder.order
        )
    }

    fun getQuotesForAuthor(authorId: Int, sortOrder: SortOrder) =
        getQuotes {
            quoteRepository.getQuotesForAuthor(
                authorId, sortOrder.sortBy, sortOrder.order
            )
        }

    fun getLikedQuotes(sortOrder: SortOrder) = getQuotes {
        quoteRepository.getLikedQuotes(
            sortOrder.sortBy, sortOrder.order
        )
    }

    fun getPersonalQuotes(sortOrder: SortOrder) = getQuotes {
        quoteRepository.getUserQuotes(
            sortOrder.sortBy, sortOrder.order
        )
    }

    fun searchQuotes(query: String) =
        getQuotes { quoteRepository.searchQuotes(String.format("*%s*", query)) }

    fun getRecommendedQuotes() = quoteRepository.recommendedQuotes()

    fun updateQuote(quote: QuoteV2) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { quoteRepository.update(quote.quote) }
        }
    }

    fun deleteQuote(quote: Quote) = viewModelScope.launch(Dispatchers.IO) {
        quoteRepository.delete(quote)
    }

    fun shareQuote(context: Context, quote: QuoteV2) {
        // Convert AnnotatedString to plain text
        val plainText = annotatedString(quote).toString()

        // Share the plain text using an Intent
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, plainText)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, null))
    }

    @OptIn(ExperimentalPermissionsApi::class)
    fun saveBitmapFromComposable(
        context: Context,
        snackbarHostState: SnackbarHostState,
        writeStorageAccessState: MultiplePermissionsState,
        graphicsLayer: GraphicsLayer,
        deductAction: unit,
    ) {
        if (writeStorageAccessState.allPermissionsGranted) {
            viewModelScope.launch {
                val uri = graphicsLayer.toImageBitmap().asAndroidBitmap().saveToDisk(context)
                snackbarHostState.showSnackbar(
                    if (uri != null) context.getString(
                        R.string.file_saved_to, context.getString(R.string.pictures)
                    ) else context.getString(
                        R.string.image_save_error
                    )
                )
                if (uri != null) deductAction()
            }
        } else if (writeStorageAccessState.shouldShowRationale) {
            viewModelScope.launch {
                val result = snackbarHostState.showSnackbar(
                    context.getString(R.string.storage_permission_message),
                    actionLabel = context.getString(
                        R.string.grant_access
                    )
                )
                if (result == SnackbarResult.ActionPerformed) {
                    writeStorageAccessState.launchMultiplePermissionRequest()
                }
            }
        } else writeStorageAccessState.launchMultiplePermissionRequest()
    }
}