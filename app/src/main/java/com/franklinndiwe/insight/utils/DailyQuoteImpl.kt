package com.franklinndiwe.insight.utils

import android.os.Build
import com.franklinndiwe.insight.data.QuoteV2
import com.franklinndiwe.insight.repository.QuoteRepository
import com.franklinndiwe.insight.repository.SettingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.Calendar

class DailyQuoteImpl(
    private val quoteRepository: QuoteRepository,
    private val settingRepository: SettingRepository,
) {

    private val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()
    }
    private val date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) LocalDate.now()
        .toString() else calendar.time.toString()

    suspend fun generateQuoteOfTheDay(): QuoteV2? = withContext(Dispatchers.IO) {
        val setting = settingRepository.getSetting().first()
        var quoteV2: QuoteV2? = null
        if (date != setting.dateOfLastQuote) {
            val quote = async { quoteRepository.dailyQuote().first() }.await()
            if (quote != null) {
                async {
                    settingRepository.update(
                        setting.copy(
                            dailyQuote = quote.quote.id, dateOfLastQuote = date
                        )
                    )
                }.await()
            }
            quoteV2 = quote
        } else setting.dailyQuote?.let { i ->
            quoteV2 = async { quoteRepository.getQuoteById(i).first() }.await()
        }
        return@withContext quoteV2
    }
}